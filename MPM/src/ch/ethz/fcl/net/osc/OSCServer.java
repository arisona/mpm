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
package ch.ethz.fcl.net.osc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.SwingUtilities;

import ch.ethz.fcl.net.util.AddressUtilities;

public final class OSCServer extends OSCDispatcher implements OSCSender {
	private static final int RECEIVE_BUFFER_SIZE = 1024 * 1024;
	private static final int SEND_BUFFER_SIZE = 1024 * 1024;

	private final InetSocketAddress address;
	private final DatagramSocket socket;

	private final BlockingQueue<DatagramPacket> receiveQueue = new LinkedBlockingQueue<DatagramPacket>();
	private final BlockingQueue<DatagramPacket> sendQueue = new LinkedBlockingQueue<DatagramPacket>();

	private final AtomicBoolean awtPending = new AtomicBoolean();

	private final Map<String, SocketAddress> remotePeers = new HashMap<String, SocketAddress>();

	public OSCServer(int port, String multicastAddress) throws UnknownHostException, IOException {
		address = new InetSocketAddress(AddressUtilities.getDefaultInterface(), port);
		if (multicastAddress == null) {
			socket = new DatagramSocket(address.getPort());
		} else {
			MulticastSocket multicastSocket = new MulticastSocket(address.getPort());
			multicastSocket.joinGroup(InetAddress.getByName(multicastAddress));
			socket = multicastSocket;
		}
		int dec = socket.getReceiveBufferSize();
		for (int size = RECEIVE_BUFFER_SIZE; socket.getReceiveBufferSize() < size; size -= dec) {
			socket.setReceiveBufferSize(size);
		}
		dec = socket.getSendBufferSize();
		for (int size = SEND_BUFFER_SIZE; socket.getSendBufferSize() < size; size -= dec) {
			socket.setSendBufferSize(size);
		}

		final Runnable awtHandler = new Runnable() {
			@Override
			public void run() {
				try {
					while (!receiveQueue.isEmpty()) {
						DatagramPacket packet = receiveQueue.take();
						ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
						process(packet.getSocketAddress(), buffer, OSCCommon.TIMETAG_IMMEDIATE, OSCServer.this);
					}
				} catch (Exception ex) {
					OSCCommon.handleException(ex, this);
				}
				awtPending.set(false);
			}
		};

		final Thread receiveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					try {
						byte[] buffer = new byte[socket.getReceiveBufferSize()];
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						receiveQueue.add(packet);
						if (!awtPending.getAndSet(true))
							SwingUtilities.invokeLater(awtHandler);
					} catch (Exception e) {
					}
				}
			}
		});
		receiveThread.setDaemon(true);
		receiveThread.setPriority(Thread.MAX_PRIORITY);
		receiveThread.start();

		final Thread sendThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// every 10 packets wait 10ms to avoid UDP packet drops.
				int count = 10;
				int i = count;
				for (;;) {
					try {
						DatagramPacket packet = sendQueue.take();
						socket.send(packet);
						if (i-- == 0) {
							i = count;
							Thread.sleep(10);
						}
					} catch (Exception e) {
					}
				}
			}
		});
		sendThread.setDaemon(true);
		sendThread.start();

		OSCCommon.setExceptionHandler(new ExceptionHandler() {
			@Override
			public void exception(Throwable t, Object source) {
				System.out.println(source == null ? "OSC Exception (without source)" : "OSC Exception: " + source.toString());
			}
		});
	}

	public void send(String address, Object... args) throws IOException {
		ByteBuffer packet = OSCMessage.getBytes(address, args);
		for (SocketAddress destination : remotePeers.values()) {
			send(destination, packet);
		}
	}

	@Override
	public void send(SocketAddress destination, ByteBuffer packet) throws IOException {
		DatagramPacket p = new DatagramPacket(packet.array(), packet.capacity());
		p.setSocketAddress(destination);
		sendQueue.add(p);
	}
}
