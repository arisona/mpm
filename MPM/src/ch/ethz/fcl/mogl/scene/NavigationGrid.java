package ch.ethz.fcl.mogl.scene;

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
