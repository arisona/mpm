package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

interface ITool<T extends IView> {
	// key listener

	void keyPressed(KeyEvent e, T view);

	void keyReleased(KeyEvent e, T view);

	void keyTyped(KeyEvent e, T view);

	// mouse listener

	void mouseEntered(MouseEvent e, T view);

	void mouseExited(MouseEvent e, T view);

	void mousePressed(MouseEvent e, T view);

	void mouseReleased(MouseEvent e, T view);

	void mouseClicked(MouseEvent e, T view);

	// mouse motion listener

	void mouseMoved(MouseEvent e, T view);

	void mouseDragged(MouseEvent e, T view);

	void mouseWheelMoved(MouseWheelEvent e, T view);
}
