package ch.ethz.fcl.mogl.gl;

import javax.media.opengl.GL2;

public final class DrawingUtils {
	public static void drawPoints(GL2 gl, float[] vertices) {
		gl.glBegin(GL2.GL_POINTS);
		for (int i = 0; i < vertices.length; i += 3) {
			gl.glVertex3fv(vertices, i);
		}
		gl.glEnd();
	}

	public static void drawLines(GL2 gl, float[] vertices) {
		gl.glBegin(GL2.GL_LINES);
		for (int i = 0; i < vertices.length; i += 3) {
			gl.glVertex3fv(vertices, i);
		}
		gl.glEnd();
	}
}
