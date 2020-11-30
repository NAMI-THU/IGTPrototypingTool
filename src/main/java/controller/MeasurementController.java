package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.jme3.math.Quaternion;

import algorithm.AverageMeasurement;
import algorithm.DataService;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.Tool;
import inputOutput.TrackingDataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class MeasurementController implements Controller {

	Integer measurementCounter = 0;
	Timer timer;
	boolean timerOn = false;
	TrackingDataSource source; // continuous tracking
	TrackingDataSource sourceFileReader;
	DataService dataS = new DataService();
	Map<String, ToolMeasure> storedMeasurements;
	TrackingDataController trackingDataController;

	@FXML ListView<String> toolList, measurementList;
	@FXML ChoiceBox<String> measurementTyp;
	@FXML Label lCalcJR, lCalcC, lCalcJP;
	@FXML TextField address, numberToLoad, expDistance, rotationX,
		rotationY, rotationZ, rotationR;
	@FXML CheckBox jitterR, jitterP, correctnessR, correctnessP;
	@FXML FlowPane quaternionPane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		storedMeasurements = new LinkedHashMap<String, ToolMeasure>();		
	}
	
	public void setTrackingDataController(TrackingDataController trackingDataController) {
		this.trackingDataController = trackingDataController;
	}

//	public MeasurementController(TrackingDataController trackingDataController) {
//		this.trackingDataController = trackingDataController;
//		if(source != null) {
//			dataS = trackingDataController.ds;
//		}
//	}
	public void setTrackingDataSource(TrackingDataSource source) {
		this.source = source;
		this.dataS.setTrackingDataSource(source);
	}

	private void updateMeasurementList() {
        measurementList.getItems().clear();
        for (String n : storedMeasurements.keySet()) {
            measurementList.getItems().add(n);
        }
    }

	@FXML
	private void loadFromFile() throws IOException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select CSV File");
		fc.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
		File f = fc.showOpenDialog(new Stage());
		if (f != null) {
			address.setText(f.getAbsolutePath());
            sourceFileReader = new CSVFileReader(f.getAbsolutePath());
            toolList.getItems().clear();
            for (Tool t : sourceFileReader.update()) {
                toolList.getItems().add(t.getName());
            }
        } else {
        	address.setText("No file selected");
        }
	}

	@FXML
	private void startMeasurement() {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle("Attention!");
		a.setHeaderText(null);

		if (source == null) {
			a.setContentText("Tracking not started yet (aborting)!");
			a.showAndWait();
			return;
        }

		a.setContentText("Please hold tracking tool in fixed position.");
		a.showAndWait();

        if (source != null && trackingDataController.timeline != null 
        		&& !timerOn) {
        	dataS.restartMeasurements();
        	timerOn = true;
        	timer = new Timer();
        	TimerTask tt = new TimerTask() {
        		@Override
        		public void run() {
        			System.out.println("timer runs");
        			dataS.getDataManager().getNextData(1);
        		}
        	};
        	timer.schedule(tt, 0, 50);
        }
	}

	@FXML
	private void endMeasurement() {
		if (source == null) { 
			Alert a = new Alert(AlertType.INFORMATION);
			a.setTitle("Attention!");
			a.setHeaderText(null);
			a.setContentText("Tracking not started yet (aborting)!");
			a.showAndWait();
        }

		if (timer != null && timerOn) {
			timer.cancel();
			timerOn = false;
			storedMeasurements.put("Measurement " + measurementCounter + "("
                    + dataS.getDataManager().getToolMeasures().get(0).getName()
                    + ")", dataS.getDataManager().getToolMeasures().get(0));
            this.updateMeasurementList();
            measurementCounter++;
		}
	}

	@FXML
	private void switchType() {
		switch(measurementTyp.getValue()) {
			case "Correctness":
				quaternionPane.getChildren().forEach(
						(item) -> {
							item.setDisable(false);
						});
				break;
			case "Jitter": 
				quaternionPane.getChildren().forEach(
						(item) -> {
							item.setDisable(true);
						});
				break;
		}
	}

	@FXML
	private void calculate() {
		if(measurementList.getItems().size() > 0) {
			System.out.println("Computing results");

            ToolMeasure tool = (ToolMeasure) storedMeasurements.values()
            	 .toArray()[measurementList.getSelectionModel().getSelectedIndex()];
            AverageMeasurement avgMes = tool.getAverageMeasurement();

            // Jitter Rotation
            if (jitterR.isSelected()) {
                lCalcJR.setText("0.00 mm");
                lCalcJR.setText(new DecimalFormat("#0.00").format(
                		avgMes.getRotationError()) + " mm");
            }
            // Jitter Position
            if (jitterP.isSelected()) {
                lCalcJP.setText("0.00 mm");
                lCalcJP.setText(new DecimalFormat("#0.00").format(
                		avgMes.getJitter()) + " mm");
            }
            // Correctness Rotation
            if (correctnessR.isSelected()) {
	
                Quaternion expectedrotation = new Quaternion().set(
                        (float) Double.parseDouble(rotationX.getText()),
                        (float) Double.parseDouble(rotationY.getText()),
                        (float) Double.parseDouble(rotationZ.getText()),
                        (float) Double.parseDouble(rotationR.getText()));

                ToolMeasure firstTool = (ToolMeasure) storedMeasurements.values().toArray()[0];
                ToolMeasure secondTool = (ToolMeasure) storedMeasurements.values().toArray()[1];

                lCalcJR.setText(String.valueOf(dataS.getAccuracyRotation(
					                        expectedrotation,
					                        firstTool.getMeasurement().get(0),
					                        secondTool.getMeasurement().get(0))));

            }
            // Correctness Position
            if (correctnessP.isSelected()) {
                lCalcC.setText("0,00");
                ToolMeasure firstTool = null;
                ToolMeasure secondTool = null;
                firstTool = (ToolMeasure) storedMeasurements.values().toArray()[0];
                secondTool = (ToolMeasure) storedMeasurements.values().toArray()[1];
                for (String m : storedMeasurements.keySet()) {
                    System.out.println("Tool:" + m);
                }

                System.out.println("Avgmes1:" + firstTool.getAverageMeasurement().getPoint());
                System.out.println("Avgmes2:" + secondTool.getAverageMeasurement().getPoint());

                lCalcJP.setText(String.valueOf(dataS.getAccuracy(
                		Double.parseDouble(expDistance.getText()),
                        firstTool.getAverageMeasurement(),
                        secondTool.getAverageMeasurement())));
            }
		}
	}

	@FXML
	private void addMeasurement() throws NumberFormatException, Exception {
		if (toolList.getItems().size() > 0) {
			if(toolList.getSelectionModel().getSelectedItem() == null) {
				System.out.println("no selection");
				return;
			}
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
		}
	}
	
	public void close() {

	}
}
