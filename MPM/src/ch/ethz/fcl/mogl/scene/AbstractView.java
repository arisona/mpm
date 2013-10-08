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

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import ch.ethz.fcl.mogl.gl.DrawingUtils;
import ch.ethz.fcl.mogl.gl.Frame;
import ch.ethz.fcl.mogl.gl.ProjectionUtils;
import ch.ethz.fcl.mogl.ui.Button;

import com.jogamp.opengl.util.awt.TextRenderer;

/**
 * Abstract view class that implements some basic common functionality. Use as
 * base for common implementations.
 * 
 * @author radar
 * 
 */
public abstract class AbstractView implements IView {

	private static final Font FONT = new Font("SansSerif", Font.BOLD, 12);

	private final Frame frame;
	private final IScene scene;

	private final ViewType viewType;
	private final String id;

	private Camera camera = new Camera();

	private GLU glu;
	private TextRenderer textRenderer;

	private int[] viewport = new int[4];
	private double[] projectionMatrix = new double[16];
	private double[] modelviewMatrix = new double[16];	
	private boolean updateMatrices = true;
	
	protected AbstractView(IScene scene, int x, int y, int w, int h, ViewType viewType, String id, String title) {
		this.frame = new Frame(w, h, title);
		this.scene = scene;
		this.viewType = viewType;
		this.id = id;
		frame.setView(this);
		Point p = frame.getJFrame().getLocation();
		if (x != -1)
			p.x = x;
		if (y != -1)
			p.y = y;
		frame.getJFrame().setLocation(p);
	}

	@Override
	public final Frame getFrame() {
		return frame;
	}

	@Override
	public IScene getScene() {
		return scene;
	}
	
	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public final int getWidth() {
		return viewport[2];
	}

	@Override
	public final int getHeight() {
		return viewport[3];
	}
	
	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public ViewType getViewType() {
		return viewType;
	}

	@Override
	public int[] getViewport() {
		return viewport;
	}
	
	@Override
	public final double[] getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public final double[] getModelviewMatrix() {
		return modelviewMatrix;
	}

	@Override
	public void setMatrices(double[] projectionMatrix, double[] modelviewMatrix) {
		if (projectionMatrix == null) {
			this.updateMatrices = true;
		} else {
			this.updateMatrices = false;
			this.projectionMatrix = projectionMatrix;
			this.modelviewMatrix = modelviewMatrix;			
		}
	}
	
	@Override
	public final GLU getGLU() {
		return glu;
	}
	
	@Override
	public TextRenderer getTextRenderer() {
		return textRenderer;
	}
	
	@Override
	public boolean isEnabled() {
		return getScene().isEnabled(this);
	}
	
	@Override
	public boolean isCurrent() {
		return getScene().getCurrentView() == this;
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
		
		gl.glEnable(GL2.GL_POINT_SMOOTH);
	}

	
	@Override
	public void display(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);

		if (!getScene().isEnabled(this))
			return;

		
		// ---- 3D SCENE ----

		// projection setup
		gl.glMatrixMode(GL2.GL_PROJECTION);
		if (updateMatrices) {
			Camera c = getCamera();
			gl.glLoadIdentity();
			gl.glLoadMatrixd(ProjectionUtils.getPerspectiveMatrix(c.getFOV(), (double) getWidth() / getHeight(), c.getNearClippingPlane(), c.getFarClippingPlane()), 0);
		} else {
			gl.glLoadMatrixd(getProjectionMatrix(), 0);
		}

		// view setup
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		if (updateMatrices) {
			Camera c = getCamera();
			gl.glLoadIdentity();
			gl.glTranslated(c.getTranslateX(), c.getTranslateY(), -c.getDistance());
			gl.glRotated(c.getRotateX() - 90.0, 1.0, 0.0, 0.0);
			gl.glRotated(c.getRotateZ(), 0.0, 0.0, 1.0);
		} else {
			gl.glLoadMatrixd(getModelviewMatrix(), 0);
		}

		// fetch viewport, and projection/modelview matrices
		fetchView(gl);

		// draw model elements
		if (!getScene().getCurrentTool().isExclusive()) {
			getScene().getRenderer().renderModel(gl, this);
		}
		getScene().getCurrentTool().render3D(gl, this);
		
		
		// ---- 2D SCENE ----

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		getScene().getCurrentTool().render2D(gl, this);
		
		// ---- PIXEL SPACE (FOR UI)
		
		if (getViewType() == ViewType.INTERACTIVE_VIEW) {
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glOrtho(0, viewport[2], viewport[3], 0, -1, 1);
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			for (Button button : getScene().getButtons()) {
				button.render(gl, this);
			}
			if (Button.getMessage() != null) {
				DrawingUtils.drawText2D(this, 8, 8, Button.getMessage());
			}
		}
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

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		scene.mouseWheelMoved(e, this);
	}

	
	// private stuff
	
	private void fetchView(GL2 gl) {
		gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelviewMatrix, 0);
	}	
}
