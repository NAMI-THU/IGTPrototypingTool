package userinterface;

import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import algorithm.Measurement;
import algorithm.ToolMeasure;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class ThrombectomyView {
	
	private Stage stage;
	private FileChooser fc;
	private Button loadFileBtn1, loadFileBtn2, loadFileBtn3, loadTrackingDataBtn;
	
	private GridPane positionDetailBox;
	private Label imageLabel;
	private TextField imageXValue, imageYValue, imageScale;
	private Button setPositionBtn;
	
	private Label message;
	
	private ImageScatterChart chart1, chart2, chart3;
	
	private HashMap<String, XYChart.Series<Double, Double>[]> toolSeriesMap;
	private TrackingDataView trackingDataView;
	
	/* axis that fit for ndi aurora */
	NumberAxis x = new NumberAxis(-100, 200, 0);
	NumberAxis y = new NumberAxis(-200, 150, 0);
	NumberAxis z = new NumberAxis(-350, -150, 0);
	NumberAxis x2 = new NumberAxis(-100, 200, 0);
	NumberAxis y2 = new NumberAxis(-200, 150, 0);
	NumberAxis z2 = new NumberAxis(-350, -150, 0);
	
	public ThrombectomyView(TrackingDataView trackingDataView) {
		this.trackingDataView = trackingDataView;
		init();
	}
	
	private void init() {
		
		stage = new Stage();
		stage.setTitle("Thrombectomy View");
		
		/* initialize charts */
		chart1 = new ImageScatterChart("XY-plane", x, y);
		chart2 = new ImageScatterChart("XZ-plane", x2, z);
		chart3 = new ImageScatterChart("ZY-plane", z2, y2);
		
		toolSeriesMap = new HashMap<String, XYChart.Series<Double, Double>[]>();
		
		/* initialize buttons and add listeners */
		loadFileBtn1 = new Button("Select Image (Coronal)");
		loadFileBtn1.setMaxWidth(Double.MAX_VALUE);
		loadFileBtn1.setOnAction(loadFile(this.chart1));
		loadFileBtn2 = new Button("Select Image (Axial)");
		loadFileBtn2.setMaxWidth(Double.MAX_VALUE);
		loadFileBtn2.setOnAction(loadFile(this.chart2));
		loadFileBtn3 = new Button("Select Image (Sagittal)");
		loadFileBtn3.setMaxWidth(Double.MAX_VALUE);
		loadFileBtn3.setOnAction(loadFile(this.chart3));
		loadTrackingDataBtn = new Button("Show Tracking Data");
		loadTrackingDataBtn.setMaxWidth(Double.MAX_VALUE);
		loadTrackingDataBtn.setOnAction(showTrackingData());
		
		/* maybe use this for user feedback */
		message = new Label();
		
		/* input elements to set size and scale of image manually */
		imageLabel = new Label();
		imageLabel.setStyle("-fx-font-weight: bold;");
		imageXValue = new TextField();
		imageXValue.setMaxWidth(50);
		imageYValue = new TextField();
		imageYValue.setMaxWidth(50);
		imageScale = new TextField("1.0");
		imageScale.setMaxWidth(50);
		setPositionBtn = new Button("Set Position");
		
		
		/* layout */
		VBox buttonBox = new VBox();
		buttonBox.setSpacing(10);
		buttonBox.setPadding(new Insets(0, 10, 20, 10));
		buttonBox.getChildren().addAll(new Label("Button Menu"), loadFileBtn1, loadFileBtn2,
				loadFileBtn3, loadTrackingDataBtn);
//		buttonBox.setStyle("-fx-border-style: solid;"
//                + "-fx-border-width: 1;"
//                + "-fx-border-color: black");
		VBox imageBox1 = new VBox();
		imageBox1.setPadding(new Insets(0, 10, 20, 10));
		imageBox1.getChildren().addAll(chart1, chart2);
		
		
		positionDetailBox = new GridPane();
		positionDetailBox.setHgap(5);
		positionDetailBox.setVgap(5);
		positionDetailBox.add(imageLabel, 0, 0);
		positionDetailBox.addRow(1, new Label("X-Value:"), imageXValue);
		positionDetailBox.addRow(2, new Label("Y-Value:"), imageYValue);
		positionDetailBox.addRow(3, new Label("Scale:"), imageScale);
		positionDetailBox.add(setPositionBtn, 3, 4);
		positionDetailBox.setVisible(false);
		
		VBox imageBox2 = new VBox();
		imageBox2.getChildren().addAll(chart3, positionDetailBox);
		
		HBox mainBox = new HBox();
		mainBox.setMinSize(700, 550);
		mainBox.getChildren().addAll(imageBox1, imageBox2, buttonBox);
		
		
		stage.setScene(new Scene(mainBox));
		stage.show();
	}
	
	/**
	 * this method opens a file chooser to select an image file (png or jpg) to be displayed behind the visualization
	 * of the tracking data
	 * @param id to identify which image is loaded
	 */
	private EventHandler<ActionEvent> loadFile(ImageScatterChart chart) {
		return event -> {
						fc = new FileChooser();
			fc.setTitle("Load image");
			fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg","*.png"));
			File file = fc.showOpenDialog(stage);
			
			if(file != null) {
				ImageView iv = new ImageView();
				iv.setPreserveRatio(true);
				Image img;
				try {
					img = new Image(file.toURI().toURL().toString());
					iv.setImage(img);
					iv.setFitHeight(chart.getHeight());
					iv.setFitHeight(chart.getWidth());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
					
				chart.setIv(iv);
				// add eventlisteners
				editImagePosition(chart);
				
			} else {
				System.out.println("No file selected");
			}
		};
	}
	
	/**
	 * displays position and scale of the selected chart's image
	 * enables manually setting position and scale of that image
	 * @param chart is the selected imagescatterchart
	 */
	public void editImagePosition(ImageScatterChart chart) {
		
		if(chart.getIv() != null) {
			// use values of selected image when image is clicked
			chart.setOnMouseClicked(event -> {
				positionDetailBox.setVisible(true);
				imageLabel.setText(chart.getTitle());
				imageXValue.setText(toString(chart.getSavedX()));
				imageYValue.setText(toString(chart.getSavedY()));
				imageScale.setText(toString(chart.getSavedScale()));
			});
			
			// vllt doch scroll listener hier
			// use values of selected image when scale is changed by scrolling
			chart.getIv().scaleXProperty().addListener((event, oldX, newX) -> {
				positionDetailBox.setVisible(true);
				imageLabel.setText(chart.getTitle());
				imageXValue.setText(toString(chart.getSavedX()));
				imageYValue.setText(toString(chart.getSavedY()));
				imageScale.setText(toString(chart.getSavedScale()));
				
			});
			
			// make image values manually editable for selected chart
			setPositionBtn.setOnAction(event -> {
				if(chart.getTitle().equals(imageLabel.getText())) {
					if(formatToDouble(imageXValue.getText()) != null) {
						chart.setSavedX(formatToDouble(imageXValue.getText()));
						imageXValue.setStyle("-fx-border-color: transparent;");
					} else {
						imageXValue.setStyle("-fx-border-color: red;");
					}
					
					if(formatToDouble(imageYValue.getText()) != null) {
						chart.setSavedY(formatToDouble(imageYValue.getText()));
						imageYValue.setStyle("-fx-border-color: transparent;");
					} else {
						imageYValue.setStyle("-fx-border-color: red;");
					}
					
					if(formatToDouble(imageScale.getText()) != null) {
						chart.setSavedScale(formatToDouble(imageScale.getText()));
						imageScale.setStyle("-fx-border-color: transparent;");
					} else {
						imageScale.setStyle("-fx-border-color: red;");
					}
				}
			});
		}
	}
	
	private EventHandler<ActionEvent> showTrackingData() {
		return event -> {
			if(trackingDataView.source != null) {
				if(trackingDataView.timeline != null) {
				
				// vllt noch anders lösen
//				this.toolSeriesMap.forEach((tool, seriesArray) -> {
//					for(XYChart.Series<Double, Double> dataSeries : seriesArray) {
//            			dataSeries.getData().clear();
//            		}
//				});
				
				trackingDataView.timeline.getKeyFrames().add(
						new KeyFrame(Duration.millis(100),
								event2 -> updateThrombectomyDiagrams())
						);
				// timeline has not been started in trackingdata view
				} else {
					message.setText("Start Tracking in Main Window first.");
				}
			} else {
				// no data source
				message.setText("No Tracking Data Source.");
			}
		};
	}
	
	private void updateThrombectomyDiagrams() {
		
		List<ToolMeasure> tools = trackingDataView.da.loadNextData(1);
		if(tools.isEmpty()) return;
		
		for(ToolMeasure tool : tools) {
			
			List<Measurement> measurements = tool.getMeasurement();
			
			if(!toolSeriesMap.containsKey(tool.getName())) {
				createSeriesForTool(tool.getName());
			}
			
			XYChart.Series<Double, Double>[] toolSeries = toolSeriesMap.get(tool.getName());
	        for(XYChart.Series<Double, Double> s : toolSeries) {
	        	s.getData().clear();
	        }
	        
	        for (int i = 1; i < 5; i++) { //use the last 5 measurements, otherwise blending will be a problem during motion
	
	            if (measurements.size() - i < 0) {
	                break;
	            }
	
	            double x = measurements.get(measurements.size() - i).getPoint().getX();
	            double y = measurements.get(measurements.size() - i).getPoint().getY();
	            double z = measurements.get(measurements.size() - i).getPoint().getZ();

	            // display position and rotation of tool
	            if (i == 1) {
//	                DecimalFormat df = new DecimalFormat("0.00");
//	                double qX = measurements.get(measurements.size() - i).getRotation().getX();
//	                double qY = measurements.get(measurements.size() - i).getRotation().getY();
//	                double qZ = measurements.get(measurements.size() - i).getRotation().getZ();
//	                double qR = measurements.get(measurements.size() - i).getRotation().getW();
//	                
//	                position.get(tool.getName()).setText(tool.getName()+": [" 
//	                		+ df.format(x) + ";" + df.format(y) + ";" + df.format(z) + "]");
//	                rotation.get(tool.getName()).setText(tool.getName()+": [" 
//	                		+ df.format(qX) + ";" + df.format(qY) + ";" + df.format(qZ) + ";" + df.format(qR) + "]");
	            }
	            
	            toolSeries[0].getData().add(new XYChart.Data<Double, Double>(x,y));
	            toolSeries[1].getData().add(new XYChart.Data<Double, Double>(x,z));
	            toolSeries[2].getData().add(new XYChart.Data<Double, Double>(z,y));
	        }
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createSeriesForTool(String toolname) {
		XYChart.Series[] seriesArray = new XYChart.Series[3];
    	
    	// Series needs to have a dataset so name and symbol are set correctly
    	for(int i=0; i<3; i++) {
    		seriesArray[i] = new XYChart.Series();
    		seriesArray[i].getData().add(new XYChart.Data(0,0));
    		seriesArray[i].setName(toolname);
    	}
    	
    	chart1.getData().addAll(seriesArray[0]);
    	chart2.getData().addAll(seriesArray[1]);
    	chart3.getData().addAll(seriesArray[2]);
    	
    	this.toolSeriesMap.put(toolname, seriesArray);
	}

	
	
	
//	private EventHandler<ScrollEvent> updateImageScale(ImageScatterChart chart) {
//		return event -> {
//			if(chart.getIv() != null) {
//				imageScale.set
//			}
//		};
//	}
	
	private Double formatToDouble(String input) {
		try {
			return Double.valueOf(input);
		} catch(Exception e) {
			return null;
		}
	}
	
	private String toString(Double input) {
		DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
		return df.format(input);
	}
	
}
