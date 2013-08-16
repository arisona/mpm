package ch.ethz.fcl.mogl.scene;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class NavigationTool extends AbstractTool {
	private static final double CAMERA_TRANSLATE_SCALE = 0.01;
	private static final double CAMERA_ROTATE_SCALE = 1.0;

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
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e, IView view) {
		view.getCamera().addToDistance(0.25 * e.getWheelRotation());
		view.repaint();
	}
}
