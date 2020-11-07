package userinterface;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import com.jme3.math.Quaternion;

import algorithm.AverageMeasurement;
import algorithm.DataService;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.Tool;
import inputOutput.TrackingDataSource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MeasurementView implements View {
	
	ListView<String> toolList;
	ListView<String> measurementList;
	Button start2, finish2, calculate, loadTool, searchButton;
	ChoiceBox<String> measurementtyp;
    CheckBox cBJitterP, cBJitterR, cBCorrectnessR, cBCorrectnessP;
    Label lCalcJR, lCalcC, lCalcJP, rotationL1,	rotationL2, rotationL3,	rotationL4;
    TextField address, toLoadField, distanceF, rotationAngle, rotationAngle1,
		rotationAngle2, rotationAngle3;
    
    int measurementCounter = 0;
    
    Timer timer;
    boolean timerOn = false;
    
    TrackingDataSource source; // continuous tracking
    TrackingDataSource sourceFileReader; // csv source
    TrackingDataView trackingDataView;
    DataService dataS = new DataService();
    Map<String, ToolMeasure> storedMeasurements;
    
    
    public MeasurementView(TrackingDataView tdv) {
    	this.trackingDataView = tdv;
    	this.source = tdv.getSource();
    	if(source != null) {
    		dataS = tdv.da;
    	}
    	init();
    }
    
    public MeasurementView() {
    	init();
    }
    
    public void setTrackingDeviceSource(TrackingDataSource source) {
        this.source = source;
        this.dataS.setTrackingDataSource(source);
    }

    private void updateMeasurementList() {
        measurementList.getItems().clear();
        for (String n : storedMeasurements.keySet()) {
            measurementList.getItems().add(n);
        }
    }
    
    private void init() {
    	
    	toolList = new ListView<String>();
    	measurementList = new ListView<String>();
    	storedMeasurements = new LinkedHashMap<String, ToolMeasure>();
    	
    	toLoadField = new TextField("50");
    	toLoadField.setPrefWidth(100);
    	
    	
    	address = new TextField();
    	searchButton = new Button("Load Data");
    	searchButton.setOnAction(loadFromFile());
    	
    	loadTool = new Button("Add Measurement");
    	loadTool.setOnAction(addMeasurement());
    	
    	start2 = new Button("Start Measurement");
    	start2.setOnAction(startMeasurement());
    	finish2 = new Button("End Measurement");
    	finish2.setOnAction(endMeasurement());
    	
    	cBJitterP = new CheckBox("Jitterposition");
    	cBJitterR = new CheckBox("Jitterrotation");
    	cBCorrectnessP = new CheckBox("Accuracy-Position");
    	cBCorrectnessR = new CheckBox("Accuracy-Rotation");
    	
    	lCalcJR = new Label();
    	lCalcC = new Label();
    	lCalcJP = new Label();
    	
    	measurementtyp = new ChoiceBox<String>();
    	measurementtyp.getItems().addAll("Jitter","Correctness");
    	measurementtyp.setOnAction(switchType());
    	distanceF = new TextField("30");
    	distanceF.setPrefWidth(30);
    	rotationL1 = new Label("x:");
    	rotationL1.setStyle("-fx-font-weight: bold; -fx-text-align: center;");
    	rotationL2 = new Label("y:");
    	rotationL2.setStyle("-fx-font-weight: bold; -fx-text-align: center;");
    	rotationL3 = new Label("z:");
    	rotationL3.setStyle("-fx-font-weight: bold; -fx-text-align: center;");
    	rotationL4 = new Label("r:");
    	rotationL4.setStyle("-fx-font-weight: bold; -fx-text-align: center;");
    	rotationAngle = new TextField();
    	rotationAngle.setPrefWidth(30);
    	rotationAngle1 = new TextField();
    	rotationAngle1.setPrefWidth(30);
    	rotationAngle2 = new TextField();
    	rotationAngle2.setPrefWidth(30);
    	rotationAngle3 = new TextField();
    	rotationAngle3.setPrefWidth(30);
    	calculate = new Button("Calculate");    	
    	calculate.setOnAction(calculate());
    	
    	toolList.setPrefHeight(100.0);
    	measurementList.setPrefHeight(100.0);
    }
    
	@Override
	public Tab createTab() {
		VBox main = new VBox();
		HBox hbox = new HBox();
		VBox vbox = new VBox(5);
    	VBox vbox1 = new VBox();
    	HBox hbox1 = new HBox();
		
    	// select CSV
		GridPane grid = new GridPane();
		grid.setHgap(5.0);
		grid.setVgap(5.0);
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.addRow(0, new Label("CSV-Datafile:"), address, searchButton);
		grid.addRow(1, new Label("Number of samples to load"), toLoadField);
		
		// continuous data 
		HBox h = new HBox(5);
		h.setPadding(new Insets(10, 10, 10, 10));
		h.setBackground(new Background(new BackgroundFill(Color.LIGHTSKYBLUE, null, null)));
		h.getChildren().addAll(new Label("Capture contiuous data"), start2, finish2);
		
		// select calculation details
		GridPane grid2 = new GridPane();
		grid2.setHgap(5.0);
		grid2.setVgap(5.0);
		grid2.setPadding(new Insets(10, 10, 10, 10));
		grid2.addRow(0,  new Label("Type of Measurement"), measurementtyp);
		grid2.addRow(1, new Label("Expected Distance [mm]"), distanceF);
		grid2.add(new Label("Quaternion"), 0, 3);
		
		GridPane grid3 = new GridPane();
		grid3.setHgap(5.0);
		grid3.setVgap(5.0);
		grid3.setPadding(new Insets(0, 100, 10, 20));
		grid3.addRow(0, rotationL1, rotationAngle);
		grid3.addRow(1, rotationL2, rotationAngle1);
		grid3.addRow(2, rotationL3, rotationAngle2);
		grid3.addRow(3, rotationL4, rotationAngle3);
		
		GridPane.setHalignment(rotationL1, HPos.RIGHT);
		GridPane.setHalignment(rotationL2, HPos.RIGHT);
		GridPane.setHalignment(rotationL3, HPos.RIGHT);
		GridPane.setHalignment(rotationL4, HPos.RIGHT);
		
		GridPane grid4 = new GridPane();
		grid4.setHgap(5.0);
		grid4.setVgap(5.0);
		grid4.setPadding(new Insets(10, 10, 10, 10));
		grid4.addRow(0, cBJitterP, lCalcJP);
		grid4.addRow(1, cBCorrectnessP, lCalcC);
		grid4.addRow(2, cBJitterR, lCalcJR);
		grid4.addRow(3, cBCorrectnessR);
		grid4.add(calculate, 1, 4);
		
		
    	// tools
    	vbox.getChildren().addAll(new Label("Available Tools"), toolList, loadTool);
    	vbox.setPadding(new Insets(10, 10, 10, 10));
    	hbox.getChildren().addAll(grid, vbox);
    	
    	// captured measurements list
    	vbox1.getChildren().addAll(new Label("Captured Measurements"), measurementList);
    	vbox1.setPadding(new Insets(10, 10, 10, 10));
    	
    	hbox1.getChildren().addAll(grid3, grid4);
    	main.getChildren().addAll(hbox, h, vbox1, grid2, hbox1);
    	
    	Tab tab = new Tab("Measurement View");
    	tab.setContent(main);
    	
		return tab;
	}
	
	private EventHandler<ActionEvent> loadFromFile(){
		return event -> {
			try {
				FileChooser fc = new FileChooser();
				fc.setTitle("Select CSV File");
				fc.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
				File f = fc.showOpenDialog(new Stage());
				if(f != null) {
					address.setText(f.getAbsolutePath());
					
		            sourceFileReader = new CSVFileReader(f.getAbsolutePath());
		            toolList.getItems().clear();;
		            for (Tool t : sourceFileReader.update()) {
		                toolList.getItems().add(t.getName());
		            }
		            
		        } else {
		        	address.setText("No file selected");
		        }
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
	}
	
	
	private EventHandler<ActionEvent> startMeasurement() {
		return event -> {
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
            
            if(source != null && trackingDataView.timeline != null && !timerOn) {
        		
            	dataS.restartMeasurements();
            	timerOn = true;
            	timer = new Timer();
            	TimerTask tt = new TimerTask(){
            		@Override
            		public void run() {
            			System.out.println("timer runs");
            			dataS.getDataManager().getNextData(1);
            		}
            	};
            	timer.schedule(tt, 0, 50);
            }
		};
	}
	
	
	private EventHandler<ActionEvent> endMeasurement() {
		return event -> {
			if (source == null) { 
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Attention!");
				a.setHeaderText(null);
				a.setContentText("Tracking not started yet (aborting)!");
				a.showAndWait();
            }
			
			if(timer != null && timerOn) {
				timer.cancel();
				timerOn = false;
				storedMeasurements.put("Measurement " + measurementCounter + "("
	                    + dataS.getDataManager().getToolMeasures().get(0).getName()
	                    + ")", dataS.getDataManager().getToolMeasures().get(0));
	            this.updateMeasurementList();
	            measurementCounter++;
			}
		};
	}
	
	private EventHandler<ActionEvent> switchType() {
		return event -> {
			switch(measurementtyp.getValue()) {
			case "Correctness":
				distanceF.setDisable(false);
				rotationAngle.setDisable(false);
				rotationAngle1.setDisable(false);
				rotationAngle2.setDisable(false);
				rotationAngle3.setDisable(false);
				break;
			case "Jitter": 
				distanceF.setDisable(true);
				rotationAngle.setDisable(true);
				rotationAngle1.setDisable(true);
				rotationAngle2.setDisable(true);
				rotationAngle3.setDisable(true);
				break;
			}
		};
	}
	
	private EventHandler<ActionEvent> calculate() throws NumberFormatException {
		return event -> {
			if(measurementList.getItems().size() > 0) {
				System.out.println("Computing results");
				
	            ToolMeasure tool = (ToolMeasure) storedMeasurements.values()
	            	 .toArray()[measurementList.getSelectionModel().getSelectedIndex()];
	            AverageMeasurement avgMes = tool.getAverageMeasurement();
	
	            // Jitter Rotation
	            if (cBJitterR.isSelected()) {
	            	
	                lCalcJR.setText("0.00 mm");
	                lCalcJR.setText(new DecimalFormat("#0.00").format(avgMes.getRotationError()) + " mm");
	            }
	            // Jitter Position
	            if (cBJitterP.isSelected()) {
	                lCalcJP.setText("0.00 mm");
	                lCalcJP.setText(new DecimalFormat("#0.00").format(avgMes.getJitter()) + " mm");
	            }
	            // Correctness Rotation
	            if (cBCorrectnessR.isSelected()) {
		
	                Quaternion expectedrotation = new Quaternion().set(
	                        (float) Double.parseDouble(rotationAngle.getText()),
	                        (float) Double.parseDouble(rotationAngle1.getText()),
	                        (float) Double.parseDouble(rotationAngle2.getText()),
	                        (float) Double.parseDouble(rotationAngle3.getText()));
	
	                ToolMeasure firstTool = (ToolMeasure) storedMeasurements.values().toArray()[0];
	                ToolMeasure secondTool = (ToolMeasure) storedMeasurements.values().toArray()[1];
	
	                lCalcJR.setText(String.valueOf(dataS.getAccuracyRotation(
	                        expectedrotation, firstTool.getMeasurement().get(0), secondTool.getMeasurement().get(0))));
	
	            }
	            // Correctness Position
	            if (cBCorrectnessP.isSelected()) {
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
	                		Double.parseDouble(distanceF.getText()),
	                        firstTool.getAverageMeasurement(),
	                        secondTool.getAverageMeasurement())));
	            }
			}
		};
	}
	
	private EventHandler<ActionEvent> addMeasurement() {
		return event -> {
			try {
				if(toolList.getItems().size() > 0) {
					if(toolList.getSelectionModel().getSelectedItem() == null) {
						System.out.println("no selection");
						return;
					}
					DataService loadDataService = new DataService();
		            loadDataService.setTrackingDataSource(sourceFileReader);
		            loadDataService.loadNextData(Integer.parseInt(toLoadField.getText()), true);
		            ToolMeasure newMeasurement = loadDataService.getToolByName(toolList
		            	 .getSelectionModel()
		            	 .getSelectedItem());
		            storedMeasurements.put("Measurement " + measurementCounter + "("
		                    + newMeasurement.getName()
		                    + ", from file)", newMeasurement);
		            this.updateMeasurementList();
		            measurementCounter++;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
	}
	
	public void closeView() {
		if(timer != null) {
			timer.cancel();
		}
	}

}
