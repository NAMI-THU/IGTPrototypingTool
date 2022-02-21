package controller;

import algorithm.DataService;
import algorithm.ImageDataManager;
import algorithm.ImageDataProcessor;
import algorithm.ToolMeasure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import inputOutput.TransformationMatrix;
import inputOutput.VideoSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import userinterface.PlottableImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AutoTrackController implements Controller {

    private final DataService dataService = new DataService();
    private final ImageDataManager imageDataManager = new ImageDataManager();
    private final Map<String, Integer> deviceIdMapping = new LinkedHashMap<>();
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    @FXML
    public ChoiceBox<String> sourceChoiceBox;
    @FXML
    public CheckBox trackingConnectedStatusBox;
    @FXML
    public CheckBox regMatrixStatusBox;
    @FXML
    public Button regMatrixImportButton;
    @FXML
    public TextField outputPathField;
    @FXML
    public Button outputPathButton;
    @FXML
    public ComboBox<String> captureRateComboBox;
    @FXML
    public ToggleButton autoCaptureToggleButton;
    @FXML
    public Button singleCaptureButton;
    @FXML
    public ProgressIndicator captureProgressSpinner;
    @FXML
    public ProgressIndicator connectionProgressSpinner;
    @FXML
    public PlottableImage videoImagePlot;

    private TrackingDataController trackingDataController;
    private Label statusLabel;
    private Timeline videoTimeline;
    private Timeline autoCaptureTimeline;
    private BufferedImage currentShowingImage;
    private boolean captureScheduled = false;

    private TransformationMatrix transformationMatrix = new TransformationMatrix();

    private final ObservableList<XYChart.Series<Number, Number>> dataSeries = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        connectionProgressSpinner.setVisible(false);
        captureProgressSpinner.setVisible(false);
        sourceChoiceBox.getSelectionModel().selectedItemProperty().addListener(x -> changeVideoView());
        captureRateComboBox.getItems().addAll("1000", "2000", "5000", "10000", "30000");
        captureRateComboBox.getSelectionModel().select(0);

        var expression = trackingConnectedStatusBox.selectedProperty().and(regMatrixStatusBox.selectedProperty()).and(outputPathField.textProperty().isNotEmpty());
        autoCaptureToggleButton.disableProperty().bind(expression.not());
        singleCaptureButton.disableProperty().bind(expression.not());

        videoImagePlot.setData(dataSeries);

        // For Testing Purpose only
        regMatrixStatusBox.setSelected(true);

        loadAvailableVideoDevicesAsync();
    }

    @Override
    public void close() {
        unregisterController();
        if (videoTimeline != null) {
            videoTimeline.stop();
        }
        if (autoCaptureTimeline != null) {
            autoCaptureTimeline.stop();
        }
        imageDataManager.closeConnection();
    }

    /**
     * Enables the Main View to inject the tracking data controller
     *
     * @param trackingDataController the controller to inject
     */
    public void setTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
        this.dataService.setTrackingDataSource(trackingDataController.getSource());

        var trackingOkExpression = trackingDataController.sourceConnected.and(trackingDataController.visualizationRunning);
        trackingConnectedStatusBox.selectedProperty().bind(trackingOkExpression);
    }

    /**
     * Enables the Main View to inject the status label at the bottom of the window
     *
     * @param statusLabel The injected label
     */
    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }


    /**
     * Initializes the loading of available video devices. This is done asynchronously.
     * While loading, the connection spinner shows.
     */
    private void loadAvailableVideoDevicesAsync() {
        connectionProgressSpinner.setVisible(true);
        new Thread(() -> {
            createDeviceIdMapping();
            Platform.runLater(() -> {
                sourceChoiceBox.getItems().addAll(deviceIdMapping.keySet());
                if (!deviceIdMapping.isEmpty()) {
                    sourceChoiceBox.getSelectionModel().select(0);
                } else {
                    statusLabel.setText("No video devices found!");
                }
                connectionProgressSpinner.setVisible(false);
            });
        }).start();
    }

    /**
     * Tests out available video device ids. All devices that don't throw an error are added to the list.
     * This is bad style, but openCV does not offer to list available devices.
     */
    private void createDeviceIdMapping() {
        int currentDevice = 0;
        boolean deviceExists = imageDataManager.openConnection(VideoSource.LIVESTREAM, currentDevice);
        imageDataManager.closeConnection();
        while (deviceExists) {
            deviceIdMapping.put("Camera " + currentDevice, currentDevice);
            currentDevice++;
            deviceExists = imageDataManager.openConnection(VideoSource.LIVESTREAM, currentDevice);
            imageDataManager.closeConnection();
        }
    }

    /**
     * Changes the input stream for the video view. Also starts the timeline to update the current image.
     */
    private void changeVideoView() {
        if (!sourceChoiceBox.getSelectionModel().isEmpty()) {
            var selectedItem = sourceChoiceBox.getSelectionModel().getSelectedItem();
            int deviceId = deviceIdMapping.get(selectedItem);
            imageDataManager.closeConnection();
            imageDataManager.openConnection(VideoSource.LIVESTREAM, deviceId);
            if (videoTimeline == null) {
                videoTimeline = new Timeline();
                videoTimeline.setCycleCount(Animation.INDEFINITE);
                videoTimeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(100),
                                event -> this.updateVideoImage())
                );
                videoTimeline.play();
            }
        } else {
            videoTimeline.stop();
            videoTimeline = null;
        }
    }

    /**
     * Loads and displays the next image from the stream.
     * If a measurement is scheduled, it queries the current tracking data and saves the data.
     */
    private void updateVideoImage() {
        var matrix = imageDataManager.readMat();
        matrix = applyTransformations(matrix);
        currentShowingImage = ImageDataProcessor.Mat2BufferedImage(matrix);

        // Show Tracking Data
        if(trackingConnectedStatusBox.isSelected()){
            updateTrackingData();
        }

        if (this.captureScheduled) {
            this.captureScheduled = false;
            dataService.setTrackingDataSource(trackingDataController.getSource());
            dataService.getDataManager().restartMeasurements();
            var trackingData = dataService.getDataManager().getNextData(1);
            saveCapturedData(currentShowingImage, trackingData);
        }
        videoImagePlot.setImage(ImageDataProcessor.Mat2Image(matrix, ".png"));
    }

    /**
     * Loads the next tracking data point and displays it on the image-plot
     */
    private void updateTrackingData(){
        var source = trackingDataController.getSource();
        if(source == null){return;}
        dataService.setTrackingDataSource(trackingDataController.getSource());  // TODO should make a property for this

        source.update();
        List<ToolMeasure> tools = dataService.loadNextData(1);

        if (tools.isEmpty()) return;

        for (int i = 0; i < tools.size(); i++) {
            ToolMeasure tool = tools.get(i);
            if (dataSeries.size() <= i) {
                var series = new XYChart.Series<Number, Number>();
                series.setName(tool.getName());
                series.getData().add(new XYChart.Data<>(0,0));  // Workaround to display legend
                dataSeries.add(series);
                series.getData().remove(0);
            }

            var series = dataSeries.get(i);
            var measurements = tool.getMeasurement();
            var point = measurements.get(measurements.size() - 1).getPoint();
            var data = series.getData();
            var max_num_points = 5;

            data.add(new XYChart.Data<>(point.getX(), point.getY()));
            if(data.size() > max_num_points){
                data.remove(0);
            }
        }
    }

    /**
     * Request a capture in the next frame.
     * We don't want to write out the current image and tracking position, as they might not match exactly (-> refresh rate of the video view)
     * Therefore, we schedule to capture the next measurement once the video image is updated next.
     */
    private void doSingleCaptureOnNextFrame() {
        this.captureScheduled = true;
    }

    /**
     * Saves an image and tracking data to output files
     *
     * @param image        The current image to save
     * @param trackingData The corresponding tracking data measurements
     */
    private void saveCapturedData(BufferedImage image, List<ToolMeasure> trackingData) {
        try {
            File outputDirectory = new File(outputPathField.getText());
            var files = outputDirectory.listFiles();
            var nextIndex = ((files != null ? files.length : 0) / 2) + 1;
            ImageIO.write(image, "png", Path.of(outputDirectory.getAbsolutePath(), "capture_" + nextIndex + ".png").toFile());
            try (FileWriter fw = new FileWriter(Path.of(outputDirectory.getAbsolutePath(), "capture_" + nextIndex + ".json").toFile())) {
                gson.toJson(trackingData, fw);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Action that is called when the user wants to set an output directory
     */
    @FXML
    public void on_browseOutputDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        var directory = directoryChooser.showDialog(null);
        if (directory != null) {
            outputPathField.setText(directory.getAbsolutePath());
            outputPathField.positionCaret(directory.getAbsolutePath().length());
        }
    }

    /**
     * Action that is called when the user wants to open the output directory
     */
    @FXML
    public void on_openOutputDirectory() {
        var directory = outputPathField.getText();
        if (directory != null) {
            try {
                Desktop.getDesktop().open(new File(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when the "Single Capture" Button is pressed.
     */
    @FXML
    public void on_doSingleCapture() {
        doSingleCaptureOnNextFrame();
    }

    /**
     * Called, when the "Auto Capture" Button is toggled (on or off)
     */
    @FXML
    public void on_doAutoCapture() {
        if (autoCaptureToggleButton.isSelected()) {
            if (autoCaptureTimeline == null) {
                captureProgressSpinner.setVisible(true);
                autoCaptureTimeline = new Timeline();
                autoCaptureTimeline.setCycleCount(Animation.INDEFINITE);
                autoCaptureTimeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(Integer.parseInt(captureRateComboBox.getSelectionModel().getSelectedItem())),
                                event -> doSingleCaptureOnNextFrame())
                );
                autoCaptureTimeline.play();
            }
        } else {
            if (autoCaptureTimeline != null) {
                autoCaptureTimeline.stop();
                autoCaptureTimeline = null;
                captureProgressSpinner.setVisible(false);
            }
        }
    }

    /**
     * Called, when "Import Matrix" Button is pressed.
     */
    @FXML
    public void on_importMatrix() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Matrix JSON");
        var inputFile = fileChooser.showOpenDialog(null);
        if(inputFile == null){
            return;
        }
        try {
            transformationMatrix = TransformationMatrix.loadFromJSON(inputFile.getAbsolutePath());
            regMatrixStatusBox.setSelected(true);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies the transformation matrix to the image
     * @param mat The image to be transformed
     * @return The transformed image
     */
    private Mat applyTransformations(Mat mat){
        // TODO: Make sure which dimensions of the matrix are needed. We can't use the 3D matrix directly
        Mat warpDst = Mat.zeros( mat.rows(), mat.cols(), mat.type() );
        Imgproc.warpAffine(mat, warpDst, transformationMatrix.toMat(), warpDst.size());
        return warpDst;
    }
}
