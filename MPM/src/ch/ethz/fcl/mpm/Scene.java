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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.prefs.Preferences;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.fcl.mogl.scene.AbstractScene;
import ch.ethz.fcl.mogl.scene.ShadowVolumeRenderer;
import ch.ethz.fcl.mpm.View.ViewType;
import ch.ethz.fcl.mpm.calibration.BimberRaskarCalibrator;
import ch.ethz.fcl.mpm.calibration.CalibrationContext;
import ch.ethz.fcl.mpm.calibration.ICalibrator;
import ch.ethz.fcl.mpm.model.IGeometryModel;

public class Scene extends AbstractScene<View> {
	public static final String[] HELP = {
		"[1] Navigation Mode",
		"[2] Calibration Mode",
		"[3] Fill Mode",
		"",
		"[0] Enable/Disable Sunpath and Shadows",
		"",
		"[R] Reset Model",
		"",
		"[C] Clear Calibration",
		"[L] Load Calibration",
		"[S] Save Calibration",
		"[DEL] Clear Current Calibration Point",
		"",
		"[ESC] Quit"
	};
	
	
	public static final float[] MODEL_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float[] AXIS_COLOR = { 1.0f, 1.0f, 1.0f, 0.75f };
	public static final float[] GRID_COLOR = { 1.0f, 1.0f, 1.0f, 0.5f };
	public static final float[] CALIBRATION_COLOR_UNCALIBRATED = { 1.0f, 1.0f, 0.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_CALIBRATED = { 0.0f, 1.0f, 0.0f, 1.0f };

	public static final int SNAP_SIZE = 4;
	
	public static final double CROSSHAIR_SIZE = 20.0;
	
	public static final double CAMERA_TRANSLATE_SCALE = 0.01;
	public static final double CAMERA_ROTATE_SCALE = 1.0;
	
	public static final double MAX_CALIBRATION_ERROR = 0.5;

	private float[] lightPosition = { 10.0f, 6.0f, 8.0f };

	public enum ControlMode {
		NAVIGATE("Mode: Navigation"),
		CALIBRATE("Mode: Calibration"),
		FILL("Mode: Fill");
		
		ControlMode(String text) {
			this.text = text;
		}
		
		private final String text;
		
		public String getText() {
			return text;
		}
		
		@Override
		public String toString() {
			return text;
		}
	}
	
	
	private IGeometryModel model;
	private final ICalibrator calibrator = new BimberRaskarCalibrator();
	private final ShadowVolumeRenderer renderer = new ShadowVolumeRenderer();

	private ControlMode mode = ControlMode.NAVIGATE;
	private View selectedView = null;
	
	public Scene() {
		setLightPosition(lightPosition);
	}

	public IGeometryModel getModel() {
		return model;
	}
	
	public void setModel(IGeometryModel model) {
		this.model = model;
	}
	
	public ShadowVolumeRenderer getRenderer() {
		return renderer;
	}
	
	public void modelChanged() {
		repaintAll();
	}
	
	public ControlMode getControlMode() {
		return mode;
	}

	public String getControlModeText() {
		if (mode == ControlMode.CALIBRATE) {
			return mode.getText() + " (View " + selectedView.getViewIndex() + ")";
		} else if (mode == ControlMode.FILL) {
			return mode.getText() + " (Fill " + selectedView.getViewIndex() + ")";			
		}
		return mode.getText();
	}
	
	public ICalibrator getCalibrator() {
		return calibrator;
	}
	
	public boolean isEnabled(View view) {
		if (view.getViewType() == ViewType.CONTROL_VIEW)
			return true;
		
		if (mode == ControlMode.CALIBRATE || mode == ControlMode.FILL) {
			if (view == selectedView)
				return true;
			else
				return false;
		}
		
		return true;
	}
	
	@Override
	public void keyPressed(KeyEvent e, View view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_1:
			mode = ControlMode.NAVIGATE;
			break;
		case KeyEvent.VK_2:
			mode = ControlMode.CALIBRATE;
			selectedView = view;
			break;
		case KeyEvent.VK_3:
			mode = ControlMode.FILL;
			selectedView = view;
			break;
		case KeyEvent.VK_0:
			renderer.setEnableShadows(!renderer.getEnableShadows());
			break;
		case KeyEvent.VK_R:
			model.reset();
			break;
		case KeyEvent.VK_C:
			view.setCalibrationContext(new CalibrationContext());
			break;
		case KeyEvent.VK_L:
			loadCalibration();
			break;
		case KeyEvent.VK_S:
			saveCalibration();
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
		default:
			super.keyPressed(e, view);
		}
		repaintAll();
	}
	
	// mouse handling
	private int button;
	private int mouseX;
	private int mouseY;

