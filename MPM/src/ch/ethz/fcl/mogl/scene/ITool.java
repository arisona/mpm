package ch.ethz.fcl.mogl.scene;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL2;

public interface ITool {
	boolean isEnabled();
	void setEnabled(boolean enabled);

	boolean isExclusive();
	void setExclusive(boolean exclusive);
	
	void draw3D(GL2 gl, IView view);
	void draw2D(GL2 gl, IView view);
	
	// key listener

	void keyPressed(KeyEvent e, IView view);

	// mouse listener

	void mousePressed(MouseEvent e, IView view);

	void mouseReleased(MouseEvent e, IView view);

	// mouse motion listener

	void mouseMoved(MouseEvent e, IView view);

	void mouseDragged(MouseEvent e, IView view);

	// mouse wheel listener
	
	void mouseWheelMoved(MouseWheelEvent e, IView view);
}
