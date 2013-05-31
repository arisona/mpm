/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.fcl.mpm;

import java.util.prefs.Preferences;

public class PreferencesStore {
	private static Preferences preferences;

	public static Preferences get() {
		if (preferences == null)
			preferences = Preferences.userRoot().node(PreferencesStore.class.getName());
		return preferences;
	}

	/**
	 * Stores a 4x4 matrix to the preferences store with given key.
	 * 
	 * @param key
	 *            key for the matrix to store
	 * @param matrix
	 *            matrix (4x4) to be stored
	 */
	public void putMatrix4x4(String key, double[] matrix) {
		assert (matrix.length == 16);
		get().putBoolean(key + "_matrix", true);
		for (int i = 0; i < matrix.length; ++i)
			get().putDouble(key + "_" + i, matrix[i]);
	}

	/**
	 * Reads a 4x4 matrix from preferences store with given key.
	 * 
	 * @param key
	 *            key for the matrix to read
	 * @return the stored matrix or null if no matrix exists in store for given
	 *         key.
	 */
	public double[] getMatrix4x4(String key) {
		if (!get().getBoolean(key + "_matrix", false))
			return null;

		double[] matrix = new double[16];
		for (int i = 0; i < matrix.length; ++i) {
			matrix[i] = get().getDouble(key + "_" + i, 0.0);
		}
		return matrix;
	}
}
