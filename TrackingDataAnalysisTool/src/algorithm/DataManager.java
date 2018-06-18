package algorithm;

import java.util.ArrayList;
import java.util.List;

import inputOutput.CSVFileReader;

import inputOutput.Tool;

public class DataManager {
	List<ToolMeasure> toolMeasures = new ArrayList<>();
	private boolean count = false;

	public List<ToolMeasure> getToolMeasures() {
		return toolMeasures;
	}

	public List<ToolMeasure> getNextData(int countToGetNext) {

		for (double i = 0; i < countToGetNext; i++) {
			// aus rückgabe von update neue messung erstellen
			List<Tool> tools = inputOutput.CSVFileReader.update();

			for (Tool tool : tools) {
				Measurement measurement = new Measurement(tool);
				addMeasurementToTool(measurement);
			}

		}

		return toolMeasures;
	}

	private void addMeasurementToTool(Measurement measurement) {
		for (ToolMeasure toolMeasure : toolMeasures) {
			if (toolMeasure.getName().equals(measurement.getToolname())) {
				toolMeasure.addMeasurement(measurement);
			} else {
				ToolMeasure newTool = new ToolMeasure(measurement.getToolname());
				newTool.addMeasurement(measurement);
				toolMeasures.add(newTool);
			}
		}
	}

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
}