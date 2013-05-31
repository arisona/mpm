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
package ch.ethz.fcl.mpm;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.fcl.mogl.gl.ProjectionUtilities;
import ch.ethz.fcl.mogl.gl.VBO;
import ch.ethz.fcl.mogl.scene.AbstractView;
import ch.ethz.fcl.mpm.Scene.ControlMode;
import ch.ethz.fcl.mpm.calibration.CalibrationContext;
import ch.ethz.fcl.mpm.model.ICalibrationModel;

public class View extends AbstractView {
	public enum ViewType {
		CONTROL_VIEW, PROJECTION_VIEW
	}

	public static final double NEAR = 0.1;
	// public static final double FAR = 1000.0;
	public static final double FAR = Double.POSITIVE_INFINITY;

	private final int viewIndex;
	private final ViewType viewType;

	private CalibrationContext calibrationContext = new CalibrationContext();

	private double camDistance = 2.0;
	private double camRotateZ = 0.0;
	private double camRotateX = 45.0;
	private double camTranslateX = 0.0;
	private double camTranslateY = 0.0;

	private VBO vboVertices;
	private VBO vboEdges;

	/**
	 * Create a MPM control or projection view.
	 * 
	 * @param scene
	 *            scene to add the view to
	 * @param x
	 *            view x coordinate
	 * @param y
	 *            view y coordinate
	 * @param w
	 *            view width
	 * @param h
	 *            view height
	 * @param title
	 *            view title (control view only)
	 * @param viewIndex
	 *            view index (used for saving calibration profiles)
	 * @param initialCamRotateZ
	 *            initial z angle of view
	 * @param viewType
	 *            type of view (CONTROL_VIEW, PROJECTION_VIEW)
	 */
	public View(Scene scene, int x, int y, int w, int h, String title, int viewIndex, double initialCamRotateZ, ViewType viewType) {
		super(scene, x, y, w, h, viewType == ViewType.PROJECTION_VIEW ? null : title);
		this.viewIndex = viewIndex;
		this.viewType = viewType;
		camRotateZ = initialCamRotateZ;
	}

	public int getViewIndex() {
		return viewIndex;
	}

	public ViewType getViewType() {
		return viewType;
	}

	public CalibrationContext getCalibrationContext() {
		return calibrationContext;
	}

	public void setCalibrationContext(CalibrationContext calibrationContext) {
		this.calibrationContext = calibrationContext;
	}

	public boolean isCalibrated() {
		return calibrationContext.calibrated;
	}

	@Override
	public Scene getScene() {
		return (Scene) super.getScene();
	}

	@Override
	public void init(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		super.init(drawable, gl, glu);

		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glPointSize(10.0f);
	}

	@Override
	public void display(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		updateVBOs(gl);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_STENCIL_BUFFER_BIT);

		if (!getScene().isEnabled(this))
			return;

		// ---- INFO DISPLAY
		if (viewType != ViewType.PROJECTION_VIEW) {
			setTextColor(1.0f, 1.0f, 1.0f, 0.5f);
			drawTextRaster(drawable, getScene().getControlModeText(), 1, 1);

			for (int i = 0; i < Scene.HELP.length; ++i) {
				drawTextRaster(drawable, Scene.HELP[i], 1, i + 3);
			}
		}

		// ---- 3D SCENE ----

		// projection setup
		gl.glMatrixMode(GL2.GL_PROJECTION);
		if (!isCalibrated()) {
			gl.glLoadIdentity();
			gl.glLoadMatrixd(ProjectionUtilities.getPerspectiveMatrix(45.0, (double) getWidth() / getHeight(), NEAR, FAR), 0);
		} else {
			gl.glLoadMatrixd(getProjectionMatrix(), 0);
		}

		// view setup
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		if (!isCalibrated()) {
			gl.glLoadIdentity();
			gl.glTranslated(camTranslateX, camTranslateY, -camDistance);
			gl.glRotated(camRotateX - 90.0, 1.0, 0.0, 0.0);
			gl.glRotated(camRotateZ, 0.0, 0.0, 1.0);
		} else {
			gl.glLoadMatrixd(getModelviewMatrix(), 0);
		}

		// fetch viewport, and projection/modelview matrices
		fetchView(gl);

		// draw static elements
		if (viewType != ViewType.PROJECTION_VIEW || getScene().getControlMode() != ControlMode.NAVIGATE) {
			gl.glColor4fv(Scene.AXIS_COLOR, 0);
			drawLines(gl, getModel().getAxisLines());

			drawText3D(drawable, "X", getModel().getAxisLines()[3], getModel().getAxisLines()[4], getModel().getAxisLines()[5]);
			drawText3D(drawable, "Y", getModel().getAxisLines()[9], getModel().getAxisLines()[10], getModel().getAxisLines()[11]);

			gl.glColor4fv(Scene.GRID_COLOR, 0);
			drawLines(gl, getModel().getGridLines());
		}

