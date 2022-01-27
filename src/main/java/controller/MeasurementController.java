package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.math.Quaternion;

import algorithm.AverageMeasurement;
import algorithm.DataService;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.Tool;
import inputOutput.AbstractTrackingDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.FormatManager;
import javafx.stage.FileChooser.ExtensionFilter;

public class MeasurementController implements Controller {
    private int measurementCounter = 0;
    private Timer timer;
    private boolean timerOn = false;
    private AbstractTrackingDataSource source; // continuous tracking
    private AbstractTrackingDataSource sourceFileReader;
    private final DataService dataS = new DataService();
    private Map<String, ToolMeasure> storedMeasurements;
    private TrackingDataController trackingDataController;
    private Label statusLabel;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @FXML ListView<String> toolList, measurementList;
    @FXML ChoiceBox<String> measurementTyp;
    @FXML Label lCalcJR, lCalcCR, lCalcJP, lCalcCP;
    @FXML TextField address, numberToLoad, expDistance, rotationX,
      rotationY, rotationZ, rotationR;
    @FXML CheckBox jitterR, jitterP, correctnessR, correctnessP;
    @FXML FlowPane quaternionPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storedMeasurements = new LinkedHashMap<>();
    }

    public void setTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
        if(trackingDataController.source != null) {
            this.setTrackingDataSource(trackingDataController.source);
        }
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    public void setTrackingDataSource(AbstractTrackingDataSource source) {
        this.source = source;
        this.dataS.setTrackingDataSource(source);
    }

    private void updateTrackingDataSource(){
        var newSource = this.trackingDataController.getSource();
        if(!newSource.equals(this.source)){
            logger.info("Updating TrackingDataSource. Had before: "+this.source+", updating to: "+newSource);
            this.source =  newSource;
            this.dataS.setTrackingDataSource(newSource);
        }
    }

    private void updateMeasurementList() {
        measurementList.getItems().clear();
        for (String n : storedMeasurements.keySet()) {
            measurementList.getItems().add(n);
        }
    }

    @FXML
    private void loadFromFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select CSV File");
        fc.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
        File f = fc.showOpenDialog(new Stage());
        if (f != null) {
            try {
                address.setText(f.getAbsolutePath());
                sourceFileReader = new CSVFileReader(f.getAbsolutePath());
                toolList.getItems().clear();
                for (Tool t : sourceFileReader.update()) {
                    toolList.getItems().add(t.getName());
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error reading CSV file");
                statusLabel.setText("Error reading CSV file");
            }
        }
    }

    @FXML
    private void startMeasurement() {
        if(source == null) {
            updateTrackingDataSource();
        }
        // Check, if it is still null
        if (source == null) {
            statusLabel.setText("No source connected. No Measurement possible.");
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("There is no tracking source connected. No measurements can be calculated.");
            a.show();
            return;
        }

        if(trackingDataController.timeline == null){
            statusLabel.setText("Tracking has not started yet. No Measurement possible.");
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText("You need to start tracking in the TrackingView at first. Otherwise no measurements can be calculated.");
            a.show();
            return;
        }

        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Attention!");
        a.setHeaderText(null);
        a.setContentText("Please hold tracking tool in fixed position.");
        a.showAndWait();

        if (source != null && trackingDataController.timeline != null
                && !timerOn) {
            this.statusLabel.setText("Capturing...");
            dataS.restartMeasurements();
            timerOn = true;
            timer = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    dataS.getDataManager().getNextData(1);
                }
            };
            timer.schedule(tt, 0, 50);
        }
    }

    @FXML
    private void endMeasurement() {
        if (source == null) {
            statusLabel.setText("No source. Tracking has not started");
            return;
        }

        if (timer != null && timerOn) {
            timer.cancel();
            timerOn = false;
            storedMeasurements.put("Measurement " + measurementCounter + "("
                    + dataS.getDataManager().getToolMeasures().get(0).getName()
                    + ")", dataS.getDataManager().getToolMeasures().get(0));
            this.updateMeasurementList();
            measurementCounter++;
            this.statusLabel.setText("");
        }
    }

    @FXML
    private void switchType() {
        switch(measurementTyp.getValue()) {
            case "Correctness":
                quaternionPane.getChildren().forEach(
                    (item) -> item.setDisable(false));
                break;
            case "Jitter":
                quaternionPane.getChildren().forEach(
                    (item) -> item.setDisable(true));
                break;
        }
    }

    @FXML
    private void calculate() {
        if(measurementList.getItems().size() > 0
                && !measurementList.getSelectionModel().isEmpty()) {

            ToolMeasure tool = (ToolMeasure) storedMeasurements.values()
                     .toArray()[measurementList.getSelectionModel().getSelectedIndex()];
            AverageMeasurement avgMes = tool.getAverageMeasurement();

            try {
                // Jitter Position
                if (jitterP.isSelected()) {
                    lCalcJP.setText(FormatManager.toString(avgMes.getJitter()) + " mm");
                }
                // Jitter Rotation
                if (jitterR.isSelected()) {
                    // This was before set to rotationError, which was never calculated.
                    var rotationJitter = avgMes.getRotationJitter();
                    if(rotationJitter != null) {
                        lCalcJR.setText(rotationJitter + " mm");
                    } else {
                        logger.log(Level.WARNING, "Rotation Error cannot be calculated.");
                    }
                }
                // Correctness calculation needs two measurements
                // Correctness will take an expected distance and calculate how exact the measurements were (eg, how close they were to the expected distance)
                if(storedMeasurements.values().size() > 1) {
                    // Correctness Rotation
                    if (correctnessR.isSelected()) {
                        Quaternion expectedRotation = new Quaternion();
                        try {
                             expectedRotation = new Quaternion().set(
                                     Float.parseFloat(rotationX.getText()),
                                     Float.parseFloat(rotationY.getText()),
                                     Float.parseFloat(rotationZ.getText()),
                                     Float.parseFloat(rotationR.getText()));
                        }catch(NumberFormatException ex){
                            Alert a = new Alert(AlertType.ERROR);
                            a.setTitle("Error");
                            a.setHeaderText(null);
                            a.setContentText("The values you entered for the expected rotation are not valid.");
                            a.showAndWait();
                        }
                        ToolMeasure firstTool = (ToolMeasure) storedMeasurements.values().toArray()[0];
                        ToolMeasure secondTool = (ToolMeasure) storedMeasurements.values().toArray()[1];

                        lCalcCR.setText(String.valueOf(dataS.getAccuracyRotation(
                            expectedRotation,
                            firstTool.getMeasurement().get(0),
                            secondTool.getMeasurement().get(0))));
                    }
                    // Correctness Position
                    if (correctnessP.isSelected()) {
                        lCalcCP.setText("0,00");
                        ToolMeasure firstTool = (ToolMeasure) storedMeasurements.values().toArray()[0];
                        ToolMeasure secondTool = (ToolMeasure) storedMeasurements.values().toArray()[1];
                        lCalcCP.setText(String.valueOf(dataS.getAccuracy(
                            Double.parseDouble(expDistance.getText()),
                            firstTool.getAverageMeasurement(),
                            secondTool.getAverageMeasurement())));
                    }
                }else{
                    if(correctnessR.isSelected() || correctnessP.isSelected()) {
                        Alert a = new Alert(AlertType.WARNING);
                        a.setTitle("Warning");
                        a.setHeaderText(null);
                        a.setContentText("You need two measurements to calculate the accuracy.");
                        a.show();
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.log(Level.WARNING, "Calculation error", e);
                statusLabel.setText("Calculation error");
            }
        }else{
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("No measurement selected");
            a.setHeaderText(null);
            a.setContentText("You need to select a measurement to calculate these values.");
            a.show();
        }
    }

    @FXML
    private void addMeasurement(){
        if (toolList.getItems().size() > 0
                && toolList.getSelectionModel().getSelectedItem() != null) {
            try {
                DataService loadDataService = new DataService();
                loadDataService.setTrackingDataSource(sourceFileReader);
                loadDataService.loadNextData(Integer.parseInt(numberToLoad.getText()), true);
                ToolMeasure newMeasurement;

                newMeasurement = loadDataService.getToolByName(toolList
                        .getSelectionModel()
                        .getSelectedItem());
                storedMeasurements.put("Measurement " + measurementCounter + "("
                        + newMeasurement.getName()
                        + ", from file)", newMeasurement);
                this.updateMeasurementList();
                measurementCounter++;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error when adding a measurement", e);
            }
        }
    }

    public void close() {
        this.statusLabel.setText("");
    }
}
