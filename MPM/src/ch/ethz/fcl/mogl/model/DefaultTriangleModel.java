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

public class DefaultTriangleModel implements ITriangleModel {
	private float[] faces;
	private float[] normals;
	private float[] colors;
	private Vector3D extentMin = new Vector3D(0, 0, 0);
	private Vector3D extentMax = new Vector3D(0, 0, 0);

	public DefaultTriangleModel() {
	}

	@Override
	public Vector3D getExtentMin() {
		return extentMin;
	}

	@Override
	public Vector3D getExtentMax() {
		return extentMax;
	}

	@Override
	public float[] getFaces() {
		return faces;
	}

	@Override
	public float[] getNormals() {
		if (normals == null) {
			normals = ModelUtils.calculateNormals(getFaces());
		}
		return normals;
	}

	@Override
	public float[] getColors() {
		return colors;
	}

	@Override
	public void setTriangles(float[] faces, float[] colors) {
		this.faces = faces;
		this.colors = colors;
		this.normals = null;

		float[] extent = new float[] { Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY };
		for (int i = 0; i < faces.length; i += 3) {
			extent[0] = Math.max(extent[0], faces[i]);
			extent[1] = Math.min(extent[1], faces[i]);
			extent[2] = Math.max(extent[2], faces[i+1]);
			extent[3] = Math.min(extent[3], faces[i+1]);
			extent[4] = Math.max(extent[4], faces[i+2]);
			extent[5] = Math.min(extent[5], faces[i+2]);
		}
		extentMin = new Vector3D(extent[0], extent[2], extent[4]);
		extentMax = new Vector3D(extent[1], extent[3], extent[5]);
	}
}
