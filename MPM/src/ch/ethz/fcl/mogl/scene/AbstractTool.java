package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class AbstractTool<T extends IView> implements ITool<T> {
	public static final int SNAP_SIZE = 4;

	public static final boolean snap2D(int mx, int my, int x, int y) {
		if ((mx >= x - SNAP_SIZE) && (mx <= x + SNAP_SIZE) && (my >= y - SNAP_SIZE) && (my < y + SNAP_SIZE))
			return true;
		return false;
	}	

	
	// key listener

	@Override
	public void keyPressed(KeyEvent e, T view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e, T view) {
	}

	@Override
	public void keyTyped(KeyEvent e, T view) {
	}

	// mouse listener

	@Override
	public void mouseEntered(MouseEvent e, T view) {
	}

	@Override
	public void mouseExited(MouseEvent e, T view) {
	}

	@Override
	public void mousePressed(MouseEvent e, T view) {
	}

	@Override
	public void mouseReleased(MouseEvent e, T view) {
	}

	@Override
	public void mouseClicked(MouseEvent e, T view) {
	}

	// mouse motion listener

	@Override
	public void mouseMoved(MouseEvent e, T view) {
	}

	@Override
	public void mouseDragged(MouseEvent e, T view) {
	}

	// mouse wheel listener

	@Override
	public void mouseWheelMoved(MouseWheelEvent e, T view) {
	}
}
