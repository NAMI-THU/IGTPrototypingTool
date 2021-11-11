package controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import userinterface.TrackingDataDisplay;
import util.FormatManager;

public class ThrombectomyController implements Controller {

    @FXML private ImageScatterChart chartCoronal;
    @FXML private ImageScatterChart chartAxial;
    @FXML private ImageScatterChart chartSagittal;
    @FXML private Button setPositionBtn;
    @FXML private Button loadCoronalBtn;
    @FXML private Button loadSagittalBtn;
    @FXML private Button loadAxialBtn;
    @FXML private Label imageLabel;
    @FXML private TextField imageXValue;
    @FXML private TextField imageYValue;
    @FXML private TextField imageScale;
    @FXML private AnchorPane positionDetailBox;
    private List<TrackingDataDisplay> toolDisplayList;
    private TrackingDataController trackingDataController;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.toolDisplayList = new ArrayList<>();
    }

    public void setTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }

    /**
     * method with only event as parameter so it can be used in fxml
     * @param e
     */
    @FXML
    private void loadFile(Event e) {

        if (e.getSource().equals(loadCoronalBtn)) {
            loadFile(chartCoronal);
        } else if (e.getSource().equals(loadAxialBtn)) {
            loadFile(chartAxial);
        } else if (e.getSource().equals(loadSagittalBtn)) {
            loadFile(chartSagittal);
        }
    }

    /**
     * this method opens a file chooser to select an image file (png/jpg)
     * to be displayed behind the visualization
     * of the tracking data
     * @param chart - ImageScatterChart to load img into
     */
    private void loadFile(ImageScatterChart chart) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Load image");
        fc.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File file = fc.showOpenDialog(new Stage());

        if(file == null) {
            statusLabel.setText("No file selected");
            return;
        }

        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        try {
            Image img = new Image(file.toURI().toURL().toString());
            iv.setImage(img);
            iv.setFitHeight(chart.getHeight());
            iv.setFitHeight(chart.getWidth());
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Image file could not be opened.", e);
        }

        chart.setIv(iv);
        // add eventlisteners
        editImagePosition(chart);
        // make image values manually editable for selected chart
        setPositionBtn.addEventHandler(ActionEvent.ACTION,(event2 -> {
            if (chart.getTitle().equals(imageLabel.getText())) {
                try {
                    chart.getIv().setX((Double.parseDouble(imageXValue.getText())));
                    imageXValue.getStyleClass().removeIf(style -> style.equals("error-textfield"));
                } catch (NumberFormatException e) {
                    imageXValue.getStyleClass().add("error-textfield");
                }
                try {
                    chart.getIv().setY((Double.parseDouble(imageYValue.getText())));
                    imageYValue.getStyleClass().removeIf(style -> style.equals("error-textfield"));
                } catch (NumberFormatException e) {
                    imageYValue.getStyleClass().add("error-textfield");
                }
                try {
                    chart.getIv().setScaleX(Double.parseDouble(imageScale.getText()));
                    imageScale.getStyleClass().removeIf(style -> style.equals("error-textfield"));
                } catch (NumberFormatException e) {
                    imageScale.getStyleClass().add("error-textfield");
                }
            }
        }));
    }

    /**
     * displays position and scale of the selected chart's image
     * enables manually setting position and scale of that image
     * @param chart is the selected imagescatterchart
     */
    public void editImagePosition(ImageScatterChart chart) {
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

    /**
     * start showing tracking data from source in charts
     * if source is set and visualization was started in
     * trackingdataview
     */
    @FXML
    private void showTrackingData() {
        if (trackingDataController.source == null) {
            statusLabel.setText("No Tracking Data Source");
            return;
        }
        // timeline has not been started in trackingdata view
        if (trackingDataController.timeline == null) {
            statusLabel.setText("Start Tracking in Tracking Data View first");
            return;
        }
        statusLabel.setText("");
        trackingDataController.timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                    event2 -> updateThrombectomyDiagrams())
                );
    }

    /**
     * Visualize tracking data from source. Checks if there are tools
     * available and creates data series for each tool. Visualization
     * uses last 5 measurements for blending.
     */
    private void updateThrombectomyDiagrams() {

        List<ToolMeasure> tools = trackingDataController.ds.loadNextData(1);
        if (tools.isEmpty()) return;

        for (ToolMeasure tool : tools) {

            TrackingDataDisplay display = checkToolDisplayList(tool.getName());
            // clear old data
            display.clearData();

            List<Measurement> measurements = tool.getMeasurement();
            //use the last 10 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 10; i++) {
                if (measurements.size() - i < 0) {
                    break;
                }
                // invert tracking data, so display fits the experiment's setup
                double x = measurements.get(measurements.size() - i).getPoint().getX();
                double y = measurements.get(measurements.size() - i).getPoint().getY();
                double z = measurements.get(measurements.size() - i).getPoint().getZ() * -1;

                display.addDataToSeries1(new XYChart.Data<>(x, y));
                display.addDataToSeries2(new XYChart.Data<>(x, z));
                display.addDataToSeries3(new XYChart.Data<>(z, y));
            }
        }
    }

    /**
     * check if display data exists for this tool
     * create display data if it does not exist
     */
    @SuppressWarnings("unchecked")
    private TrackingDataDisplay checkToolDisplayList(String toolName) {
        if (toolDisplayList.size() > 0) {
            for (TrackingDataDisplay d : toolDisplayList) {
                if (d.getToolName().equals(toolName)) return d;
            }
        }
        // create display data for tool
        TrackingDataDisplay newDisplay = new TrackingDataDisplay(toolName);
        chartCoronal.getData().addAll(newDisplay.getDataSeries1());
        chartAxial.getData().addAll(newDisplay.getDataSeries2());
        chartSagittal.getData().addAll(newDisplay.getDataSeries3());
        toolDisplayList.add(newDisplay);
        return newDisplay;
    }

    public void close() {
        statusLabel.setText("");
    }
}
