package inputOutput;

import java.util.ArrayList;

public abstract class AbstractTrackingDataSource {

    protected ArrayList<TempTool> tempToolList = new ArrayList<>();

    public abstract ArrayList<TempTool> update();

    public ArrayList<TempTool> getLastToolList() {
        return tempToolList;
    }

    public abstract void closeConnection();
}
