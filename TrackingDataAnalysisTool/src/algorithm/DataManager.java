import java.util.ArrayList;
import java.util.List;

import inputOutput.Interface;
import inputOutput.Tool;

public class DataManager {
	List<ToolMeasure> toolMeasures;
	int countToGetNext;
	
	//Constructor
	public DataManager (int countToGetNext) {
		this.countToGetNext = countToGetNext;
	}
	
	public void getNextData (int countToGetNext) {
		
		for(double i = 0; i< countToGetNext; i++) {
		//aus rückgabe von update neue messung erstellen
		List<Tool> tools = new ArrayList<>();
		
			for(Tool tool : tools) {
//				Measurement measurement = new Measurement(tool);
//				addMeasurementToTool(measurement);
			}
		
		}
	} 
	
	private void addMeasurementToTool(Measurement measurement) {
		for(ToolMeasure toolMeasure : toolMeasures) {
			if(toolMeasure.getName().equals(measurement.getToolname())) {
				toolMeasure.addMeasurement(measurement);
			}else {
				ToolMeasure newTool = new ToolMeasure(measurement.getToolname());
				newTool.addMeasurement(measurement);
				toolMeasures.add(newTool);
			}
		}
	}

}
	


	


