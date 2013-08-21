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
package ch.ethz.fcl.mogl.scene;

import javax.media.opengl.GL2;

import ch.ethz.fcl.mogl.gl.DrawingUtils;

public class NavigationGrid {
	public static final float[] AXIS_COLOR = { 1.0f, 1.0f, 1.0f, 0.75f };
	public static final float[] GRID_COLOR = { 1.0f, 1.0f, 1.0f, 0.5f };

	private int numGridLines;
	private float gridSpacing;

	private float[] axisLines;
	private float[] gridLines;

	public NavigationGrid(int numGridLines, float gridSpacing) {
		this.numGridLines = numGridLines;
		this.gridSpacing = gridSpacing;
	}

	protected void render(GL2 gl, IView view) {
		gl.glColor4fv(NavigationGrid.AXIS_COLOR, 0);
		DrawingUtils.drawLines(gl, getAxisLines());

		DrawingUtils.drawText3D(view, "X", (double)getAxisLines()[3], (double)getAxisLines()[4], (double)getAxisLines()[5]);
		DrawingUtils.drawText3D(view, "Y", (double)getAxisLines()[9], (double)getAxisLines()[10], (double)getAxisLines()[11]);

		gl.glColor4fv(NavigationGrid.GRID_COLOR, 0);
		DrawingUtils.drawLines(gl, getGridLines());		
	}
	
	public float[] getAxisLines() {
		if (axisLines == null) {
			float e = 0.5f * gridSpacing * (numGridLines + 1);
			axisLines = new float[] { -e, 0, 0, e, 0, 0, 0, -e, 0, 0, e, 0 };
		}
		return axisLines;
	}

	public float[] getGridLines() {
		if (gridLines == null) {
			gridLines = new float[numGridLines * 3 * 2 * 2];
			float e = 0.5f * gridSpacing * (numGridLines + 1);
			int n = numGridLines / 2;
			int i = 0;
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = -e;
				gridLines[i++] = 0;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = e;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = -e;
				gridLines[i++] = 0;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = e;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -e;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = 0;
				gridLines[i++] = e;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -e;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = 0;
				gridLines[i++] = e;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = 0;
			}
		}
		return gridLines;
	}
	
	public float getExtentX() {
		return gridSpacing * numGridLines;
	}

	public float getExtentY() {
		return gridSpacing * numGridLines;
	}	
}
