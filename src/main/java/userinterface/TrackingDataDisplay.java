package userinterface;

import javafx.scene.chart.XYChart;

/**
 * This class stores the Series of one tool. These are needed
 * to display tracking data in XYCharts.
 */
@SuppressWarnings("rawtypes")
public class TrackingDataDisplay {

    private final String toolName;
    private final XYChart.Series<Double, Double> dataSeries1;
    private final XYChart.Series<Double, Double> dataSeries2;
    private final XYChart.Series<Double, Double> dataSeries3;

    public TrackingDataDisplay(String toolName) {
        this.toolName = toolName;
        dataSeries1 = new XYChart.Series<>();
        dataSeries2 = new XYChart.Series<>();
        dataSeries3 = new XYChart.Series<>();
        // set names, so labels display tool name
        dataSeries1.setName(toolName);
        dataSeries2.setName(toolName);
        dataSeries3.setName(toolName);
        // Series need to have a data set so name and symbol are set correctly
        dataSeries1.getData().add(new XYChart.Data<>(0.0, 0.0));
        dataSeries2.getData().add(new XYChart.Data<>(0.0, 0.0));
        dataSeries3.getData().add(new XYChart.Data<>(0.0, 0.0));
    }

    public String getToolName() {
        return this.toolName;
    }

    public XYChart.Series getDataSeries1() {
        return dataSeries1;
    }

    public XYChart.Series getDataSeries2() {
        return dataSeries2;
    }

    public XYChart.Series getDataSeries3() {
        return dataSeries3;
    }

    public void addDataToSeries1(XYChart.Data<Double, Double> dataset) {
        dataSeries1.getData().add(dataset);
    }

    public void addDataToSeries2(XYChart.Data<Double, Double> dataset) {
        dataSeries2.getData().add(dataset);
    }

    public void addDataToSeries3(XYChart.Data<Double, Double> dataset) {
        dataSeries3.getData().add(dataset);
    }

    public void clearData() {
        dataSeries1.getData().clear();
        dataSeries2.getData().clear();
        dataSeries3.getData().clear();
    }
}
