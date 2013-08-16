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

import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import ch.ethz.fcl.mogl.gl.Frame;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Abstract view class that implements some basic common functionality. Use as
 * base for common implementations.
 * 
 * @author radar
 * 
 */
// XXX currently contains stuff that doesn't belong here (e.g. text renderer).
public abstract class AbstractView implements IView {
	private static final Font FONT = new Font("SansSerif", Font.BOLD, 12);

	private final Frame frame;
	private final IScene<IView> scene;

	private Camera camera = new Camera();

	private GLU glu;
	private TextRenderer textRenderer;

	private int[] viewport = new int[4];
	private double[] projectionMatrix = new double[16];
	private double[] modelviewMatrix = new double[16];

	@SuppressWarnings("unchecked")
	protected AbstractView(IScene<? extends IView> scene, int x, int y, int w, int h, String title) {
		this.frame = new Frame(w, h, title);
		this.scene = (IScene<IView>) scene;
		frame.setView(this);
		Point p = frame.getJFrame().getLocation();
		if (x != -1)
			p.x = x;
		if (y != -1)
			p.y = y;
		frame.getJFrame().setLocation(p);
	}

	public IScene<?> getScene() {
		return scene;
	}
	
	public Camera getCamera() {
		return camera;
	}

	protected final Frame getFrame() {
		return frame;
	}

	protected final GLU getGLU() {
		return glu;
	}

	public final int getWidth() {
		return viewport[2];
	}

	public final int getHeight() {
		return viewport[3];
	}

	public final double[] getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(double[] projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}

	public final double[] getModelviewMatrix() {
		return modelviewMatrix;
	}

	public void setModelviewMatrix(double[] modelviewMatrix) {
		this.modelviewMatrix = modelviewMatrix;
	}

	protected void fetchView(GL2 gl) {
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelviewMatrix, 0);
	}

	// opengl handling

	@Override
	public void init(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		this.glu = glu;

		textRenderer = new TextRenderer(FONT);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);

		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, GL2 gl, GLU glu, int x, int y, int width, int height) {
		if (height == 0)
			height = 1; // prevent divide by zero
		viewport[2] = width;
		viewport[3] = height;
		gl.glViewport(0, 0, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		glu = null;
		textRenderer = null;
	}

	@Override
	public void repaint() {
		frame.repaint();
	}

	// key listener

	@Override
	public void keyPressed(KeyEvent e) {
		scene.keyPressed(e, this);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		scene.keyReleased(e, this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		scene.keyTyped(e, this);
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e) {
		scene.mouseEntered(e, this);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		scene.mouseExited(e, this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		frame.requestFocus();
		scene.mousePressed(e, this);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		scene.mouseReleased(e, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		scene.mouseClicked(e, this);
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e) {
		scene.mouseMoved(e, this);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		scene.mouseDragged(e, this);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scene.mouseWheelMoved(e, this);
	}

	// text rendering

	private Rectangle2D fontBounds = null;

	protected void drawTextRaster(GLAutoDrawable drawable, String text, int col, int row) {
		if (fontBounds == null)
			fontBounds = textRenderer.getBounds("W");
		double y = getHeight() - fontBounds.getHeight() * (row + 1);
		double x = fontBounds.getWidth() * col;
		drawText2D(drawable, text, x, y);
	}

	protected void drawText2D(GLAutoDrawable drawable, String text, double x, double y) {
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.draw(text, (int) x, (int) y);
		textRenderer.endRendering();
	}

	protected void drawText3D(GLAutoDrawable drawable, String text, double x, double y, double z) {
		drawText3D(drawable, text, x, y, z, 0, 0);
	}

	protected void drawText3D(GLAutoDrawable drawable, String text, double x, double y, double z, int dx, int dy) {
		double[] v = new double[3];
		glu.gluProject(x, y, z, modelviewMatrix, 0, projectionMatrix, 0, viewport, 0, v, 0);
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.draw(text, (int) v[0] + dx, (int) v[1] + dy);
		textRenderer.endRendering();
	}

	protected void setTextColor(float r, float g, float b, float a) {
		textRenderer.setColor(r, g, b, a);
	}

	// other helpers

	public boolean projectToDeviceCoordinates(double x, double y, double z, double[] v) {
		if (!glu.gluProject(x, y, z, modelviewMatrix, 0, projectionMatrix, 0, viewport, 0, v, 0))
			return false;
		v[0] = screenToDeviceX((int) v[0]);
		v[1] = screenToDeviceY((int) v[1]);
		v[2] = 0;
		return true;
	}

	public boolean projectToScreenCoordinates(double x, double y, double z, double[] v) {
		return glu.gluProject(x, y, z, modelviewMatrix, 0, projectionMatrix, 0, viewport, 0, v, 0);
	}

	public final int deviceToScreenX(double x) {
		return (int) ((1.0 + x) / 2.0 * getWidth());
	}

	public final int deviceToScreenY(double y) {
		return (int) ((1.0 + y) / 2.0 * getHeight());
	}

	public final double screenToDeviceX(int x) {
		return 2.0 * x / getWidth() - 1.0;
	}

	public final double screenToDeviceY(int y) {
		return 2.0 * y / getHeight() - 1.0;
	}

}
