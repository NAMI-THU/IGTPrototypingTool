package algorithm;

import java.util.List;

public class DataService {
	private DataManager dataManager;
	private DataProcessor dataProcessor;
	
	//Constructor 
	public DataService() {
	
	}
	
	public String loadNextData() {
		
		this.dataManager.getNextData();
		 return null;
		
	}
	
	public ToolMeasure getAverageMeasurement(ToolMeasure tool) {
		this.dataProcessor.getAverageMeasurement(tool);
		return tool;
	}
	
	public ToolMeasure getJitter(ToolMeasure tool){
		this.dataProcessor.getJitter(tool);
		return tool;
	}
	
	public double getAccuracy(double expectedDistance, List<Point> firstPoints, List<Point> secondPoints) {
		return this.dataProcessor.getAccuracy(expectedDistance, firstPoints, secondPoints);
		
	}
	
	
}
