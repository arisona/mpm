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

public class OSCCommon {
	public static final long TIMETAG_IMMEDIATE = 1;

	static final String HEX = "0123456789ABCDEF";

	static ExceptionHandler handler = new ExceptionHandler() {
		@Override
		public void exception(Throwable t, Object source) {
			System.err.println("### " + source);
			t.printStackTrace();
		}
	};

	public static String toHex(ByteBuffer buffer) {
		StringBuilder result = new StringBuilder();
		int pos = buffer.position();
		buffer.position(0);

		for (int i = buffer.limit(); --i >= 0;) {
			byte b = buffer.get();
			result.append(HEX.charAt((b >> 4) & 0xF));
			result.append(HEX.charAt(b & 0xF));
			result.append(' ');
		}

		buffer.position(pos);

		return result.toString();
	}

	public static String toHex(byte[] buffer) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < buffer.length; i++) {
			result.append(HEX.charAt((buffer[i] >> 4) & 0xF));
			result.append(HEX.charAt(buffer[i] & 0xF));
			result.append(' ');
		}

		return result.toString();
	}

	public final static void setExceptionHandler(ExceptionHandler ehandler) {
		handler = ehandler;
	}

	final static void handleException(Throwable t, Object source) {
		handler.exception(t, source);
	}

	final static void append(ByteBuffer buffer, String s) {
		int len = s.length();
		for (int i = 0; i < len; i++)
			buffer.put((byte) s.charAt(i));
		buffer.put((byte) 0);
		align(buffer);
	}

	final static void append(ByteBuffer buffer, byte[] data) {
		buffer.putInt(data.length);
		buffer.put(data);
		align(buffer);
	}

	final static void append(ByteBuffer buffer, Object o) {
		if (o instanceof Integer)
			buffer.putInt((Integer) o);
		else if (o instanceof Float)
			buffer.putFloat((Float) o);
		else if (o instanceof Double)
			buffer.putDouble((Double) o);
		else if (o instanceof String)
			append(buffer, (String) o);
		else if (o instanceof byte[])
			append(buffer, (byte[]) o);
		else if (o instanceof Boolean) {
			// Nothing to do. The value (true or false) is already sent in the
			// type string
		} else if (o == null) {
			// Nothing to do. The type string alreday specifies a null
		} else
			throw new IllegalArgumentException("Unsupported OSC Type:" + o.getClass().getName());
	}

	final static void align(ByteBuffer buffer) {
		buffer.position(((buffer.position() + 3) / 4) * 4);
	}

	private static final String[] EMPTY_STR_A = new String[0];
	private static final String EMPTY_STR = "";

	public static String[] split(String str, char splitchar) {
		if (str == null)
			return EMPTY_STR_A;

		int len = str.length();
		int count = 1;
		for (int i = 0; i < len; i++)
			if (str.charAt(i) == splitchar)
				count++;

		String[] result = new String[count];

		count = 0;
		int start = 0;
		for (int i = 0; i < len; i++)
			if (str.charAt(i) == splitchar) {
				result[count++] = start == i ? EMPTY_STR : str.substring(start, i);
				start = i + 1;
			}
		result[count] = str.substring(start, len);

		return result;
	}
}
