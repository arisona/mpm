package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL2;

public abstract class AbstractTool implements ITool {
	public static final int SNAP_SIZE = 4;
	
	private boolean enabled = true;
	private boolean exclusive = false;

	@Override
	public boolean isExclusive() {
		return exclusive;
	}
	
	@Override
	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
	
	@Override
	public final boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	// draw routine
	@Override
	public void draw3D(GL2 gl, IView view) {
	}
	
	@Override
	public void draw2D(GL2 gl, IView view) {
	}
	

	// key listener

	@Override
	public void keyPressed(KeyEvent e, IView view) {
	}

	// mouse listener

	@Override
	public void mousePressed(MouseEvent e, IView view) {
	}

	@Override
	public void mouseReleased(MouseEvent e, IView view) {
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
	
	
	public static final boolean snap2D(int mx, int my, int x, int y) {
		if ((mx >= x - SNAP_SIZE) && (mx <= x + SNAP_SIZE) && (my >= y - SNAP_SIZE) && (my < y + SNAP_SIZE))
			return true;
		return false;
	}	
}
