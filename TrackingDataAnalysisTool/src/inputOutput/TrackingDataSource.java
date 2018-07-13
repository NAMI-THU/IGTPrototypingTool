package inputOutput;

import java.util.ArrayList;

public abstract class TrackingDataSource {
	
	protected ArrayList<Tool> toollist = new ArrayList<Tool>();
	
	abstract ArrayList<Tool> update();

}
