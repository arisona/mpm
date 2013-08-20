package ch.ethz.fcl.mogl.mapping;

import javax.media.opengl.GL2;

import ch.ethz.fcl.mogl.scene.AbstractTool;
import ch.ethz.fcl.mogl.scene.IView;
import ch.ethz.fcl.mogl.scene.IView.ViewType;

public final class FillTool extends AbstractTool {
	// @formatter:off
	public static final String[] FILL_HELP = {
		"[0] Return"
	};
	// @formatter:on

	public FillTool() {
		setExclusive(true);
	}

	@Override
	public void draw2D(GL2 gl, IView view) {
		if (view.getViewType() != ViewType.MAPPED_VIEW)
			return;
		gl.glColor4f(1, 1, 1, 1);
		gl.glRectd(-1, -1, -0.1, -0.1);
		gl.glRectd(1, -1, 0.1, -0.1);
		gl.glRectd(-1, 1, -0.1, 0.1);
		gl.glRectd(1, 1, 0.1, 0.1);
		gl.glRectd(-0.01, -1, 0.01, 1);
		gl.glRectd(-1, -0.01, 1, 0.01);
	}
}
