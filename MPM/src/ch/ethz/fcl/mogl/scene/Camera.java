package ch.ethz.fcl.mogl.scene;

public class Camera {
	private static final double MIN_DISTANCE = 1.0;
	private static final double MAX_DISTANCE = 80.0;
	
	private double near = 0.1;
	// public double far = 1000.0;
	private double far = Double.POSITIVE_INFINITY;

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
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
		distance = Math.max(MIN_DISTANCE, distance);
		distance = Math.min(MAX_DISTANCE, distance);
	}
	
	public void addToDistance(double delta) {
		distance += delta;
		distance = Math.max(MIN_DISTANCE, distance);
		distance = Math.min(MAX_DISTANCE, distance);
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
		this.rotateX = rotateX;
	}

	public void addToRotateX(double delta) {
		rotateX += delta;
	}

	public double getTranslateX() {
		return translateX;
	}
	
	public void setTranslateX(double translateX) {
		this.translateX = translateX;
	}
	
	public void addToTranslateX(double delta) {
		translateX += delta;
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
}
