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

import ch.ethz.fcl.mogl.scene.AbstractScene;
import ch.ethz.fcl.mogl.scene.ShadowVolumeRenderer;
import ch.ethz.fcl.mpm.View.ViewType;
import ch.ethz.fcl.mpm.model.ICalibrationModel;

public class Scene extends AbstractScene<View> {
	// @formatter:off
	public static final String[] HELP = {
		"[1] Rendering/Navigation Mode",
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
	// @formatter:on

	public static final float[] MODEL_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float[] AXIS_COLOR = { 1.0f, 1.0f, 1.0f, 0.75f };
	public static final float[] GRID_COLOR = { 1.0f, 1.0f, 1.0f, 0.5f };
	public static final float[] CALIBRATION_COLOR_UNCALIBRATED = { 1.0f, 1.0f, 0.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_CALIBRATED = { 0.0f, 1.0f, 0.0f, 1.0f };

	public static final double CROSSHAIR_SIZE = 20.0;

	private float[] lightPosition = { 10.0f, 6.0f, 8.0f };

	public enum ControlMode {
		NAVIGATE("Mode: Navigation"), CALIBRATE("Mode: Calibration"), FILL("Mode: Fill");

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

	private ICalibrationModel model;
	private final ShadowVolumeRenderer renderer = new ShadowVolumeRenderer();

	private ControlMode mode = ControlMode.NAVIGATE;
	private View selectedView = null;

	public Scene() {
		setLightPosition(lightPosition);
	}

	public ICalibrationModel getModel() {
		return model;
	}

	public void setModel(ICalibrationModel model) {
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
		default:
			super.keyPressed(e, view);
		}
		repaintAll();
	}

	@Override
	public void mousePressed(MouseEvent e, View view) {
		if (selectedView != view) {
			selectedView = view;
			repaintAll();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e, View view) {
	}

	@Override
	public void mouseMoved(MouseEvent e, View view) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e, View view) {
		// XXX fix this
		//if (view.getCalibrationContext().calibrated)
		//	return;
	}



	public float[] getLightPosition() {
		return lightPosition.clone();
	}

	public void setLightPosition(float[] position) {
		lightPosition = position;
	}
}
