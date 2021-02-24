package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import algorithm.DataService;
import algorithm.Measurement;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.OpenIGTLinkConnection;
import inputOutput.TrackingDataSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import userinterface.TrackingDataDisplay;
import util.CustomLogger;
import javafx.stage.Stage;

public class TrackingDataController implements Controller {

    @FXML ScatterChart<Number, Number> s1;
    @FXML ScatterChart<Number, Number> s2;
    @FXML ScatterChart<Number, Number> s3;
    @FXML VBox posBox;
    @FXML VBox rotBox;
    public DataService ds;
    public Timeline timeline;
    TrackingDataSource source;
    List<TrackingDataDisplay> toolDisplayList;
    HashMap<String, Label> position;
    HashMap<String, Label> rotation;
    Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toolDisplayList = new ArrayList<TrackingDataDisplay>();
        position = new HashMap<String, Label>();
        rotation = new HashMap<String, Label>();
    }

    public TrackingDataSource getSource() {
        return source;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * load CSV file with tracking data created by MITK workbench
     */
    @FXML
    private void loadCSVFile() {
        CSVFileReader newSource = null;
        FileChooser fp = new FileChooser();
        fp.setTitle("Load Data");
        fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

        File file = fp.showOpenDialog(new Stage());
        if (file != null) {
            try {
                newSource = new CSVFileReader(file.getAbsolutePath());
                CustomLogger.log(Level.INFO, "CSV file read from: " + file.getAbsolutePath());
            } catch (IOException e) {
                CustomLogger.log(Level.SEVERE, "Error loading CSV file", e);
                statusLabel.setText("Error loading CSV file");
            }
            newSource.setRepeatMode(true);
            source = newSource;
        }
    }

    /**
     * Connect via OpenIGTLink.
     */
    @FXML
    private void connect() {
        OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
        source = newSource;
    }

    /**
     * Start the visualization of tracking data that is read from CSV or IGT connection
     */
    @FXML
    public void visualizeTracking() {

        if (timeline == null && source != null) {
            // this is used to load tracking data from source
            ds = new DataService(source);

            timeline = new Timeline();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            event2 -> updateDiagrams())
            );
            timeline.play();
            updateDiagrams();
        }
        if (timeline != null) {
            timeline.play();
        }
    }

    public void updateDiagrams() {
        // loads the next set of tracking data
        source.update();
        // this returns tracking data from all tools at one point in time
        List<ToolMeasure> tools = ds.loadNextData(1);
        
        if (tools.isEmpty()) return;

        for (ToolMeasure tool : tools) {

            TrackingDataDisplay display = checkToolDisplayList(tool.getName());
            display.clearData();

            List<Measurement> li = tool.getMeasurement();
            //use the last 5 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 5; i++) {
                if (li.size() - i < 0) {
                    break;
                }
                double x = li.get(li.size() - i).getPoint().getX();
                double y = li.get(li.size() - i).getPoint().getY();
                double z = li.get(li.size() - i).getPoint().getZ();

                // display position and rotation of tool
                if (i == 1) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double qX = li.get(li.size() - i).getRotation().getX();
                    double qY = li.get(li.size() - i).getRotation().getY();
                    double qZ = li.get(li.size() - i).getRotation().getZ();
                    double qR = li.get(li.size() - i).getRotation().getW();

                    position.get(tool.getName()).setText(tool.getName() + ": ["
                            + df.format(x) + ";"
                            + df.format(y) + ";"
                            + df.format(z) + "]");
                    rotation.get(tool.getName()).setText(tool.getName()+": ["
                            + df.format(qY) + ";"
                            + df.format(qZ) + ";"
                            + df.format(qR) + "]");
                }

                display.addDataToSeries1(new XYChart.Data<Double, Double>(x, y));
                display.addDataToSeries2(new XYChart.Data<Double, Double>(x, z));
                display.addDataToSeries3(new XYChart.Data<Double, Double>(z, y));
            }
        }
    }

    /**
     * This method creates a new series to store tracking data
     * and position and rotation labels for one tool.
     */
    @SuppressWarnings({ "unchecked" })
    private TrackingDataDisplay checkToolDisplayList(String toolName) {
        if (toolDisplayList.size() > 0) {
            for (TrackingDataDisplay d : toolDisplayList) {
                if (d.getToolName().equals(toolName)) return d;
            }
        }
        // create display data for tool
        TrackingDataDisplay newDisplay = new TrackingDataDisplay(toolName);
        s1.getData().addAll(newDisplay.getDataSeries1());
        s2.getData().addAll(newDisplay.getDataSeries2());
        s3.getData().addAll(newDisplay.getDataSeries3());
        toolDisplayList.add(newDisplay);

        position.put(toolName, new Label(toolName + ": [-]"));
        posBox.getChildren().add(position.get(toolName));
        rotation.put(toolName, new Label(toolName + ": [-]"));
        rotBox.getChildren().add(rotation.get(toolName));
        return newDisplay;
    }

    @FXML
    public void freezeVisualization() {
        if (timeline != null) {
            switch (timeline.getStatus()) {
            case RUNNING:
                timeline.pause();
                statusLabel.setText("Visualization paused");
                break;
            case PAUSED:
                timeline.play();
                statusLabel.setText("");
                break;
            default: break;
            }
        }
    }

    public void close() {}
}
