package algorithm;

import inputOutput.TempTool;
import inputOutput.AbstractTrackingDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class TrackingDataManager represents the interface between the GUI and
 * data from InputOutput. It manages all collected tracking data.
 */
public class TrackingDataManager {

    List<Tool> tools = new ArrayList<>();
    private AbstractTrackingDataSource source;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public List<Tool> getToolMeasures() {
        return tools;
    }

    /**
     * Restarts all measurements: resets
     * the internal list of tools.
     */
    public void restartMeasurements() {
        tools = new ArrayList<>();
    }

    /**
     * The method getNextData calls method update, which is from inputOutput,
     * creates from return value of update a measurement and adds this tool
     *
     * @param countToGetNext - number of the reloaded data
     * @param countToGetNext ,
     * @return toolMeasures
     */
    public List<Tool> getNextData(int countToGetNext) {

        if (source == null) {
            logger.log(Level.WARNING,"Tracking data source is not set. Aborting!");
            return tools;
        }

        for (double i = 1; i <= countToGetNext; i++) {
            /* from return value of update a new measurement will be created */
            List<TempTool> tempTools = source.getLastToolList();

            if (tempTools.isEmpty()) {
                logger.log(Level.WARNING,"Toollist is empty.");
                break;
            }

            for (TempTool tempTool : tempTools) {
                Measurement measurement = new Measurement(tempTool);
                addMeasurementToTool(measurement);
            }

        }

        return tools;
    }

    public AbstractTrackingDataSource getSource() {
        return source;
    }

    /**
     * @param source Sets the TrackingDataSource that is used to get all data.
     */
    public void setSource(AbstractTrackingDataSource source) {
        this.source = source;
    }

    /**
     * This methods manages the tools. AddMeasurementToTool controls if a tool
     * with this name exists. If there is already a tool with this name, then
     * the method adds the new measurements to this tool. If there is no
     * tool with this name,then a new tool is created
     *
     * @param measurement - variable of type Measurement
     */

    private void addMeasurementToTool(Measurement measurement) {

        /* Check if tool exists */
        for (Tool tool : tools) {
            if (tool.getName().equals(measurement.getToolname())) {

                /* added new measurements to the tool */
                tool.addMeasurement(measurement);
                return;
            }
        }

        /* creation of a new tool */
        Tool newTool = new Tool(measurement.getToolname());
        newTool.addMeasurement(measurement);
        tools.add(newTool);
    }
}
