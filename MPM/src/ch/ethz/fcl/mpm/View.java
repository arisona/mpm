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

import ch.ethz.fcl.mogl.scene.AbstractView;

public class View extends AbstractView {
	public enum ViewType {
		CONTROL_VIEW, PROJECTION_VIEW
	}

	private final ViewType viewType;

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
	public View(Scene scene, int x, int y, int w, int h, String id, String title, double initialCamRotateZ, ViewType viewType) {
		super(scene, x, y, w, h, id, viewType == ViewType.PROJECTION_VIEW ? null : title);
		this.viewType = viewType;
		getCamera().setRotateZ(initialCamRotateZ);
	}

	public ViewType getViewType() {
		return viewType;
	}
}
