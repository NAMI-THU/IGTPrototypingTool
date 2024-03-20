package algorithm;

import inputOutput.AbstractTrackingDataSource;

import java.util.List;

/**
 * The class DataService represents the interface to team 1
 * Access to calculation with trackingdata over DataProcessor.
 * Access to trackingtool data and its source over DataManager.
 */

public class DataService {
    private TrackingDataManager dataManager;

    public DataService() {
        dataManager = new TrackingDataManager();
    }

    public DataService(AbstractTrackingDataSource source) {
        dataManager = new TrackingDataManager();
        dataManager.setSource(source);
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
     * @exception Exception if tool is not found
     */

    public Tool getToolByName(String name) throws Exception {
        for(Tool tool : dataManager.getToolMeasures()) {
            if(tool.getName().equals(name)) {
                return tool;
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
     * @throws Exception an exception if the tool was not found
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
    public List<Tool> loadNextData(int countToGetNext) {

        /* calls method getNextData */
        dataManager.getNextData(countToGetNext);
        return dataManager.getToolMeasures();
    }

    public List<Tool> loadNextData(int countToGetNext, boolean updateSource) {

        if(updateSource) dataManager.getSource().update();
        /* calls method getNextData */
        dataManager.getNextData(countToGetNext);
        return dataManager.getToolMeasures();
    }
}
