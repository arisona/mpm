package ch.ethz.fcl.mogl.gl;

import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;

import ch.ethz.fcl.mogl.scene.IView;

import com.jogamp.opengl.util.awt.TextRenderer;

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

	public static void drawTextRaster(IView view, String text, int col, int row) {
		Rectangle2D fontBounds = view.getTextRenderer().getBounds("W");
		double y = view.getHeight() - fontBounds.getHeight() * (row + 1);
		double x = fontBounds.getWidth() * col;
		drawText2D(view, text, x, y);
	}

	public static  void drawText2D(IView view, String text, double x, double y) {
		TextRenderer tr = view.getTextRenderer();
		tr.beginRendering(view.getWidth(), view.getHeight());
		tr.draw(text, (int) x, (int) y);
		tr.endRendering();
	}

	public static void drawText3D(IView view, String text, double x, double y, double z) {
		drawText3D(view, text, x, y, z, 0, 0);
	}

	public static void drawText3D(IView view, String text, double x, double y, double z, int dx, int dy) {
		double[] v = new double[3];
		view.getGLU().gluProject(x, y, z, view.getModelviewMatrix(), 0, view.getProjectionMatrix(), 0, view.getViewport(), 0, v, 0);
		TextRenderer tr = view.getTextRenderer();
		tr.beginRendering(view.getWidth(), view.getHeight());
		tr.draw(text, (int) v[0] + dx, (int) v[1] + dy);
		tr.endRendering();
	}

	public static void setTextColor(IView view, float r, float g, float b, float a) {
		view.getTextRenderer().setColor(r, g, b, a);
	}

}
