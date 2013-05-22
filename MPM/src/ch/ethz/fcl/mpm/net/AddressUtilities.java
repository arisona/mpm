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
package ch.ethz.fcl.mpm.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class AddressUtilities {
	private static final int[][] PRIVATE_ADDRS = { { 10 }, { 192, 168 }, { 172, 16 }, { 172, 17 }, { 172, 18 }, { 172, 19 }, { 172, 20 }, { 172, 21 },
			{ 172, 22 }, { 172, 23 }, { 172, 24 }, { 172, 25 }, { 172, 26 }, { 172, 27 }, { 172, 28 }, { 172, 29 }, { 172, 30 }, { 172, 31 }, };

	public static InetAddress getDefaultInterface() throws UnknownHostException, SocketException {
		InetAddress addr = getFirstNonLoopbackAddress(true);
		if (addr != null)
			return addr;
		return getLocalHost(true);
	}

	// TODO: can be removed when with JDK1.7
	public static InetAddress getLoopBackAddress() throws UnknownHostException {
		return InetAddress.getByName("127.0.0.1");
	}

	private static InetAddress getFirstNonLoopbackAddress(boolean ipv4only) throws SocketException {
		InetAddress[] addrs = AddressUtilities.getLocalAddresses(ipv4only);
		InetAddress result = null;
		for (int i = 0; i < addrs.length; i++) {
			if (!addrs[i].isLoopbackAddress()) {
				result = addrs[i];
				if (isPrivate(result))
					break;
			}
		}
		return result;
	}

	private static InetAddress getLocalHost(boolean ipv4only) throws UnknownHostException, SocketException {
		InetAddress result = InetAddress.getLocalHost();
		if (ipv4only && !(result instanceof Inet4Address)) {
			InetAddress[] addrs = AddressUtilities.getLocalAddresses(ipv4only);
			for (int i = 0; i < addrs.length; i++) {
				result = addrs[i];
				if (isPrivate(result))
					break;
			}
		}
		return result;
	}

	private static InetAddress[] getLocalAddresses(boolean ipv4only) throws SocketException {
		ArrayList<InetAddress> result = new ArrayList<InetAddress>();
		for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
			NetworkInterface nif = e.nextElement();
			for (Enumeration<InetAddress> en = nif.getInetAddresses(); en.hasMoreElements();) {
				InetAddress addr = en.nextElement();
				if (ipv4only && !(addr instanceof Inet4Address))
					continue;
				result.add(addr);
			}
		}
		return result.toArray(new InetAddress[result.size()]);
	}

	private static boolean isPrivate(InetAddress addr) {
		byte[] addrb = addr.getAddress();
		for (int i = PRIVATE_ADDRS.length; --i >= 0;) {
			boolean valid = true;
			for (int j = 0; j < PRIVATE_ADDRS[i].length; j++)
				if (PRIVATE_ADDRS[i][j] != (addrb[j] & 0xFF)) {
					valid = false;
					break;
				}
			if (valid)
				return true;
		}
		return false;
	}
}
