package algorithm;

import java.util.List;

public class DataService {
	private DataProcessor dataProcessor;
	private DataManager dataManager;
	
	public DataService() {
		dataManager = new DataManager();
		dataProcessor = new DataProcessor();
	}
	
	public ToolMeasure getToolByName(String Name) throws Exception{
		for(ToolMeasure toolMeasure : dataManager.getToolMeasures()) {
			if(toolMeasure.getName().equals(Name)) {
				return toolMeasure;
			}
		}
		throw new Exception("Tool not found: " + Name);
	}
	
	public List<ToolMeasure> loadNextData(int countToGetNext) {
		dataManager.getNextData(countToGetNext);
		
		for(ToolMeasure toolMeasure : dataManager.getToolMeasures()) {
			
			List<Measurement> mes = toolMeasure.getMeasurement();
			AverageMeasurement avgMes = dataProcessor.getAverageMeasurement(mes);
			
			avgMes.setErrors(dataProcessor.getErrors(mes, avgMes.getPoint()));
			avgMes.setError(dataProcessor.getJitter(avgMes.getErrors()));
			avgMes.setRotationError(dataProcessor.getRotationJitter(mes, avgMes.getRotation()));
			avgMes.setBoxPlot(dataProcessor.getBoxPlot(avgMes.getErrors()));
			
			toolMeasure.setAverageMeasurement(avgMes);
		}
		
		return dataManager.getToolMeasures();
	}
	
	
	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement, AverageMeasurement secondAverangeMeasurement) {
		return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);
	}
	
	public double getAccuracyRotation(double expectedAngle, Measurement firstMeasurement, Measurement secondMeasurement) {
		return dataProcessor.getAccuracyRotation(expectedAngle, firstMeasurement, secondMeasurement);
	}
}
