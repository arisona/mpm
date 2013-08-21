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
package ch.ethz.fcl.mogl.gl;

import ch.ethz.fcl.mogl.scene.IView;

public final class ProjectionUtils {
	/**
	 * Returns a perspective projection matrix. Same as gluPerspective, but with
	 * support for far plane at infinity.
	 * 
	 * @param fovy
	 *            field of view (degrees)
	 * @param aspect
	 *            aspect ratio
	 * @param near
	 *            near plane
	 * @param far
	 *            far plane (set to Double.POSITIVE_INFINITY for far plane at
	 *            infinity)
	 * @return column first perspective projection matrix
	 */
	public static double[] getPerspectiveMatrix(double fovy, double aspect, double near, double far) {
		final double radians = fovy / 2 * Math.PI / 180;
		double sine = Math.sin(radians);
		double deltaZ = far - near;

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return new double[16];
		}

		double cotangent = (float) Math.cos(radians) / sine;

		double[] m = new double[16];

		m[0] = cotangent / aspect;
		m[5] = cotangent;
		m[10] = far >= Double.POSITIVE_INFINITY ? -1 : -(far + near) / deltaZ;
		m[11] = -1;
		m[14] = far >= Double.POSITIVE_INFINITY ? -2 * near : -2 * near * far / deltaZ;
		return m;
	}
	
	public static boolean projectToDeviceCoordinates(IView view, double x, double y, double z, double[] v) {
		if (!view.getGLU().gluProject(x, y, z, view.getModelviewMatrix(), 0, view.getProjectionMatrix(), 0, view.getViewport(), 0, v, 0))
			return false;
		v[0] = screenToDeviceX(view, (int) v[0]);
		v[1] = screenToDeviceY(view, (int) v[1]);
		v[2] = 0;
		return true;
	}

	public static boolean projectToScreenCoordinates(IView view, double x, double y, double z, double[] v) {
		return view.getGLU().gluProject(x, y, z, view.getModelviewMatrix(), 0, view.getProjectionMatrix(), 0, view.getViewport(), 0, v, 0);
	}

	public static int deviceToScreenX(IView view, double x) {
		return (int) ((1.0 + x) / 2.0 * view.getWidth());
	}

	public static int deviceToScreenY(IView view, double y) {
		return (int) ((1.0 + y) / 2.0 * view.getHeight());
	}

	public static double screenToDeviceX(IView view, int x) {
		return 2.0 * x / view.getWidth() - 1.0;
	}

	public static double screenToDeviceY(IView view, int y) {
		return 2.0 * y / view.getHeight() - 1.0;
	}

}
