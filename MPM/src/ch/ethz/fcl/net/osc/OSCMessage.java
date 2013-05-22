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

import java.nio.ByteBuffer;

public class OSCMessage {
	private static int buildTypeTagString(byte[] typeTag, int ttidx, int size, Object[] args) {
		for (Object o : args) {
			if (o instanceof Integer) {
				size++;
				typeTag[ttidx++] = 'i';
			} else if (o instanceof Float) {
				size++;
				typeTag[ttidx++] = 'f';
			} else if (o instanceof Double) {
				size += 2;
				typeTag[ttidx++] = 'd';
			} else if (o instanceof String) {
				size += (((String) o).length() / 4) + 1;
				typeTag[ttidx++] = 's';
			} else if (o instanceof byte[]) {
				size += ((((byte[]) o).length + 3) / 4) + 1;
				typeTag[ttidx++] = 'b';
			} else if (o instanceof Boolean) {
				boolean value = ((Boolean) o).booleanValue();
				if (value)
					typeTag[ttidx++] = 'T';
				else
					typeTag[ttidx++] = 'F';
			} else if (o == null) {
				typeTag[ttidx++] = 'N';
			} else
				throw new IllegalArgumentException("Unsupported OSC Type:" + o.getClass().getName());
		}
		return size;
	}

	// generic case
	static public ByteBuffer getBytes(String address, Object... args) {
		int size = (address.length() / 4) + 1;

		byte[] typeTag = new byte[args.length + 1];
		typeTag[0] = ',';
		size = buildTypeTagString(typeTag, 1, size, args);
		size += (typeTag.length / 4) + 1;

		ByteBuffer result = header(size, address, typeTag);

		for (Object o : args)
			OSCCommon.append(result, o);
		return result;
	}

	// generic case for reply
	static public ByteBuffer getBytes(String address, byte[] request, Object[] args) {
		int size = (address.length() / 4) + 1;

		byte[] typeTag = new byte[args.length + 2];
		typeTag[0] = ',';
		typeTag[1] = 'b';
		size += ((request.length + 3) / 4) + 1;
		size = buildTypeTagString(typeTag, 2, size, args);
		size += (typeTag.length / 4) + 1;

		ByteBuffer result = header(size, address, typeTag);

		OSCCommon.append(result, request);
		for (Object o : args)
			OSCCommon.append(result, o);
		return result;
	}

	final private static ByteBuffer header(int size, String address, byte[] typeTag) {
		ByteBuffer result = ByteBuffer.allocate(size * 4);

		OSCCommon.append(result, address);
		result.put(typeTag);
		result.put((byte) 0); // End of type tag string
		OSCCommon.align(result);

		return result;
	}
}
