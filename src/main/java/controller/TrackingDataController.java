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
import javafx.stage.Stage;
import javafx.util.Duration;
import algorithm.VisualizationManager;
import tracking.Measurement;
import tracking.Tool;
import tracking.TrackingService;
import tracking.observers.TrackingMeasurementObserver;
import tracking.observers.TrackingSourceObserver;
import tracking.tracker.CSVFileReader;
import tracking.tracker.OIGTTrackingDataSource;
import userinterface.TrackingDataDisplay;
import util.HardwareStatus;

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
//    private final BooleanProperty visualizationRunning = new SimpleBooleanProperty(false);
//    private final BooleanProperty sourceConnected = new SimpleBooleanProperty(false);

    private TrackingMeasurementObserver measurementObserver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        toolDisplayList = new ArrayList<>();
        position = new HashMap<>();
        rotation = new HashMap<>();

        trackingService.subscribe((TrackingSourceObserver) this::on_hardwareStatusChanged);
//        loadCSVBtn.disableProperty().bind(visualizationRunning);
//        visualizeTrackingBtn.disableProperty().bind(visualizationRunning.or(sourceConnected.not()));
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

    private void on_hardwareStatusChanged(HardwareStatus status) {
        if(status == HardwareStatus.DISCONNECTED) {
            loadCSVBtn.setDisable(false);
            visualizeTrackingBtn.setDisable(true);
        }else if(status == HardwareStatus.CONNECTED_NO_STREAM){
            loadCSVBtn.setDisable(false);
            visualizeTrackingBtn.setDisable(false);
        }else if(status == HardwareStatus.CONNECTED_AND_STREAMING){
            loadCSVBtn.setDisable(true);
            visualizeTrackingBtn.setDisable(false);
        }
    }

    /**
     * load CSV file with tracking data created by MITK workbench
     */
    @FXML
    public void loadCSVFile() {
        CSVFileReader newSource;

        FileChooser fp = new FileChooser();
        fp.setTitle("Load Data");
        fp.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Datei", "*.csv"));
        File file = fp.showOpenDialog(new Stage());


        if (file != null) {
            if (trackingService.isConnected()) {
                disconnectSource();
            }
            try {
                newSource = new CSVFileReader(file.getAbsolutePath(), true);
                trackingService.connect(newSource);
//                sourceConnected.setValue(true);
                visualizationController.setSourceConnected(true);
                logger.log(Level.INFO, "CSV file read from: " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error loading CSV file", e);
                statusLabel.setText("Error loading CSV file");
            }
        }
    }

//    @FXML
//    public void loadAIData() {
//        System.out.println("AI DATA LOADING");
//
//        AIDataSource newSource = new AIDataSource();
//
//        if (trackingService.isConnected()) {
//            disconnectSource();
//        }
//
//        try {
//            trackingService.connect(newSource);
//            sourceConnected.setValue(true);
//            visualizationController.setSourceConnected(true);
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Error loading AI DATA", e);
//            statusLabel.setText("Error loading AI DATA SOURCE");
//        }
//    }

    /**
     * Connect via OpenIGTLink.
     */
    @FXML
    public void onConnectButtonClicked() {
        if (trackingService.isConnected()) { // bit hacky
            disconnectSource();
        }
        connectionIndicator.setVisible(true);
        OIGTTrackingDataSource newSource = new OIGTTrackingDataSource();
        new Thread(() -> {
            newSource.connect();
            Platform.runLater(() -> {
                try {
                    trackingService.connect(newSource);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                connectionIndicator.setVisible(false);
//                sourceConnected.setValue(true);
//                visualizationController.setSourceConnected(true);
            });
        }).start();
    }

    /**
     * This method disconnects the current source, closes the connection and resets the timeline.
     */
    private void disconnectSource() {
//        var timeline = trackingService.getTimeline();
//        if (timeline != null) {
//            if (timeline.getStatus() == Animation.Status.PAUSED) {
//                statusLabel.setText("");    // To remove the label
//                freezeTglBtn.setSelected(false);
//            }
//            timeline.stop();
//            trackingService.changeTimeline(null);
//        }
//        visualizationRunning.setValue(false);
//        visualizationController.setVisualizationRunning(false);
//
//        var source = trackingService.getTrackingDataSource();
//        if (source != null) {
//            source.closeConnection();
//            trackingService.changeTrackingSource(null);
//        }
//
//        if (trackingService.getDataService() != null) {
//            trackingService.changeDataService(null);
//        }
//
//        sourceConnected.setValue(false);
//        visualizationController.setSourceConnected(false);
        trackingService.disconnect();
    }

    /**
     * Start the visualization of tracking data that is read from CSV or IGT connection
     */
    @FXML
    public void visualizeTracking() {
        if(measurementObserver == null) {
            measurementObserver = trackingService.subscribe(m -> Platform.runLater(this::updateDiagrams));
        }
        trackingService.scheduleUpdates(100);
//        var timeline = trackingService.getTimeline();
//        var source = trackingService.getTrackingDataSource();
//
//        if (timeline == null && source != null) {
//            // this is used to load tracking data from source
//            trackingService.changeDataService(new DataService(source));
//
//            timeline = new Timeline();
//            timeline.setCycleCount(Animation.INDEFINITE);
//            timeline.getKeyFrames().addAll(
//                    new KeyFrame(Duration.millis(100),
//                            event2 -> updateDiagrams()));
//
//            timeline.play();
//            TrackingService.getInstance().changeTimeline(timeline);
//
//            updateDiagrams();
//            visualizationRunning.setValue(true);
//            visualizationController.setVisualizationRunning(true);
//            visualizationController.addTrackerToTreeView(trackingService.getDataService().loadNextData(1));
//            visualizationManager.loadLastSTLModels();
//            visualizationController.addSTLToTreeView();
//            visualizationManager.showFigure();
//        }
//        if (timeline != null) {
//            timeline.play();
//        }
    }

    public void updateDiagrams() {
        var tools = trackingService.getTools();
        for (Tool tool : tools) {

            TrackingDataDisplay display = checkToolDisplayList(tool.getName());
            display.clearData();

            List<Measurement> li = tool.getMeasurementHistory();
            //use the last 5 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 5; i++) {
                if (li.size() - i < 0) {
                    break;
                }
                double x = li.get(li.size() - i).getPosition().getX();
                double y = li.get(li.size() - i).getPosition().getY();
                double z = li.get(li.size() - i).getPosition().getZ();

                // display position and rotation of tool
                if (i == 1) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double qX = li.get(li.size() - i).getOrientation().getX();
                    double qY = li.get(li.size() - i).getOrientation().getY();
                    double qZ = li.get(li.size() - i).getOrientation().getZ();
                    double qR = li.get(li.size() - i).getOrientation().getW();

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
     * and position and rotation labels for one tool.
     */
    private TrackingDataDisplay checkToolDisplayList(String toolName) {
        if (!this.toolDisplayList.isEmpty()) {
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
        trackingService.cancelUpdates();
//        var timeline = trackingService.getTimeline();
//        if (timeline != null) {
//            switch (timeline.getStatus()) {
//                case RUNNING:
//                    timeline.pause();
//                    statusLabel.setText("Visualization paused");
//                    break;
//                case PAUSED:
//                    timeline.play();
//                    statusLabel.setText("");
//                    break;
//                default:
//                    break;
//            }
//        }
    }

    @Override
    public void close() {
        disconnectSource();
        unregisterController();
    }
}



