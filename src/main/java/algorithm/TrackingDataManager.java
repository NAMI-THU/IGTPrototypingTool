package algorithm;

import inputOutput.Tool;
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

    List<ToolMeasure> toolMeasures = new ArrayList<>();
    private AbstractTrackingDataSource source;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public List<ToolMeasure> getToolMeasures() {
        return toolMeasures;
    }

    /**
     * Restarts all measurements: resets
     * the internal list of tools.
     */
    public void restartMeasurements() {
        toolMeasures = new ArrayList<>();
    }

    /**
     * The method getNextData calls method update, which is from inputOutput,
     * creates from return value of update a measurement and adds this tool
     *
     * @param countToGetNext - number of the reloaded data
     * @param countToGetNext ,
     * @return toolMeasures
     */
    public List<ToolMeasure> getNextData(int countToGetNext) {

        if (source == null) {
            logger.log(Level.WARNING,"Tracking data source is not set. Aborting!");
            return toolMeasures;
        }

        for (double i = 1; i <= countToGetNext; i++) {
            /* from return value of update a new measurement will be created */
            List<Tool> tools = source.getLastToolList();

            if (tools.isEmpty()) {
                logger.log(Level.WARNING,"Toollist is empty.");
                break;
            }

            for (Tool tool : tools) {
                Measurement measurement = new Measurement(tool);
                addMeasurementToTool(measurement);
            }

        }

        return toolMeasures;
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
        for (ToolMeasure toolMeasure : toolMeasures) {
            if (toolMeasure.getName().equals(measurement.getToolname())) {

                /* added new measurements to the tool */
                toolMeasure.addMeasurement(measurement);
                return;
            }
        }

        /* creation of a new tool */
        ToolMeasure newTool = new ToolMeasure(measurement.getToolname());
        newTool.addMeasurement(measurement);
        toolMeasures.add(newTool);
    }
}
