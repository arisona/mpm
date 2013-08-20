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
package ch.ethz.fcl.mpm.examples.mapping;

import ch.ethz.fcl.mogl.scene.AbstractView;

public class MappingView extends AbstractView {
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
	 * @param id
	 *            view id
	 * @param viewType
	 *            view type
	 * @param title
	 *            view title (control view only)
	 * @param initialCamRotateZ
	 *            initial z angle of view
	 */
	public MappingView(MappingScene scene, int x, int y, int w, int h, ViewType viewType, String id, String title, double initialCamRotateZ) {
		super(scene, x, y, w, h, viewType, id, viewType == ViewType.MAPPED_VIEW ? null : title);
		getCamera().setRotateZ(initialCamRotateZ);
	}
}
