package algorithm;

public class Point {
	
	
	private double x;
	private double y;
	private double z;
	private double distance;
	private double error;
	
	public Point(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x=x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y=y;
	}

	public double getZ() {
		return z;
	}


	public void setZ(double z) {
		this.z = z;
	}

	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	
	public double getError() {
		return error;
	}
	
	public void setError(double error) {
		this.error = error;
	}
	
}
