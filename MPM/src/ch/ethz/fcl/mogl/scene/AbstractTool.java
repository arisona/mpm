package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public abstract class AbstractTool implements ITool {
	public static final int SNAP_SIZE = 4;

	public static final boolean snap2D(int mx, int my, int x, int y) {
		if ((mx >= x - SNAP_SIZE) && (mx <= x + SNAP_SIZE) && (my >= y - SNAP_SIZE) && (my < y + SNAP_SIZE))
			return true;
		return false;
	}	

	
	// key listener

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			System.exit(0);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e, IView view) {
	}

	@Override
	public void keyTyped(KeyEvent e, IView view) {
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
