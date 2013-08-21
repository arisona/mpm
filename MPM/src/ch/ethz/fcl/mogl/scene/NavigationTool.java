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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class NavigationTool extends AbstractTool {
	private static final double CAMERA_ROTATE_SCALE = 1.0;
	private static final double CAMERA_TRANSLATE_SCALE = 0.01;

	private int button;
	private int mouseX;
	private int mouseY;

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		button = e.getButton();
	}
	
	@Override
	public void mouseMoved(MouseEvent e, IView view) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		if (button == MouseEvent.BUTTON1) {
			view.getCamera().addToRotateZ(CAMERA_ROTATE_SCALE * (e.getX() - mouseX));
			view.getCamera().addToRotateX(CAMERA_ROTATE_SCALE * (e.getY() - mouseY));
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			view.getCamera().addToTranslateX(CAMERA_TRANSLATE_SCALE * (e.getX() - mouseX));
			view.getCamera().addToTranslateY(CAMERA_TRANSLATE_SCALE * (mouseY - e.getY()));
		}
		view.repaint();
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e, IView view) {
		view.getCamera().addToDistance(0.25 * e.getWheelRotation());
		view.repaint();
	}
}
