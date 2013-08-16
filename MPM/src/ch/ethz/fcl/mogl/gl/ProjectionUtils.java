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
