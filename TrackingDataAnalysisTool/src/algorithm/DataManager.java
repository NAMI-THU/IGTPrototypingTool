package algorithm;

import java.util.ArrayList;
import java.util.List;

import inputOutput.Tool;

public class DataManager {
	List<ToolMeasure> tools;
	int countToGetNext;
	
	//Constructor
	public DataManager (int countToGetNext) {
		this.countToGetNext = countToGetNext;
	}
	
public void getNextData (int countToGetNext) {
		
		for(double i = 0; i< countToGetNext; i++) {
			
		//aus rückgabe von update neue messung erstellen
		Measurement measurement = new Measurement(Team3Service.update());
		
		 for(ToolMeasure tool : tools) {
		        if(tool.getName().equals(measurement.getToolName())) {
		            tool.addMeasurement(measurement);
		        }else {
		        	ToolMeasure newTool = new ToolMeasure(measurement.getToolName());
		        	newTool.addMeasurement(measurement);
		        	tools.add(newTool);
		        }
		    }
		}
	}   
	
	   
	
}

		

		
	}   
	
}
	


	


