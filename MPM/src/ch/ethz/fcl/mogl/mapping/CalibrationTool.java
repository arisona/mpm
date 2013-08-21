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
package ch.ethz.fcl.mogl.mapping;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.fcl.mogl.gl.ProjectionUtils;
import ch.ethz.fcl.mogl.gl.VBO;
import ch.ethz.fcl.mogl.scene.AbstractTool;
import ch.ethz.fcl.mogl.scene.IView;
import ch.ethz.fcl.util.PreferencesStore;

public final class CalibrationTool extends AbstractTool {
	// @formatter:off
	private static final String[] CALIBRATION_HELP = {
		"Calibration Tool for 3D Mapping",
		"",
		"[0] Return",
		"",
		"[C] Clear Calibration",
		"[L] Load Calibration",
		"[S] Save Calibration",
		"[DEL] Clear Current Calibration Point",
	};
	// @formatter:on

	public static final double MAX_CALIBRATION_ERROR = 0.5;

	public static final float[] MODEL_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_UNCALIBRATED = { 1.0f, 1.0f, 0.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_CALIBRATED = { 0.0f, 1.0f, 0.0f, 1.0f };

	public static final double CROSSHAIR_SIZE = 20.0;

	private final ICalibrator calibrator = new BimberRaskarCalibrator();
	private final ICalibrationModel model;

	private Map<IView, CalibrationContext> contexts = new HashMap<IView, CalibrationContext>();

	private VBO vboVertices;
	private VBO vboEdges;

	public CalibrationTool(ICalibrationModel model) {
		this.model = model;
		setExclusive(true);
	}

	@Override
	public void render3D(GL2 gl, IView view) {
		if (!view.isCurrent())
			return;

		renderGrid(gl, view);
		
		updateVBOs(gl);

		gl.glColor4fv(MODEL_COLOR, 0);

		// vertices
		gl.glPointSize(10.0f);
		if (vboVertices != null) {
			vboVertices.render(gl, GL.GL_POINTS);
		}
		gl.glPointSize(1.0f);

		// lines
		if (vboEdges != null) {
			vboEdges.render(gl, GL.GL_LINES);
		}
	}

	@Override
	public void render2D(GL2 gl, IView view) {
		renderUI(gl, view, CALIBRATION_HELP);
		
		if (!view.isCurrent())
			return;

		CalibrationContext context = getContext(view);

		// ---- CALIBRATION MODEL
		float[] c = context.calibrated ? CALIBRATION_COLOR_CALIBRATED : CALIBRATION_COLOR_UNCALIBRATED;
		double[] v = new double[3];
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			Vector3D a = context.projectedVertices.get(i);
			gl.glPointSize(10.0f);
			gl.glColor4f(c[0], c[1], c[2], 0.5f);
			gl.glBegin(GL2.GL_POINTS);
			gl.glVertex3d(a.getX(), a.getY(), a.getZ());
			gl.glEnd();
			gl.glPointSize(1.0f);
			if (i == context.currentSelection) {
				gl.glColor4f(c[0], c[1], c[2], 1.0f);
				gl.glBegin(GL2.GL_LINES);
				gl.glVertex3d(a.getX() - CROSSHAIR_SIZE / view.getWidth(), a.getY(), a.getZ());
				gl.glVertex3d(a.getX() + CROSSHAIR_SIZE / view.getWidth(), a.getY(), a.getZ());
				gl.glVertex3d(a.getX(), a.getY() - CROSSHAIR_SIZE / view.getHeight(), a.getZ());
				gl.glVertex3d(a.getX(), a.getY() + CROSSHAIR_SIZE / view.getHeight(), a.getZ());
				gl.glEnd();
			}
		}
		gl.glColor4f(c[0], c[1], c[2], 0.5f);
		gl.glBegin(GL2.GL_LINES);
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			Vector3D a = context.modelVertices.get(i);
			if (!ProjectionUtils.projectToDeviceCoordinates(view, a.getX(), a.getY(), a.getZ(), v))
				continue;
			gl.glVertex3dv(v, 0);
			a = context.projectedVertices.get(i);
			gl.glVertex3d(a.getX(), a.getY(), a.getZ());
		}
		gl.glEnd();
	}

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_C:
			contexts.put(view, new CalibrationContext());
			break;
		case KeyEvent.VK_L:
			loadCalibration(view);
			break;
		case KeyEvent.VK_S:
			saveCalibration(view);
			break;
		case KeyEvent.VK_UP:
			cursorAdjust(view, 0, 1);
			break;
		case KeyEvent.VK_DOWN:
			cursorAdjust(view, 0, -1);
			break;
		case KeyEvent.VK_LEFT:
			cursorAdjust(view, -1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			cursorAdjust(view, 1, 0);
			break;
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
			deleteCurrent(view);
			break;
		}
		view.getScene().repaintAll();
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		CalibrationContext context = getContext(view);

		// reset first
		context.currentSelection = -1;

		// first, try to hit calibration point
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			int x = ProjectionUtils.deviceToScreenX(view, context.projectedVertices.get(i).getX());
			int y = ProjectionUtils.deviceToScreenY(view, context.projectedVertices.get(i).getY());
			if (snap2D(e.getX(), view.getHeight() - e.getY(), x, y)) {
				// we got a point to move!
				context.currentSelection = i;
				view.repaint();
				return;
			}
		}

		// second, try to hit model point
		float[] mv = model.getCalibrationVertices();
		double[] vv = new double[3];
		for (int i = 0; i < mv.length; i += 3) {
			if (!ProjectionUtils.projectToScreenCoordinates(view, mv[i], mv[i + 1], mv[i + 2], vv))
				continue;
			if (snap2D(e.getX(), view.getHeight() - e.getY(), (int) vv[0], (int) vv[1])) {
				Vector3D a = new Vector3D(mv[i], mv[i + 1], mv[i + 2]);
				int index = context.modelVertices.indexOf(a);
				if (index != -1) {
					context.currentSelection = index;
				} else {
					context.currentSelection = context.modelVertices.size();
					context.modelVertices.add(a);
					context.projectedVertices.add(new Vector3D(ProjectionUtils.screenToDeviceX(view, (int) vv[0]), ProjectionUtils.screenToDeviceY(view, (int) vv[1]), 0));
				}
				calibrate(view);
				view.repaint();
				return;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			Vector3D a = new Vector3D(ProjectionUtils.screenToDeviceX(view, e.getX()), ProjectionUtils.screenToDeviceY(view, view.getHeight() - e.getY()), 0);
			context.projectedVertices.set(context.currentSelection, a);
			calibrate(view);
		}
		view.repaint();
	}

	private CalibrationContext getContext(IView view) {
		CalibrationContext context = contexts.get(view);
		if (context == null) {
			context = new CalibrationContext();
			contexts.put(view, context);
		}
		return context;
	}

	private void cursorAdjust(IView view, double dx, double dy) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			Vector3D p = context.projectedVertices.get(context.currentSelection);
			Vector3D a = new Vector3D(p.getX() + dx / view.getWidth(), p.getY() + dy / view.getHeight(), 0.0);
			context.projectedVertices.set(context.currentSelection, a);
			calibrate(view);
		}
	}

	private void deleteCurrent(IView view) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			context.modelVertices.remove(context.currentSelection);
			context.projectedVertices.remove(context.currentSelection);
			context.currentSelection = -1;
			calibrate(view);
		}
	}

	private void loadCalibration(IView view) {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (IView v : view.getScene().getViews()) {
			getContext(v).load(p, iv);
			calibrate(v);
			iv++;
		}
	}

	private void saveCalibration(IView view) {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (IView v : view.getScene().getViews()) {
			getContext(v).save(p, iv);
			iv++;
		}
	}

	private void calibrate(IView view) {
		try {
			CalibrationContext context = getContext(view);
			context.calibrated = false;

			double error = calibrator.calibrate(context.modelVertices, context.projectedVertices, view.getCamera().getNearClippingPlane(), view.getCamera().getFarClippingPlane());
			if (error < MAX_CALIBRATION_ERROR)
				context.calibrated = true;

			if (context.calibrated)
				view.setMatrices(calibrator.getProjectionMatrix(), calibrator.getModelviewMatrix());
			else
				view.setMatrices(null, null);
			view.repaint();
			// System.out.println("error: " + error);
		} catch (Throwable t) {
		}
	}

	// vbo handling
	private void updateVBOs(GL2 gl) {
		if (vboVertices == null && model.getCalibrationVertices() != null) {
			vboVertices = new VBO(gl, model.getCalibrationVertices());
		}
		if (vboEdges == null && model.getCalibrationLines() != null) {
			vboEdges = new VBO(gl, model.getCalibrationLines());
		}
	}

}
