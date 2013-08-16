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
package ch.ethz.fcl.mogl.mapping;


public class BoxCalibrationModel implements ICalibrationModel {
	private float[] calibrationVertices;
	private float[] calibrationLines;
	private float boxExtentX;
	private float boxExtentY;
	private float boxExtentZ;
	private float planeExtentX;
	private float planeExtentY;

	public BoxCalibrationModel(float boxExtentX, float boxExtentY, float boxExtentZ, float planeExtentX, float planeExtentY) {
		this.boxExtentX = boxExtentX;
		this.boxExtentY = boxExtentY;
		this.boxExtentZ = boxExtentZ;
		this.planeExtentX = planeExtentX;
		this.planeExtentY = planeExtentY;
	}

	@Override
	public float[] getCalibrationVertices() {
		if (calibrationVertices == null) {
			float bx = boxExtentX / 2;
			float by = boxExtentY / 2;
			float bz = boxExtentZ;
			float px = planeExtentX / 2;
			float py = planeExtentY / 2;
			calibrationVertices = new float[] { bx, by, 0, -bx, by, 0, -bx, -by, 0, bx, -by, 0, bx, by, bz, -bx, by, bz, -bx, -by, bz, bx, -by, bz, px, py, 0,
					-px, py, 0, -px, -py, 0, px, -py, 0 };

		}
		return calibrationVertices;
	}

	@Override
	public float[] getCalibrationLines() {
		if (calibrationLines == null) {
			float dx = boxExtentX / 2;
			float dy = boxExtentY / 2;
			float dz = boxExtentZ;
			calibrationLines = new float[] {
					// bottom
					dx, dy, 0, -dx, dy, 0, -dx, dy, 0, -dx, -dy, 0, -dx, -dy, 0, dx, -dy, 0, dx, -dy, 0, dx, dy, 0,

					// top
					dx, dy, dz, -dx, dy, dz, -dx, dy, dz, -dx, -dy, dz, -dx, -dy, dz, dx, -dy, dz, dx, -dy, dz, dx, dy, dz,

					// side
					dx, dy, 0, dx, dy, dz, -dx, dy, 0, -dx, dy, dz, -dx, -dy, 0, -dx, -dy, dz, dx, -dy, 0, dx, -dy, dz };
		}
		return calibrationLines;
	}
}
