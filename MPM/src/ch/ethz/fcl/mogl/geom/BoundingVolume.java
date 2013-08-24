package ch.ethz.fcl.mogl.geom;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public final class BoundingVolume {
	boolean valid;
	float minX;
	float maxX;
	float minY;
	float maxY;
	float minZ;
	float maxZ;

	public BoundingVolume() {
		reset();
	}

	public void reset() {
		valid = false;
		minX = Float.POSITIVE_INFINITY;
		maxX = Float.NEGATIVE_INFINITY;
		minY = Float.POSITIVE_INFINITY;
		maxY = Float.NEGATIVE_INFINITY;
		minZ = Float.POSITIVE_INFINITY;
		maxZ = Float.NEGATIVE_INFINITY;
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

	public float getMinZ() {
		return minZ;
	}

	public float getMaxZ() {
		return maxZ;
	}

	public float getExtentX() {
		return maxX - minX;
	}

	public float getExtentY() {
		return maxY - minY;
	}

	public float getExtentZ() {
		return maxZ - minZ;
	}

	public void add(float x, float y, float z) {
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		minZ = Math.min(minZ, z);
		maxZ = Math.max(maxZ, z);
		valid = true;
	}

	public void add(Vector3D v) {
		add((float) v.getX(), (float) v.getY(), (float) v.getZ());
	}

	public void add(BoundingVolume b) {
		add(b.minX, b.minY, b.minZ);
		add(b.maxX, b.maxY, b.maxZ);
	}
}
