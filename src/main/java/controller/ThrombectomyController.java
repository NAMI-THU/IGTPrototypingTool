package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.Measurement;
import algorithm.ToolMeasure;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import userinterface.ImageScatterChart;
import util.FormatManager;

public class ThrombectomyController implements Controller {

	@FXML private ImageScatterChart chart1;
	@FXML private ImageScatterChart chart2;
	@FXML private ImageScatterChart chart3;
	@FXML private Button setPositionBtn;
	@FXML private Button loadCoronalBtn;
	@FXML private Button loadSagittalBtn;
	@FXML private Button loadAxialBtn;
	@FXML private Label imageLabel;
	@FXML private TextField imageXValue;
	@FXML private TextField imageYValue;
	@FXML private TextField imageScale;
	@FXML private AnchorPane positionDetailBox;
	private HashMap<String, XYChart.Series<Double, Double>[]> toolSeriesMap;
	private TrackingDataController trackingDataController;

	public ThrombectomyController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		toolSeriesMap = new HashMap<String, XYChart.Series<Double, Double>[]>();
	}

	public void setTrackingDataController(TrackingDataController trackingDataController) {
		this.trackingDataController = trackingDataController;
	}
	
	@FXML
	private void loadFile(Event e) {

		if (e.getSource().equals(loadCoronalBtn)) {
			loadFile(chart1);
		} else if (e.getSource().equals(loadAxialBtn)) {
			loadFile(chart2);
		} else if (e.getSource().equals(loadSagittalBtn)) {
			loadFile(chart3);
		}
	}

	/**
	 * this method opens a file chooser to select an image file (png/jpg) 
	 * to be displayed behind the visualization
	 * of the tracking data
	 * @param chart - ImageScatterChart to load img into
	 */
	private void loadFile(ImageScatterChart chart) throws NumberFormatException {

		FileChooser fc = new FileChooser();
		fc.setTitle("Load image");
		fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.png"));
		File file = fc.showOpenDialog(new Stage());
		
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
			
			if(chart.getIv() != null) {
				chart.removeImage();
			}
			chart.setIv(iv);
			// add eventlisteners
			editImagePosition(chart);
			
			// make image values manually editable for selected chart
			setPositionBtn.addEventHandler(ActionEvent.ACTION,(event2 -> {
				if (chart.getTitle().equals(imageLabel.getText())) {
					if (Double.valueOf(imageXValue.getText()) != null) {
						chart.getIv().setX((Double.parseDouble(imageXValue.getText())));
						imageXValue.setStyle("-fx-border-color: transparent;");
					} else {
						imageXValue.setStyle("-fx-border-color: red;");
					}

					if (Double.valueOf(imageYValue.getText()) != null) {
						chart.getIv().setY((Double.parseDouble(imageYValue.getText())));
						imageYValue.setStyle("-fx-border-color: transparent;");
					} else {
						imageYValue.setStyle("-fx-border-color: red;");
					}

					if (Double.valueOf(imageScale.getText()) != null) {
						chart.getIv().setScaleX(Double.parseDouble(imageScale.getText()));
						imageScale.setStyle("-fx-border-color: transparent;");
					} else {
						imageScale.setStyle("-fx-border-color: red;");
					}
				}
			}));
			
		} else {
			System.out.println("No file selected");
		}
	}

	/**
	 * displays position and scale of the selected chart's image
	 * enables manually setting position and scale of that image
	 * @param chart is the selected imagescatterchart
	 */
	public void editImagePosition(ImageScatterChart chart) throws NumberFormatException {

		if (chart.getIv() != null) {
			// use values of selected image when image is clicked
			chart.setOnMouseClicked(event -> {
				positionDetailBox.setVisible(true);
				imageLabel.setText(chart.getTitle());
				imageXValue.setText(FormatManager.toString(chart.getIv().getX()));
				imageYValue.setText(FormatManager.toString(chart.getIv().getY()));
				imageScale.setText(FormatManager.toString(chart.getIv().getScaleX()));
			});

			// use values of selected image when scale is changed by scrolling
			chart.getIv().scaleXProperty().addListener((event, oldX, newX) -> {
				positionDetailBox.setVisible(true);
				imageLabel.setText(chart.getTitle());
				imageXValue.setText(FormatManager.toString(chart.getIv().getX()));
				imageYValue.setText(FormatManager.toString(chart.getIv().getY()));
				imageScale.setText(FormatManager.toString(chart.getIv().getScaleX()));
			});
		}
	}

	@FXML
	private void showTrackingData() {

		if (trackingDataController.source != null) {
			if (trackingDataController.timeline != null) {
				// vllt noch anders lösen
//				this.toolSeriesMap.forEach((tool, seriesArray) -> {
//					for(XYChart.Series<Double, Double> dataSeries : seriesArray) {
//            			dataSeries.getData().clear();
//            		}
//				});
				trackingDataController.timeline.getKeyFrames().add(
						new KeyFrame(Duration.millis(100),
								event2 -> updateThrombectomyDiagrams())
						);
				// timeline has not been started in trackingdata view
			} else {
//					message.setText("Start Tracking in Main Window first.");
			}
		} else {
				// no data source
//				message.setText("No Tracking Data Source.");
		}
	}

	private void updateThrombectomyDiagrams() {

			List<ToolMeasure> tools = trackingDataController.ds.loadNextData(1);
			if (tools.isEmpty()) return;
			
			for (ToolMeasure tool : tools) {
				
				List<Measurement> measurements = tool.getMeasurement();
				
				if(!toolSeriesMap.containsKey(tool.getName())) {
					createSeriesForTool(tool.getName());
				}
				
				XYChart.Series<Double, Double>[] toolSeries = toolSeriesMap.get(tool.getName());
		        for (XYChart.Series<Double, Double> s : toolSeries) {
		        	s.getData().clear();
		        }
		        //use the last 5 measurements, otherwise blending will be a problem during motion
		        for (int i = 1; i < 5; i++) { 
		            if (measurements.size() - i < 0) {
		                break;
		            }
		            double x = measurements.get(measurements.size() - i).getPoint().getX();
		            double y = measurements.get(measurements.size() - i).getPoint().getY();
		            // use positive values so tracking data is displayed like in reality
		            double z = measurements.get(measurements.size() - i).getPoint().getZ() * -1;
	
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
    	for (int i = 0; i < 3; i++) {
    		seriesArray[i] = new XYChart.Series();
    		seriesArray[i].getData().add(new XYChart.Data(0,0));
    		seriesArray[i].setName(toolname);
    	}

    	chart1.getData().addAll(seriesArray[0]);
    	chart2.getData().addAll(seriesArray[1]);
    	chart3.getData().addAll(seriesArray[2]);

    	this.toolSeriesMap.put(toolname, seriesArray);
	}

	public void close() {

	}
}
