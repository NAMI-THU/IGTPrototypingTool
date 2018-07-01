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

	
	public void setDataManager(DataManager dataManager){
		this.dataManager = dataManager;
	}
	
	public DataManager getDataManager(){
		return dataManager;
	}
	
	/**
	 * This method checks if the tool exists. A for-loop is used to go through the
	 * list of tools of type ToolMeasure. If the name of the searched tool is the
	 * same as in the list then the tool will be returned, if there is no tool with
	 * the name, the exception will be thrown.
	 * 
	 * @param Name @return toolMeasure @exception
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
	 * 
	 * @param countToGetNext
	 * @return
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
	 * This methods calculates the correctness of the position
	 * 
	 * @param expectedDistance
	 * @param firstAverangeMeasurement
	 * @return
	 */

	public double getAccuracy(double expectedDistance, AverageMeasurement firstAverangeMeasurement,
			AverageMeasurement secondAverangeMeasurement) {
		/* method getAccuracy from class DataProcessor */
		return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement, secondAverangeMeasurement);
	}

	/**
	 * 
	 * @param expectedAngle
	 * @param firstMeasurement
	 * @return
	 */

	public Quaternion getAccuracyRotation(Quaternion expectedRotation, Measurement firstMeasurement,
			Measurement secondMeasurement) {
		return dataProcessor.getAccuracyRotation(expectedRotation, firstMeasurement, secondMeasurement);
	}
}