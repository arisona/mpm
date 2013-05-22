package ch.ethz.fcl.mogl.gl;

public final class ProjectionUtilities {
	/**
	 * Returns a perspective projection matrix. Same as gluPerspective, but with support for far plane at infinity.
	 * @param fovy Field of view (degrees)
	 * @param aspect Aspect ratio
	 * @param near Near plane
	 * @param far Far plane (set to Double.POSITIVE_INFINITY for far plane at infinity)
	 * @return Column first perspective projection matrix
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
}
