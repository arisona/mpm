/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
