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
package ch.ethz.fcl.mogl.scene;

import ch.ethz.fcl.mogl.geom.BoundingVolume;
import ch.ethz.fcl.util.MathUtils;

public class Camera {
	private static final boolean KEEP_ROT_X_POSITIVE = true;
	
	private double near = 0.1;
	// public double far = 1000.0;
	private double far = Double.POSITIVE_INFINITY;

	private double fov = 45.0;
	private double distance = 2.0;
	private double rotateZ = 0.0;
	private double rotateX = 45.0;
	private double translateX = 0.0;
	private double translateY = 0.0;

	public Camera() {

	}

	public double getNearClippingPlane() {
		return near;
	}

	public double getFarClippingPlane() {
		return far;
	}
	
	public double getFOV() {
		return fov;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
		distance = Math.max(near, distance);
		distance = Math.min(far, distance);
	}

	public void addToDistance(double delta) {
		distance += distance / 10.0 * delta;
		distance = Math.max(near, distance);
		distance = Math.min(far, distance);
	}

	public double getRotateZ() {
		return rotateZ;
	}

	public void setRotateZ(double rotateZ) {
		this.rotateZ = rotateZ;
	}

	public void addToRotateZ(double delta) {
		rotateZ += delta;
	}

	public double getRotateX() {
		return rotateX;
	}

	public void setRotateX(double rotateX) {
		this.rotateX = MathUtils.clamp(rotateX, KEEP_ROT_X_POSITIVE ? 0.0 : -90.0, 90.0);
	}

	public void addToRotateX(double delta) {
		this.rotateX = MathUtils.clamp(rotateX + delta, KEEP_ROT_X_POSITIVE ? 0.0 : -90.0, 90.0);
	}

	public double getTranslateX() {
		return translateX;
	}

	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}

	public void addToTranslateX(double delta) {
		translateX += distance / 10.0 * delta;
	}

	public double getTranslateY() {
		return translateY;
	}

	public void setTranslateY(double translateY) {
		this.translateY = translateY;
	}

	public void addToTranslateY(double delta) {
		translateY += delta;
	}
	
	public void frame(BoundingVolume bounds) {
		double extent = 1.5 * Math.max(Math.max(bounds.getExtentX(), bounds.getExtentY()), bounds.getExtentZ());
		double d = 0.5 * extent / Math.tan(Math.toRadians(fov/2));
		setDistance(d);
		// FIXME hack, assume centered model for now
		setTranslateX(0);
		setTranslateY(0);
	}
}
