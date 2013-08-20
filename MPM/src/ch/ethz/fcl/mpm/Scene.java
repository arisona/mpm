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

import ch.ethz.fcl.mogl.render.ShadowVolumeRenderer;
import ch.ethz.fcl.mogl.scene.AbstractScene;
import ch.ethz.fcl.mogl.scene.IRenderer;
import ch.ethz.fcl.mogl.scene.IView;
import ch.ethz.fcl.mpm.View.ViewType;

public class Scene extends AbstractScene {
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

	private float[] lightPosition = { 10.0f, 6.0f, 8.0f };

	private final ShadowVolumeRenderer renderer = new ShadowVolumeRenderer();

	private ControlMode mode = ControlMode.NAVIGATE;
	private IView selectedView = null;

	public Scene() {
		setLightPosition(lightPosition);
	}

	@Override
	public IRenderer getRenderer() {
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
			return mode.getText() + " (View " + selectedView.getId() + ")";
		} else if (mode == ControlMode.FILL) {
			return mode.getText() + " (Fill " + selectedView.getId() + ")";
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
	public void keyPressed(KeyEvent e, IView view) {
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
			// XXX FIXME
			//model.reset();
			break;
		default:
			super.keyPressed(e, view);
		}
		repaintAll();
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		if (selectedView != view) {
			selectedView = view;
			repaintAll();
		}
	}

	@Override
	public float[] getLightPosition() {
		return lightPosition.clone();
	}

	public void setLightPosition(float[] position) {
		lightPosition = position;
	}
}
