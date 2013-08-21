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
package ch.ethz.fcl.mogl.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public final class ModelUtils {
	// @formatter:off
	public static final float[] UNIT_CUBE_FACES = {
		1, 1, 0, 0, 1, 0, 1, 1, 1,
		0, 1, 0, 0, 1, 1, 1, 1, 1,

		0, 1, 0, 0, 0, 0, 0, 0, 1,
		0, 1, 0, 0, 0, 1, 0, 1, 1,

		0, 0, 0, 1, 0, 0, 1, 0, 1,
		0, 0, 0, 1, 0, 1, 0, 0, 1,
		
		1, 0, 0, 1, 1, 0, 1, 1, 1,
		1, 0, 0, 1, 1, 1, 1, 0, 1,
		
		0, 0, 1, 1, 0, 1, 1, 1, 1,
		0, 0, 1, 1, 1, 1, 0, 1, 1,
	};
	// @formatter:on

	public static float[] calculateNormals(float[] faces) {
		float[] normals = new float[faces.length];
		for (int i = 0; i < faces.length; i += 9) {
			Vector3D n = null;
			try {
				Vector3D a = new Vector3D(faces[i + 3] - faces[i + 0], faces[i + 4] - faces[i + 1], faces[i + 5] - faces[i + 2]);
				Vector3D b = new Vector3D(faces[i + 6] - faces[i + 0], faces[i + 7] - faces[i + 1], faces[i + 8] - faces[i + 2]);
				n = Vector3D.crossProduct(a, b).normalize();
			} catch (Exception e) {
				n = new Vector3D(0, 0, 1);
			}
			normals[i + 0] = normals[i + 3] = normals[i + 6] = (float) n.getX();
			normals[i + 1] = normals[i + 4] = normals[i + 7] = (float) n.getY();
			normals[i + 2] = normals[i + 5] = normals[i + 8] = (float) n.getZ();
		}
		return normals;
	}

	public static void addCube(float[] destination, int index, float tx, float ty, float sx, float sy, float sz) {
		int o = index * UNIT_CUBE_FACES.length;
		int i = 0;
		for (int j = 0; j < UNIT_CUBE_FACES.length; j += 3) {
			destination[o + i++] = (UNIT_CUBE_FACES[j] * sx) + tx;
			destination[o + i++] = (UNIT_CUBE_FACES[j + 1] * sy) + ty;
			destination[o + i++] = (UNIT_CUBE_FACES[j + 2] * sz);
		}
	}

}