		// draw model elements
		switch (getScene().getControlMode()) {
		case NAVIGATE:
			getScene().getRenderer().renderModel(gl, this);
			break;
		case CALIBRATE:
			gl.glColor4fv(Scene.MODEL_COLOR, 0);
			// vertices
			if (vboVertices != null) {
				vboVertices.render(gl, GL.GL_POINTS);
			}

			// lines
			if (vboEdges != null) {
				vboEdges.render(gl, GL.GL_LINES);
			}
			break;
		default:
			// do nothing;
		}

		// ---- 2D SCENE ----

		// projection setup
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// view setup
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// draw calibration elements
		switch (getScene().getControlMode()) {
		case CALIBRATE: {
			float[] c = calibrationContext.calibrated ? Scene.CALIBRATION_COLOR_CALIBRATED : Scene.CALIBRATION_COLOR_UNCALIBRATED;
			double[] v = new double[3];
			for (int i = 0; i < calibrationContext.projectedVertices.size(); ++i) {
				Vector3D a = calibrationContext.projectedVertices.get(i);
				gl.glColor4f(c[0], c[1], c[2], 0.5f);
				gl.glBegin(GL2.GL_POINTS);
				gl.glVertex3d(a.getX(), a.getY(), a.getZ());
				gl.glEnd();
				if (i == calibrationContext.currentSelection) {
					gl.glColor4f(c[0], c[1], c[2], 1.0f);
					gl.glBegin(GL2.GL_LINES);
					gl.glVertex3d(a.getX() - Scene.CROSSHAIR_SIZE / getWidth(), a.getY(), a.getZ());
					gl.glVertex3d(a.getX() + Scene.CROSSHAIR_SIZE / getWidth(), a.getY(), a.getZ());
					gl.glVertex3d(a.getX(), a.getY() - Scene.CROSSHAIR_SIZE / getHeight(), a.getZ());
					gl.glVertex3d(a.getX(), a.getY() + Scene.CROSSHAIR_SIZE / getHeight(), a.getZ());
					gl.glEnd();
				}
			}
			gl.glColor4f(c[0], c[1], c[2], 0.5f);
			gl.glBegin(GL2.GL_LINES);
			for (int i = 0; i < calibrationContext.projectedVertices.size(); ++i) {
				Vector3D a = calibrationContext.modelVertices.get(i);
				if (!projectToDeviceCoordinates(a.getX(), a.getY(), a.getZ(), v))
					continue;
				gl.glVertex3dv(v, 0);
				a = calibrationContext.projectedVertices.get(i);
				gl.glVertex3d(a.getX(), a.getY(), a.getZ());
			}
			gl.glEnd();
			break;
		}
		case FILL:
			if (viewType == ViewType.PROJECTION_VIEW) {
				gl.glColor4f(1, 1, 1, 1);
				gl.glRectd(-1, -1, -0.1, -0.1);
				gl.glRectd(1, -1, 0.1, -0.1);
				gl.glRectd(-1, 1, -0.1, 0.1);
				gl.glRectd(1, 1, 0.1, 0.1);
				gl.glRectd(-0.01, -1, 0.01, 1);
				gl.glRectd(-1, -0.01, 1, 0.01);
			}
			break;
		default:
			// do nothing
		}
	}

	@Override
	public void dispose(GLAutoDrawable drawable, GL2 gl, GLU glu) {
		super.dispose(drawable, gl, glu);
	}

	// camera handling

	public void addToCameraDistance(double delta) {
		camDistance += delta;
		camDistance = Math.max(1.0f, camDistance);
		camDistance = Math.min(80.0f, camDistance);
	}

	public void addToRotateZ(double delta) {
		camRotateZ += delta;
	}

	public void addToRotateX(double delta) {
		camRotateX += delta;
	}

	public void addToTranslateX(double delta) {
		camTranslateX += delta;
	}

	public void addToTranslateY(double delta) {
		camTranslateY += delta;
	}

	// private stuff

	private ICalibrationModel getModel() {
		return getScene().getModel();
	}

	private void updateVBOs(GL2 gl) {
		if (vboVertices == null && getModel().getCalibrationVertices() != null) {
			vboVertices = new VBO(gl, getModel().getCalibrationVertices());
		}
		if (vboEdges == null && getModel().getCalibrationLines() != null) {
			vboEdges = new VBO(gl, getModel().getCalibrationLines());
		}
	}

	// rendering helpers (for non-vbo use)

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
