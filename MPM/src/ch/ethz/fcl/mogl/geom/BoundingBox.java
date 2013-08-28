package ch.ethz.fcl.mogl.geom;

public final class BoundingBox {
	boolean valid;
	float minX;
	float maxX;
	float minY;
	float maxY;

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

	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxY() {
		return maxY;
	}

	public float getExtentX() {
		return maxX - minX;
	}

	public float getExtentY() {
		return maxY - minY;
	}

	public void add(float x, float y) {
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
