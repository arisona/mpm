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
package ch.ethz.fcl.mpm.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class SGModel extends AbstractModel {
	
	private static final float N = M;
	private static final float NN = N*2;
		
	// calibration points and edges (of the 0.4 x 0.4 x 0.4 metre calibration box)
	
	private final float[] MODEL_VERTICES = {
		M*2, M*2, 0,
		-M*2, M*2, 0,
		-M*2, -M*2, 0,
		M*2, -M*2, 0,

		M*2, M*2, M*2*2,
		-M*2, M*2, M*2*2,
		-M*2, -M*2, M*2*2,
		M*2, -M*2, M*2*2,

		M*3, M*3, 0,
		-M*3, M*3, 0,
		-M*3, -M*3, 0,
		M*3, -M*3, 0,

		M*5, M*5, 0,
		-M*5, M*5, 0,
		-M*5, -M*5, 0,
		M*5, -M*5, 0,
	};

	private static final float[] MODEL_EDGES = {
		// bottom
		M*2, M*2, 0, -M*2, M*2, 0,
		-M*2, M*2, 0, -M*2, -M*2, 0,
		-M*2, -M*2, 0, M*2, -M*2, 0,
		M*2, -M*2, 0, M*2, M*2, 0,
		
		// top
		M*2, M*2, M*2*2, -M*2, M*2, M*2*2,
		-M*2, M*2, M*2*2, -M*2, -M*2, M*2*2,
		-M*2, -M*2, M*2*2, M*2, -M*2, M*2*2,
		M*2, -M*2, M*2*2, M*2, M*2, M*2*2,
		
		// side
		M*2, M*2, 0, M*2, M*2, M*2*2,
		-M*2, M*2, 0, -M*2, M*2, M*2*2,
		-M*2, -M*2, 0, -M*2, -M*2, M*2*2,
		M*2, -M*2, 0, M*2, -M*2, M*2*2
	};
	
	
	private static final float[] UNIT_CUBE_FACES = {
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
	
	
	// default 0.2 x 0.2 x 0.2m box & colors
	
	private float[] faces = {
		N, N, 0, -N, N, 0, N, N, NN,
		-N, N, 0, -N, N, NN, N, N, NN,

		-N, N, 0, -N, -N, 0, -N, -N, NN,
		-N, N, 0, -N, -N, NN, -N, N, NN,

		-N, -N, 0, N, -N, 0, N, -N, NN,
		-N, -N, 0, N, -N, NN, -N, -N, NN,
		
		N, -N, 0, N, N, 0, N, N, NN,
		N, -N, 0, N, N, NN, N, -N, NN,
		
		-N, -N, NN, N, -N, NN, N, N, NN,
		-N, -N, NN, N, N, NN, -N, N, NN,
	};
	
	private float[] normals = null;
	private float[] colors = {
		1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1,

		1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1,

		1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1,

		1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1,

		1, 1, 1, 1, 1, 1, 1, 1, 1,
		1, 1, 1, 1, 1, 1, 1, 1, 1,
	};
	
	@Override
	public void reset() {
		float[] faces = new float[4 * UNIT_CUBE_FACES.length];
		addCube(faces, 0, -0.3f, -0.3f, 0.1f, 0.1f, 0.1f);
		addCube(faces, 1, 0.1f, -0.2f, 0.2f, 0.1f, 0.2f);
		addCube(faces, 2, 0f, 0f, 0.1f, 0.2f, 0.1f);
		addCube(faces, 3, 0.2f, 0.1f, 0.1f, 0.1f, 0.2f);
		setTriangles(faces, null);
	}
	
	@Override
	public float[] getModelVertices() {
		return MODEL_VERTICES;
	}

	@Override
	public float[] getModelEdges() {
		return MODEL_EDGES;
	}
	
	@Override
	public float[] getModelFaces() {
		return faces;
	}

	@Override
	public float[] getModelNormals() {
		float[] v = getModelFaces();
		if (normals == null) {
			normals = new float[v.length];
			for (int i = 0; i < v.length; i+=9) {
				Vector3D n = null;
				try {
					Vector3D a = new Vector3D(v[i + 3] - v[i + 0], v[i + 4] - v[i + 1], v[i + 5] - v[i + 2]);
					Vector3D b = new Vector3D(v[i + 6] - v[i + 0], v[i + 7] - v[i + 1], v[i + 8] - v[i + 2]);
					n = Vector3D.crossProduct(a, b).normalize();
				} catch (Exception e) {
					n = new Vector3D(0, 0, 1);
				}
				normals[i + 0] = normals[i + 3] = normals[i + 6] = (float)n.getX();
				normals[i + 1] = normals[i + 4] = normals[i + 7] = (float)n.getY();
				normals[i + 2] = normals[i + 5] = normals[i + 8] = (float)n.getZ();
			}
		}
		return normals;
	}
	
	@Override
	public float[] getModelColors() {
		return colors;
	}
	
	@Override
	public void setTriangles(float[] faces, float[] colors) {
		this.faces = faces;
		this.colors = colors;
		this.normals = null;
	}
	
	private void addCube(float[] destination, int index, float tx, float ty, float sx, float sy, float sz) {
		int o = index * UNIT_CUBE_FACES.length;
		int i = 0;
		for (int j = 0; j < UNIT_CUBE_FACES.length; j+=3) {
			destination[o + i++] = (UNIT_CUBE_FACES[j] * sx) + tx;
			destination[o + i++] = (UNIT_CUBE_FACES[j+1] * sy) + ty;
			destination[o + i++] = (UNIT_CUBE_FACES[j+2] * sz);
		}
	}
}
