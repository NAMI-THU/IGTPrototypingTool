package algorithm;

import java.util.List;

public class Measurement {
	
	private List<Point> points;
	private int timestamp;
	private String toolname;
	
	public List <Point> getPoints(){
		return points;
	}
	
	public void setPoints(List<Point> points) {
		this.points=points;
	}
	
	public int getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(int timestamp) {
		this.timestamp=timestamp;
	}

	public String getToolname() {
		return toolname;
	}
	
	public void setToolname(String toolname) {
		this.toolname=toolname;
	}
	
}
