package ch.ethz.fcl.mogl.geom;

public final class BoundingBox {
	boolean valid;
	double minX;
	double maxX;
	double minY;
	double maxY;

	public BoundingBox() {
		reset();
	}

	public void reset() {
		valid = false;
		minX = Float.POSITIVE_INFINITY;
		maxX = Float.NEGATIVE_INFINITY;
		minY = Float.POSITIVE_INFINITY;
		maxY = Float.NEGATIVE_INFINITY;
	}

	public boolean isValid() {
		return valid;
	}

	public double getMinX() {
		return minX;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getExtentX() {
		return maxX - minX;
	}

	public double getExtentY() {
		return maxY - minY;
	}

	public void add(double x, double y) {
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		valid = true;
	}

	public void add(BoundingBox b) {
		add(b.minX, b.minY);
		add(b.maxX, b.maxY);
	}
	
	@Override
	public String toString() {
		return valid ? "[" + minX + "," + maxX + "][" + minY + "," + maxY + "]" : "invalid";
	}	
}
