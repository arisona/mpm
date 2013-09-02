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

	public static void drawTextRaster(IView view, int col, int row, String text) {
		Rectangle2D fontBounds = view.getTextRenderer().getBounds("W");
		double y = view.getHeight() - fontBounds.getHeight() * (row + 1);
		double x = fontBounds.getWidth() * col;
		drawText2D(view, x, y, text);
	}

	public static  void drawText2D(IView view, double x, double y, String text) {
		TextRenderer tr = view.getTextRenderer();
		tr.beginRendering(view.getWidth(), view.getHeight());
		tr.draw(text, (int) x, (int) y);
		tr.endRendering();
	}

	public static void drawText3D(IView view, double x, double y, double z, String text) {
		drawText3D(view, x, y, z, 0, 0, text);
	}

	public static void drawText3D(IView view, double x, double y, double z, int dx, int dy, String text) {
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

	public static void setTextColor(IView view, float[] rgba) {
		view.getTextRenderer().setColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
}
