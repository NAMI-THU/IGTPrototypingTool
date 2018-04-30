package algorithm;

public class Jitter {
	private Point referencePoint;
	private double jitter;
	
	public Jitter(Point referencePoint, double jitter) {
		super();
		this.referencePoint = referencePoint;
		this.jitter = jitter;
	}
	
	public Point getReferencePoint() {
		return referencePoint;
	}
	public void setReferencePoint(Point referencePoint) {
		this.referencePoint = referencePoint;
	}
	
	public double getJitter() {
		return jitter;
	}
	public void setJitter(double jitter) {
		this.jitter = jitter;
	}
	
	
}
