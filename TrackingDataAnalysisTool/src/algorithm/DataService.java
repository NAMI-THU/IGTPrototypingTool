package algorithm;

import java.util.List;

public class DataService {
	private DataProcessor dataProcessor;
	
	
	
	public ToolMeasure getAverageMeasurement(ToolMeasure tool) {
		this.dataProcessor.getAverageMeasurement(tool);
		return tool;
	}
	
	public ToolMeasure getJitter(ToolMeasure tool){
		this.dataProcessor.getJitter(tool);
		return tool;
	}
	
	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement, AverageMeasurement secondAverangeMeasurement) {
		return this.dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);
		
	}
	
	public double getAccuracyRotation(double expectedAngle, Measurement firstMeasurement, Measurement secondMeasurement) {
		return this.dataProcessor.getAccuracyRotation(expectedAngle, firstMeasurement, secondMeasurement);
	}
	
}
