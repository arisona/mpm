package ch.ethz.fcl.mogl.scene;

import javax.media.opengl.GL2;

import ch.ethz.fcl.mogl.gl.DrawingUtils;

public class NavigationGrid {
	public static final float[] AXIS_COLOR = { 1.0f, 1.0f, 1.0f, 0.75f };
	public static final float[] GRID_COLOR = { 1.0f, 1.0f, 1.0f, 0.5f };

	private int numGridLines;
	private float gridSpacing;

	private float[] axisLines;
	private float[] gridLines;

	public NavigationGrid(int numGridLines, float gridSpacing) {
		this.numGridLines = numGridLines;
		this.gridSpacing = gridSpacing;
	}

	protected void render(GL2 gl, IView view) {
		gl.glColor4fv(NavigationGrid.AXIS_COLOR, 0);
		DrawingUtils.drawLines(gl, getAxisLines());

		DrawingUtils.drawText3D(view, "X", (double)getAxisLines()[3], (double)getAxisLines()[4], (double)getAxisLines()[5]);
		DrawingUtils.drawText3D(view, "Y", (double)getAxisLines()[9], (double)getAxisLines()[10], (double)getAxisLines()[11]);

		gl.glColor4fv(NavigationGrid.GRID_COLOR, 0);
		DrawingUtils.drawLines(gl, getGridLines());		
	}
	
	public float[] getAxisLines() {
		if (axisLines == null) {
			float e = 0.5f * gridSpacing * (numGridLines + 1);
			axisLines = new float[] { -e, 0, 0, e, 0, 0, 0, -e, 0, 0, e, 0 };
		}
		return axisLines;
	}

	public float[] getGridLines() {
		if (gridLines == null) {
			gridLines = new float[numGridLines * 3 * 2 * 2];
			float e = 0.5f * gridSpacing * (numGridLines + 1);
			int n = numGridLines / 2;
			int i = 0;
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = -e;
				gridLines[i++] = 0;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = e;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = -e;
				gridLines[i++] = 0;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = e;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -e;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = 0;
				gridLines[i++] = e;
				gridLines[i++] = j * gridSpacing;
				gridLines[i++] = 0;
			}
			for (int j = 1; j <= n; ++j) {
				gridLines[i++] = -e;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = 0;
				gridLines[i++] = e;
				gridLines[i++] = -j * gridSpacing;
				gridLines[i++] = 0;
			}
		}
		return gridLines;
	}
	
	public float getExtentX() {
		return gridSpacing * numGridLines;
	}

	public float getExtentY() {
		return gridSpacing * numGridLines;
	}	
}
