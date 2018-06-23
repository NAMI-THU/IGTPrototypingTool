package algorithm;

import java.util.List;

public class DataService {
	private DataProcessor dataProcessor;
	private DataManager dataManager;

	public DataService() {
		dataManager = new DataManager();
		dataProcessor = new DataProcessor();
	}

	/** This method checks if the tool exists */
	public ToolMeasure getToolByName(String Name) throws Exception {
		for (ToolMeasure toolMeasure : dataManager.getToolMeasures()) {
			if (toolMeasure.getName().equals(Name)) {
				return toolMeasure;
			}
		}
		/** If the tool doesn't exists, the exception is thrown*/
		throw new Exception("Tool not found: " + Name);
	}

	public List<ToolMeasure> loadNextData(int countToGetNext) {
		
		/** calls method getNextData */
		dataManager.getNextData(countToGetNext);

		for (ToolMeasure toolMeasure : dataManager.getToolMeasures()) {

			/** creation of a list of measurements */
			List<Measurement> mes = toolMeasure.getMeasurement();
			/** average measurement is calculated */
			AverageMeasurement avgMes = dataProcessor.getAverageMeasurement(mes);
			
			/** from the average measurement different calculations */
			avgMes.setErrors(dataProcessor.getErrors(mes, avgMes.getPoint()));
			avgMes.setError(dataProcessor.getJitter(avgMes.getErrors()));
			avgMes.setRotationError(dataProcessor.getRotationJitter(mes, avgMes.getRotation()));
			avgMes.setBoxPlot(dataProcessor.getBoxPlot(avgMes.getErrors()));

			toolMeasure.setAverageMeasurement(avgMes);
		}

		return dataManager.getToolMeasures();
	}

	
	/** This methods calculates the correctness of the position */
	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		/** method getAccuracy from class DataProcessor */
		return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);
	}

	public double getAccuracyRotation(double expectedAngle, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		return dataProcessor.getAccuracyRotation(expectedAngle, firstMeasurement, secondMeasurement);
	}
}
