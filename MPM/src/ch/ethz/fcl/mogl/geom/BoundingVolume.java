package ch.ethz.fcl.mogl.geom;

import java.util.Collection;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public final class BoundingVolume {
	boolean valid;
	double minX;
	double maxX;
	double minY;
	double maxY;
	double minZ;
	double maxZ;

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

	public double getMinZ() {
		return minZ;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public double getExtentX() {
		return maxX - minX;
	}

	public double getExtentY() {
		return maxY - minY;
	}

	public double getExtentZ() {
		return maxZ - minZ;
	}

	public void add(float x, float y, float z) {
		add((double)x, (double)y, (double)z);
	}

	public void add(double x, double y, double z) {
		minX = Math.min(minX, x);
		maxX = Math.max(maxX, x);
		minY = Math.min(minY, y);
		maxY = Math.max(maxY, y);
		minZ = Math.min(minZ, z);
		maxZ = Math.max(maxZ, z);
		valid = true;
	}

	public void add(Vector3D point) {
		add(point.getX(), point.getY(), point.getZ());
	}
	
	public void add(Collection<Vector3D> points) {
		for (Vector3D point : points)
			add(point);
	}
	
	public void add(float[] points) {
		for (int i = 0; i < points.length; i += 3) {
			add(points[i], points[i+1], points[i+2]);
		}
	}

	public void add(double[] points) {
		for (int i = 0; i < points.length; i += 3) {
			add(points[i], points[i+1], points[i+2]);
		}
	}

	public void add(BoundingVolume b) {
		add(b.minX, b.minY, b.minZ);
		add(b.maxX, b.maxY, b.maxZ);
	}
	
	@Override
	public String toString() {
		return valid ? "[" + minX + "," + maxX + "][" + minY + "," + maxY + "][" + minZ + "," + maxZ + "]" : "invalid";
	}
}
