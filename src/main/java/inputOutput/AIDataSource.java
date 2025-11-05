package inputOutput;

import java.util.ArrayList;

public class AIDataSource extends AbstractTrackingDataSource {
    private double timeCounter = 0.0;
    private double baseX = 0.0;
    private double baseY = 0.0;
    private double baseZ = 0.0;

    public AIDataSource() {
        tempToolList = new ArrayList<>();
        // Initialize with some starting position
        baseX = 10.0;
        baseY = 10.0;
        baseZ = 10.0;
    }
    
    @Override
    public ArrayList<TempTool> update() {
        tempToolList.clear();
        
        timeCounter += 0.1;
        double x = baseX + 5.0 * Math.sin(timeCounter);
        double y = baseY + 5.0 * Math.cos(timeCounter);
        double z = baseZ + 2.0 * Math.sin(timeCounter * 0.5);
        
        TempTool testTool1 = new TempTool();
        testTool1.setData(
            timeCounter,           
            1.0,                   
            x, y, z,              
            0.0, 0.0, 0.0, 1.0,  
            "testTool"            
        );
        tempToolList.add(testTool1);
        return tempToolList;
    }

    @Override
    public void closeConnection() {
        tempToolList.clear();
    }
}
