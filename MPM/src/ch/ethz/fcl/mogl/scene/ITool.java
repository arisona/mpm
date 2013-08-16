package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface ITool {
	// key listener

	void keyPressed(KeyEvent e, IView view);

	void keyReleased(KeyEvent e, IView view);

	void keyTyped(KeyEvent e, IView view);

	// mouse listener

	void mouseEntered(MouseEvent e, IView view);

	void mouseExited(MouseEvent e, IView view);

	void mousePressed(MouseEvent e, IView view);

	void mouseReleased(MouseEvent e, IView view);

	void mouseClicked(MouseEvent e, IView view);

	// mouse motion listener

	void mouseMoved(MouseEvent e, IView view);

	void mouseDragged(MouseEvent e, IView view);

	void mouseWheelMoved(MouseWheelEvent e, IView view);
}
