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

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.fcl.mogl.model.IModel;

/**
 * Abstract scene class that implements some basic common functionality. Use as
 * base for common implementations.
 * 
 * @author radar
 * 
 */
public abstract class AbstractScene implements IScene {
	private IModel model;
	private final ArrayList<IView> views = new ArrayList<IView>();
	private final NavigationTool navigationTool = new NavigationTool();
	private final NavigationGrid navigationGrid = new NavigationGrid(10, 0.1f);

	private ITool currentTool = new NullTool();
	
	@Override
	public IModel getModel() {
		return model;
	}
	
	@Override
	public void setModel(IModel model) {
		this.model = model;
	}
	
	@Override
	public void addView(IView view) {
		views.add(view);
	}

	@Override
	public List<IView> getViews() {
		return views;
	}
	
	public NavigationTool getNavigationTool() {
		return navigationTool;
	}

	public NavigationGrid getNavigationGrid() {
		return navigationGrid;
	}
	
	@Override
	public void repaintAll() {
		for (IView view : views)
			view.repaint();
	}

	// key listener

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			System.exit(0);
		currentTool.keyPressed(e, view);
		if (!e.isConsumed())
			navigationTool.keyPressed(e, view);
	}

	@Override
	public void keyReleased(KeyEvent e, IView view) {
		currentTool.keyReleased(e, view);
	}

	@Override
	public void keyTyped(KeyEvent e, IView view) {
		currentTool.keyPressed(e, view);
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e, IView view) {
	}

	@Override
	public void mouseExited(MouseEvent e, IView view) {
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
	}

	@Override
	public void mouseReleased(MouseEvent e, IView view) {
	}

	@Override
	public void mouseClicked(MouseEvent e, IView view) {
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e, IView view) {
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
	}

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseWheelEvent e, IView view) {
	}
}
