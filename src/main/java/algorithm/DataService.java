package algorithm;

import com.jme3.math.Quaternion;
import inputOutput.AbstractTrackingDataSource;

import java.util.List;

/**
 * The class DataService represents the interface to team 1
 * Access to calculation with trackingdata over DataProcessor.
 * Access to trackingtool data and its source over DataManager.
 */

public class DataService {
    private final TrackingDataProcessor dataProcessor;
    private TrackingDataManager dataManager;

    public DataService() {
        dataManager = new TrackingDataManager();
        dataProcessor = new TrackingDataProcessor();
    }

    public DataService(AbstractTrackingDataSource source) {
        dataManager = new TrackingDataManager();
        dataManager.setSource(source);
        dataProcessor = new TrackingDataProcessor();
    }

    public void setTrackingDataSource(AbstractTrackingDataSource source) {
        dataManager.setSource(source);
    }

    /**
     * Restarts all measurements: resets
     * the internal list of tools.
     */
    public void restartMeasurements() {
        dataManager.restartMeasurements();
    }

    public TrackingDataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(TrackingDataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * This method checks if the tool exists. A for-loop is used to go
     * through the list of tools of type ToolMeasure. If the name of the
     * searched tool is the same as in the list then the tool will be
     * returned, if there is no tool with the name, the exception is thrown.
     *
     * @return toolMeasure - of type ToolMeasure
     * @exception
     */

    public ToolMeasure getToolByName(String name) throws Exception {
        for(ToolMeasure toolMeasure : dataManager.getToolMeasures()) {
            if(toolMeasure.getName().equals(name)) {
                return toolMeasure;
            }
        }
        /* If the tool doesn't exists, the exception is thrown */
        throw new Exception("Tool not found: " + name);
    }

    /**
     * This method checks if the tool exists and returns the average
     * measurement of all collected data of that tool
     *
     * @return average measurement of that tool
     * @throws throws an exception if the tool was not found
     */
    public AverageMeasurement getAverageMeasurement(String name)
            throws Exception {
        return getToolByName(name).getAverageMeasurement();
    }

    /**
     * The method loadNextData gets a number of how much data should be
     * reloaded. In a loop the tools of DataMangager are brought. A list of
     * measurements is created. AverageMeasurment is called and calculated.
     * LoadNextData calls different methods and computes them. The results are
     * added to average Measurement..
     *
     * @param countToGetNext - number of the reloaded data
     * @return dataManager.getToolMeasures() - list of tools of type ToolMesure
     */
    public List<ToolMeasure> loadNextData(int countToGetNext) {

        /* calls method getNextData */
        dataManager.getNextData(countToGetNext);
        return dataManager.getToolMeasures();
    }

    public List<ToolMeasure> loadNextData(int countToGetNext, boolean updateSource) {

        if(updateSource) dataManager.getSource().update();
        /* calls method getNextData */
        dataManager.getNextData(countToGetNext);
        return dataManager.getToolMeasures();
    }

    /**
     * This methods calculates the correctness of the position. The expected
     * distance can be entered via the surface. On the surface the desired
     * measurements can be selected. In class DataProcessor the method get Accuracy
     * is called.
     *
     * @param expectedDistance         - of type double
     * @param firstAverangeMeasurement - of type AverageMeasurement
     * @return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement,
     *secondAverangeMeasurement) -
     */

    public double getAccuracy(double expectedDistance,
                              AverageMeasurement firstAverangeMeasurement,
                              AverageMeasurement secondAverangeMeasurement) {
        /* method getAccuracy from class DataProcessor */
        return dataProcessor.getAccuracy(expectedDistance, firstAverangeMeasurement,
                              secondAverangeMeasurement);
    }

    /**
     * On the surface getAccuraryRotation can be selected. A method of DataProcessor
     * is called and a quaternion is returned.
     *
     * @param expectedRotation  - of type Quaternion
     * @param firstMeasurement  - of type Measurement
     * @param secondMeasurement - of type Measurement
     * @return dataProcessor.getAccuracyRotation(expectedRotation, firstMeasurement,
     *secondMeasurement) -
     */

    public Quaternion getAccuracyRotation(Quaternion expectedRotation,
                                          Measurement firstMeasurement,
                                          Measurement secondMeasurement) {
        return dataProcessor.getAccuracyRotation(expectedRotation, firstMeasurement,
                             secondMeasurement);
    }
}
