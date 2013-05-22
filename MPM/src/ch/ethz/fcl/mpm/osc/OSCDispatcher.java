/*
Copyright (c) 2001-2013, Corebounce Association (Pascal Mueller, Stefan Mueller Arisona, Simon Schubiger, Matthias Specht)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the <organization> nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package ch.ethz.fcl.mpm.osc;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OSCDispatcher {
	private static final boolean DBG = true;

	private static final OSCHandler DEFAULT_HANDLER = new OSCHandler() {
		@Override
		public Object[] handle(String[] address, int addrIdx, StringBuilder typeString, long timetag, Object... args) {
			if (DBG) {
				System.out.print("[");
				for (int i = 0; i < address.length; i++)
					System.out.print((i == addrIdx ? ':' : '/') + address[i]);
				System.out.print("(" + typeString + ")@" + timetag + ":");
				for (Object o : args) {
					if (o instanceof String)
						System.out.print('"' + o.toString() + '"');
					else if (o instanceof byte[])
						System.out.print('{' + OSCCommon.toHex((byte[]) o) + '}');
					else
						System.out.print(o + " ");
				}
				System.out.println("]");
			}
			return null;
		}
	};

	private OSCNode addressSpace = new OSCNode(DEFAULT_HANDLER);
	protected int messageCount;

	protected OSCDispatcher() {
	}

	public void installHandler(String address, OSCHandler handler) {
		if (address.equals("/"))
			addressSpace.setHandler(handler);
		else {
			String[] parts = OSCCommon.split(address, '/');
			OSCNode node = addressSpace;
			for (int i = 1; i < parts.length - 1; i++)
				node = node.get(parts[i]);
			node.get(parts[parts.length - 1]).setHandler(handler);
		}
	}

	protected void process(SocketAddress peer, ByteBuffer packet, long timetag, OSCSender sender) {
		StringBuilder address = new StringBuilder();
		for (;;) {
			char c = (char) packet.get();
			if (c == 0)
				break;
			address.append(c);
		}
		OSCCommon.align(packet);
		dispatch(peer, address.toString(), packet, timetag, sender);
	}

	private void dispatch(SocketAddress peer, String address, ByteBuffer packet, long timetag, OSCSender sender) {
		if (address.equals("#bundle")) {
			timetag = packet.getLong();
			while (packet.position() < packet.limit()) {
				packet.getInt(); // skip size
				process(peer, packet, timetag, sender);
			}
		} else {
			messageCount++;
			int messageStart = packet.position() - (4 * ((address.length() / 4) + 1));
			StringBuilder typeString = new StringBuilder();
			for (;;) {
				char c = (char) packet.get();
				if (c == 0)
					break;
				typeString.append(c);
			}
			OSCCommon.align(packet);
			Object[] args = new Object[typeString.length() - 1];
			for (int i = 1; i < typeString.length(); i++) {
				switch (typeString.charAt(i)) {
				case 'i':
					args[i - 1] = packet.getInt();
					break;
				case 'f':
					args[i - 1] = packet.getFloat();
					break;
				case 'd':
					args[i - 1] = packet.getDouble();
					break;
				case 's':
					StringBuilder tmp = new StringBuilder();
					for (;;) {
						char c = (char) packet.get();
						if (c == 0)
							break;
						tmp.append(c);
					}
					args[i - 1] = tmp.toString();
					OSCCommon.align(packet);
					break;
				case 'T':
					args[i - 1] = true;
					break;
				case 'F':
					args[i - 1] = false;
					break;
				case 'N':
					args[i - 1] = null;
					break;
				case 'b':
					int size = packet.getInt();
					byte[] blob = new byte[size];
					packet.get(blob);
					args[i - 1] = blob;
					OSCCommon.align(packet);
					break;
				default:
					throw new IllegalArgumentException("Illegal type string: '" + typeString.charAt(i) + "' in " + typeString);
				}
			}
			int messageEnd = packet.position();

			OSCNode node = addressSpace;
			OSCHandler handler = node.getHandler();
			String[] parts = OSCCommon.split(address, '/');
			int idx = 0;
			for (int i = 1; i < parts.length; i++) {
				OSCNode tmp = node.lookup(parts[i]);
				if (tmp != null) {
					if (tmp.getHandler() != null) {
						handler = tmp.getHandler();
						idx = i;
					}
					node = tmp;
				} else
					break;
			}

			ByteBuffer msg = null;
			try {
				Object[] reply = handler.handle(parts, idx + 1, typeString, timetag, args);
				if (reply != null) {
					byte[] request = new byte[messageEnd - messageStart];
					packet.position(messageStart);
					packet.get(request);
					msg = OSCMessage.getBytes("#reply", request, reply);
				}
			} catch (OSCError err) {
				byte[] request = new byte[messageEnd - messageStart];
				packet.position(messageStart);
				packet.get(request);
				msg = OSCMessage.getBytes("#error", request, err.getDocumentation());
			}

			if (msg != null) {
				try {
					sender.send(peer, msg);
				} catch (IOException ex) {
					OSCCommon.handleException(ex, OSCDispatcher.this);
				}
			}
		}
	}

	public int getMessageCount() {
		return messageCount;
	}

	class OSCNode {
		private ConcurrentHashMap<String, OSCNode> children;
		private OSCHandler handler;

		public OSCNode() {
		}

		public OSCNode(OSCHandler handler) {
			setHandler(handler);
		}

		public OSCHandler getHandler() {
			return handler;
		}

		public OSCNode lookup(String name) {
			return children == null ? null : children.get(name);
		}

		public OSCNode get(String name) {
			if (children == null)
				children = new ConcurrentHashMap<String, OSCNode>();
			OSCNode result = children.get(name);
			if (result == null) {
				result = new OSCNode();
				children.put(name, result);
			}
			return result;
		}

		public void setHandler(OSCHandler handler) {
			this.handler = handler;
		}
	}
}
