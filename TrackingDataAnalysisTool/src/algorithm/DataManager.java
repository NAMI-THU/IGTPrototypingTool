package algorithm;

import java.util.ArrayList;
import java.util.List;

import inputOutput.CSVFileReader;

import inputOutput.Tool;

public class DataManager  {
	List<ToolMeasure> toolMeasures = new ArrayList<>();
	private boolean count = false;

	public List<ToolMeasure> getToolMeasures() {
		return toolMeasures;
	}
 /** getNextData calls method update, creates a measurement and adds this tool*/
	public List<ToolMeasure> getNextData(int countToGetNext) {

		for (double i = 0; i < countToGetNext; i++) {
			/** from return value of update a new measurement will be created */
			List<Tool> tools = inputOutput.CSVFileReader.update();

			for (Tool tool : tools) {
				Measurement measurement = new Measurement(tool);
				addMeasurementToTool(measurement);
			}

		}

		return toolMeasures;
	}
	/** This methods manages the tools */
	private void addMeasurementToTool(Measurement measurement) {
		
		/** Check if tool exists*/
		for (ToolMeasure toolMeasure : toolMeasures) {
			if (toolMeasure.getName().equals(measurement.getToolname())) {
				
				/** added new measurements to the tool */
				toolMeasure.addMeasurement(measurement);
				return;
			}
		}
		
		/** creation of a new tool */
		ToolMeasure newTool = new ToolMeasure(measurement.getToolname());
		newTool.addMeasurement(measurement);
		toolMeasures.add(newTool);
	}

	/** Method call for team 3 */
	public void setList(ArrayList<Tool> test) {

		if (count == true) {

			List<Tool> tools = test;
			for (Tool tool : tools) {
				Measurement measurement = new Measurement(tool);
				addMeasurementToTool(measurement);
			}

		} else {
			count=true;
		}
	}
	
	public void setCount(){
		count=false;
	}
}