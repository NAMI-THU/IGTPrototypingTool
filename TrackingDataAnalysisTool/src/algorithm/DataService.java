package algorithm;

import java.util.List;

/** The class DataService represents the interface to team 1 */
import com.jme3.math.Quaternion;

public class DataService {
	private DataProcessor dataProcessor;
	private DataManager dataManager;

	public DataService() {
		dataManager = new DataManager();
		dataProcessor = new DataProcessor();
	}

	/**
	 * This method checks if the tool exists. A for-loop is used to go through the
	 * list of tools of type ToolMeasure. If the name of the searched tool is the
	 * same as in the list then the tool will be returned, if there is no tool with
	 * the name, the exception will be thrown.
	 * 
	 * @param Name -  name from a tool of type String
	 * @return toolMeasure - of type ToolMeasure
	 * @exception
	 */

	public ToolMeasure getToolByName(String Name) throws Exception {
		for (ToolMeasure toolMeasure : dataManager.getToolMeasures()) {
			if (toolMeasure.getName().equals(Name)) {
				return toolMeasure;
			}
		}
		/* If the tool doesn't exists, the exception is thrown */
		throw new Exception("Tool not found: " + Name);
	}

	/**
	 * The method loadNextData gets a number of how much data should be reloaded. 
 	 * In a loop the tools of DataMangager are brought. A list of measurements is created. AverageMeasurment is called and calculated.
 	 * LoadNextData calls different methods and computes them. The results where added to average Measurement..
	 * 
	 * @param countToGetNext - number of the reloaded data
	 * @return dataManager.getToolMeasures() - list of tools of type ToolMesure
	 */
	public List<ToolMeasure> loadNextData(int countToGetNext) {

		/* calls method getNextData */
		dataManager.getNextData(countToGetNext);

		for (ToolMeasure toolMeasure : dataManager.getToolMeasures()) {

			/* creation of a list of measurements */
			List<Measurement> mes = toolMeasure.getMeasurement();
			/* average measurement is calculated */
			AverageMeasurement avgMes = dataProcessor.getAverageMeasurement(mes);

			/* from the average measurement different calculations */
			avgMes.setErrors(dataProcessor.getErrors(mes, avgMes.getPoint()));
			avgMes.setError(dataProcessor.getJitter(avgMes.getErrors()));
			avgMes.setRotationError(dataProcessor.getRotationJitter(mes, avgMes.getRotation()));
			avgMes.setBoxPlot(dataProcessor.getBoxPlot(avgMes.getErrors()));

			toolMeasure.setAverageMeasurement(avgMes);
		}

		return dataManager.getToolMeasures();
	}

	/**
	 * This methods calculates the correctness of the position. The expected distance can be entered via the surface. On the surface
	 * the desired measurements can be selected. In class DataProcessor the method get Accuracy is called. 
	 * 
	 * @param expectedDistance - of type double
	 * @param firstAverangeMeasurement - of type AverageMeasurement
	 * @return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement) - 
	 */

	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		/* method getAccuracy from class DataProcessor */
		return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);
	}

	/** On the surface getAccuraryRotation can be selected. A method of DataProcessor is called and a 
	 * quaternion is returned. 
	 * 
	 * @param expectedRotation - of type quaternion
	 * @param firstMeasurement - of type Measurement
	 * @param secondMeasurement - of type Measuremnt
	 * @return dataProcessor.getAccuracyRotation(expectedRotation, firstMeasurement, secondMeasurement) - 
	 */

	public Quaternion getAccuracyRotation(Quaternion expectedRotation, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		return dataProcessor.getAccuracyRotation(expectedRotation, firstMeasurement, secondMeasurement);
	}
}