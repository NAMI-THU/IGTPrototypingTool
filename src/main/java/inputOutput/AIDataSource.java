package inputOutput;

import java.util.ArrayList;

public class AIDataSource extends AbstractTrackingDataSource {

    public AIDataSource() {
        tempToolList = new ArrayList<>();
    }
    @Override
    public ArrayList<TempTool> update() {
        TempTool testTool1 = new TempTool();
        testTool1.setData(1, 1, 1, 1, 1, 1, 1, 1, 1, "testTool");
        tempToolList.add(testTool1);
        return tempToolList;
    }

    @Override
    public void closeConnection() {

    }
}
