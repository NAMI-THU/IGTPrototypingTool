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
import java.util.logging.Logger;

import algorithm.*;
import inputOutput.CSVFileReader;
import inputOutput.OIGTTrackingDataSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import algorithm.VisualizationManager;
import userinterface.TrackingDataDisplay;
import javafx.stage.Stage;

public class TrackingDataController implements Controller {

    @FXML
    ScatterChart<Number, Number> s1;
    @FXML
    ScatterChart<Number, Number> s2;
    @FXML
    ScatterChart<Number, Number> s3;
    @FXML
    VBox posBox;
    @FXML
    VBox rotBox;
    @FXML
    ProgressIndicator connectionIndicator;
    @FXML
    ToggleButton freezeTglBtn;
    @FXML
    Button loadCSVBtn;
    @FXML
    Button visualizeTrackingBtn;
    @FXML
    Group meshGroup;
    @FXML
    ScrollPane scrollPane;

    private final TrackingService trackingService = TrackingService.getInstance();
    List<TrackingDataDisplay> toolDisplayList;
    HashMap<String, Label> position;
    HashMap<String, Label> rotation;
    Label statusLabel;
    VisualizationManager visualizationManager;
    VisualizationController visualizationController;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        toolDisplayList = new ArrayList<>();
        position = new HashMap<>();
        rotation = new HashMap<>();

        loadCSVBtn.disableProperty().bind(visualizationRunning);
        visualizeTrackingBtn.disableProperty().bind(visualizationRunning.or(sourceConnected.not()));
    }

    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    public void injectVisualizationManager(VisualizationManager visualizationManager) {
        this.visualizationManager = visualizationManager;
    }

    public void injectVisualizationController(VisualizationController visualizationController) {
        this.visualizationController = visualizationController;
    }

    /**
     * load CSV file with tracking data created by MITK workbench
     */
    @FXML
    public void loadCSVFile() {
        CSVFileReader newSource = null;
        FileChooser fp = new FileChooser();
        fp.setTitle("Load Data");
        fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

        File file = fp.showOpenDialog(new Stage());
        if (file != null) {
            if (trackingService.getTrackingDataSource() != null) {
                disconnectSource();
            }
            try {
                newSource = new CSVFileReader(file.getAbsolutePath());
                newSource.setRepeatMode(true);
                trackingService.changeTrackingSource(newSource);
                sourceConnected.setValue(true);
                visualizationController.setSourceConnected(true);
                logger.log(Level.INFO, "CSV file read from: " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error loading CSV file", e);
                statusLabel.setText("Error loading CSV file");
            }
        }
    }

    /**
     * Connect via OpenIGTLink.
     */
    @FXML
    public void onConnectButtonClicked() {
        if (trackingService.getTrackingDataSource() != null) { // bit hacky
            disconnectSource();
        }
        connectionIndicator.setVisible(true);
        OIGTTrackingDataSource newSource = new OIGTTrackingDataSource();
        new Thread(() -> {
            newSource.connect();
            Platform.runLater(() -> {
                trackingService.changeTrackingSource(newSource);
                connectionIndicator.setVisible(false);
                sourceConnected.setValue(true);
                visualizationController.setSourceConnected(true);
            });
        }).start();
    }

    /**
     * This method disconnects the current source, closes the connection and resets the timeline.
     */
    private void disconnectSource() {
        var timeline = trackingService.getTimeline();
        if (timeline != null) {
            if (timeline.getStatus() == Animation.Status.PAUSED) {
                statusLabel.setText("");    // To remove the label
                freezeTglBtn.setSelected(false);
            }
            timeline.stop();
            trackingService.changeTimeline(null);
        }
        visualizationRunning.setValue(false);
        visualizationController.setVisualizationRunning(false);

        var source = trackingService.getTrackingDataSource();
        if (source != null) {
            source.closeConnection();
            trackingService.changeTrackingSource(null);
        }

        if (trackingService.getDataService() != null) {
            trackingService.changeDataService(null);
        }

        sourceConnected.setValue(false);
        visualizationController.setSourceConnected(false);
    }

    /**
     * Start the visualization of tracking data that is read from CSV or IGT connection
     */
    @FXML
    public void visualizeTracking() {
        var timeline = trackingService.getTimeline();
        var source = trackingService.getTrackingDataSource();

        if (timeline == null && source != null) {
            // this is used to load tracking data from source
            trackingService.changeDataService(new DataService(source));

            timeline = new Timeline();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(100),
                            event2 -> updateDiagrams()));

            timeline.play();
            TrackingService.getInstance().changeTimeline(timeline);

            updateDiagrams();
            visualizationRunning.setValue(true);
            visualizationController.setVisualizationRunning(true);
            visualizationManager.showFigure();
        }
        if (timeline != null) {
            timeline.play();
        }
    }

    public void updateDiagrams() {
        if (trackingService.getTrackingDataSource() == null) {
            return;
        }
        // loads the next set of tracking data
        trackingService.getTrackingDataSource().update();
        // this returns tracking data from all tools at one point in time
        List<ToolMeasure> tools = trackingService.getDataService().loadNextData(1);

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
                    rotation.get(tool.getName()).setText(tool.getName() + ": ["
                            + df.format(qX) + ";"
                            + df.format(qY) + ";"
                            + df.format(qZ) + ";"
                            + df.format(qR) + "]");
                }

                display.addDataToSeries1(new XYChart.Data<>(x, y));
                display.addDataToSeries2(new XYChart.Data<>(x, z));
                display.addDataToSeries3(new XYChart.Data<>(z, y));
            }
        }
        visualizationManager.visualizeTracking();
    }

    /**
     * This method creates a new series to store tracking data
     * * and position and rotation labels for one tool.
     */
    private TrackingDataDisplay checkToolDisplayList(String toolName) {
        if (this.toolDisplayList.size() > 0) {
            for (TrackingDataDisplay d : this.toolDisplayList) {
                if (d.getToolName().equals(toolName)) return d;
            }
        }
        // create display data for tool
        TrackingDataDisplay newDisplay = new TrackingDataDisplay(toolName);
        s1.getData().addAll(newDisplay.getDataSeries1());
        s2.getData().addAll(newDisplay.getDataSeries2());
        s3.getData().addAll(newDisplay.getDataSeries3());
        this.toolDisplayList.add(newDisplay);

        position.put(toolName, new Label(toolName + ": [-]"));
        posBox.getChildren().add(position.get(toolName));
        rotation.put(toolName, new Label(toolName + ": [-]"));
        rotBox.getChildren().add(rotation.get(toolName));
        return newDisplay;
    }

    @FXML
    public void freezeVisualization() {
        var timeline = trackingService.getTimeline();
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
                default:
                    break;
            }
        }
    }

    @Override
    public void close() {
        disconnectSource();
        unregisterController();
    }
}
