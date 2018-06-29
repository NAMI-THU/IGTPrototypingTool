package algorithm;

import java.util.ArrayList;
import java.util.List;

import inputOutput.CSVFileReader;

import inputOutput.Tool;

/** The class DataManager represents the interface to team 3 and manages our data */
public class DataManager {
	List<ToolMeasure> toolMeasures = new ArrayList<>();
	private boolean count = false;

	public List<ToolMeasure> getToolMeasures() {
		return toolMeasures;
	}

	/** The method getNextData calls method update, which is from inputOutput, creates from return value of update a measurement and adds this tool 
	 * 
	 * @param countToGetNext, 
	 * @return toolMeasures
	 * */

	public List<ToolMeasure> getNextData(int countToGetNext) {

		for (double i = 1; i <= countToGetNext; i++) {
			/* from return value of update a new measurement will be created */
			List<Tool> tools = inputOutput.CSVFileReader.update();
			
			if (tools.isEmpty()) {
				inputOutput.ExceptionData.checkException();
				
				break;
			}

			for (Tool tool : tools) {
				Measurement measurement = new Measurement(tool);
				addMeasurementToTool(measurement);
			}

		}

		return toolMeasures;
	}

	/** This methods manages the tools. AddMeasurementToTool controlls if a tool with this name exists. If there is already a tool with this name, then the method 
	 * 	added the new measurements to this tool. If there is no tool with this name, then there will a new tool be created 
	 * 
	 * @param measurement
	 * @return
	 * */

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

	/** This method is called by inputOutput
	 * 
	 *  @param test
	 *  @return 
	 *  */

	public void setList(ArrayList<Tool> test) {

		if (count == true) {

			List<Tool> tools = test;
			for (Tool tool : tools) {
				Measurement measurement = new Measurement(tool);
				addMeasurementToTool(measurement);
			}

		} else {
			count = true;
		}
	}

	public void setCount() {
		count = false;
	}
}