package inputOutput;

import java.util.ArrayList;

public abstract class AbstractTrackingDataSource {

    protected ArrayList<Tool> toolList = new ArrayList<>();

    public abstract ArrayList<Tool> update();

    public ArrayList<Tool> getLastToolList() {
        return toolList;
    }

}
