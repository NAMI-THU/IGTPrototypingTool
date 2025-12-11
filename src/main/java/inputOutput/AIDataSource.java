package inputOutput;

import java.util.ArrayList;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class AIDataSource extends AbstractTrackingDataSource {
    private double timeCounter = 0.0;
    
    private Expression expressionX;
    private Expression expressionY;
    private Expression expressionZ;

    public AIDataSource() {
        tempToolList = new ArrayList<>();
        // Initialize with default functions 
        setFormulaX("10 + 5 * sin(t)");
        setFormulaY("10 + 5 * cos(t)");
        setFormulaZ("10 + 2 * sin(t * 0.5)");
    }
    
    public void setFormulaX(String formula) {
        this.expressionX = new ExpressionBuilder(formula).variables("t").build();
    }

    public void setFormulaY(String formula) {
        this.expressionY = new ExpressionBuilder(formula).variables("t").build();
    }

    public void setFormulaZ(String formula) {
        this.expressionZ = new ExpressionBuilder(formula).variables("t").build();
    }
    
    @Override
    public ArrayList<TempTool> update() {
        tempToolList.clear();
        
        timeCounter += 0.1;
        
        double x = 0;
        double y = 0;
        double z = 0;
        
        try {
            x = expressionX.setVariable("t", timeCounter).evaluate();
            y = expressionY.setVariable("t", timeCounter).evaluate();
            z = expressionZ.setVariable("t", timeCounter).evaluate();
        } catch (Exception e) {
            // Fallback or log if evaluation fails
            System.err.println("Error evaluating function: " + e.getMessage());
        }
        
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