	@Override
	public void mousePressed(MouseEvent e, View view) {
		button = e.getButton();
		if (selectedView != view) {
			selectedView = view;
			repaintAll();
		}
		if (mode == ControlMode.CALIBRATE) {
			CalibrationContext context = view.getCalibrationContext();

			// reset first
			context.currentSelection = -1;
			
			// first, try to hit calibration point
			for (int i = 0; i < context.projectedVertices.size(); ++i) {
				int x = view.deviceToScreenX(context.projectedVertices.get(i).getX());
				int y = view.deviceToScreenY(context.projectedVertices.get(i).getY());
				if (snap2D(e.getX(), view.getHeight() - e.getY(), x, y)) {
					// we got a point to move!
					context.currentSelection = i;
					view.repaint();
					return;
				}
			}
			
			// second, try to hit model point
			float[] mv = getModel().getModelVertices();
			double[] vv = new double[3];
			for (int i = 0; i < mv.length; i+=3) {
				if (!view.projectToScreenCoordinates(mv[i], mv[i+1], mv[i+2], vv))
					continue;
				if (snap2D(e.getX(), view.getHeight() - e.getY(), (int)vv[0], (int)vv[1])) {
					Vector3D a = new Vector3D(mv[i], mv[i+1], mv[i+2]);
					int index = context.modelVertices.indexOf(a);
					if (index != -1) {
						context.currentSelection = index;
					} else {
						context.currentSelection = context.modelVertices.size();
						context.modelVertices.add(a);
						context.projectedVertices.add(new Vector3D(view.screenToDeviceX((int)vv[0]), view.screenToDeviceY((int)vv[1]), 0));
					}
					calibrate(view);
					view.repaint();
					return;
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e, View view) {
		switch (mode) {
		case CALIBRATE:
		{
			CalibrationContext context = view.getCalibrationContext();
			if (context.currentSelection != -1) {
				Vector3D a = new Vector3D(view.screenToDeviceX(e.getX()), view.screenToDeviceY(view.getHeight() - e.getY()), 0);
				context.projectedVertices.set(context.currentSelection, a);
				calibrate(view);
				break;
			}
			if (context.calibrated)
				break;
			// fall through
		}
		case NAVIGATE:
			if (button == MouseEvent.BUTTON1) {
				view.addToRotateZ(CAMERA_ROTATE_SCALE * (e.getX() - mouseX));
				view.addToRotateX(CAMERA_ROTATE_SCALE * (e.getY() - mouseY));
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				view.addToTranslateX(CAMERA_TRANSLATE_SCALE * (e.getX() - mouseX));
				view.addToTranslateY(CAMERA_TRANSLATE_SCALE *  (mouseY - e.getY()));				
			}
			// fall through
		default:
			mouseX = e.getX();
			mouseY = e.getY();			
		}
		view.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent e, View view) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e, View view) {
		if (view.getCalibrationContext().calibrated)
			return;
		view.addToCameraDistance(0.25 * e.getWheelRotation());
		view.repaint();
	}
	
	private void calibrate(View view) {
		try {
			CalibrationContext context = view.getCalibrationContext();
			context.calibrated = false;
			
			double error = getCalibrator().calibrate(context.modelVertices, context.projectedVertices, View.NEAR, View.FAR);
			if (error < MAX_CALIBRATION_ERROR)
				context.calibrated = true;
	
			view.setProjectionMatrix(getCalibrator().getProjectionMatrix());
			view.setModelviewMatrix(getCalibrator().getModelviewMatrix());
			view.repaint();
			//System.out.println("error: " + error);
		} catch (Throwable t) {}
	}
	
	private void cursorAdjust(View view, double dx, double dy) {
		CalibrationContext context = view.getCalibrationContext();
		if (context.currentSelection != -1) {
			Vector3D p = context.projectedVertices.get(context.currentSelection);
			Vector3D a = new Vector3D(p.getX() + dx / view.getWidth(), p.getY() + dy / view.getHeight(), 0.0);
			context.projectedVertices.set(context.currentSelection, a);
			calibrate(view);
		}		
	}
	
	private void deleteCurrent(View view) {
		CalibrationContext context = view.getCalibrationContext();
		if (context.currentSelection != -1) {
			context.modelVertices.remove(context.currentSelection);
			context.projectedVertices.remove(context.currentSelection);
			context.currentSelection = -1;
			calibrate(view);
		}		
	}
	
	private void loadCalibration() {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (View v : getViews()) {
			v.getCalibrationContext().load(p, iv);
			calibrate(v);
			iv++;
		}
		//repaintAll();
	}
	
	private void saveCalibration() {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (View v : getViews()) {
			v.getCalibrationContext().save(p, iv);
			iv++;
		}
	}
	
	private static final boolean snap2D(int mx, int my, int x, int y) {
		if ((mx >= x - SNAP_SIZE) && (mx <= x + SNAP_SIZE) && (my >= y - SNAP_SIZE) && (my < y + SNAP_SIZE))
			return true;
		return false;
	}

	public float[] getLightPosition() {
		return lightPosition.clone();
	}
	
	public void setLightPosition(float[] position) {
		lightPosition = position;
	}
}
