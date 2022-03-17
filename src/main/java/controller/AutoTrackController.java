package controller;

import algorithm.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import inputOutput.ExportMeasurement;
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
import org.opencv.core.*;
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
import java.util.*;
import java.util.List;

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
    private String lastMatrixPath = "";
    private List<ExportMeasurement> lastTrackingData = new ArrayList<>();

    // Used to crop the image to the actual content. Dirty describes whether the roi cache needs to be updated on the next transform, it's set when a new matrix is loaded
    private int[] matrixRoi = new int[4];
    private boolean roiDirty = true;

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
        videoImagePlot.registerImageClickedHandler(this::onImageClicked);

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
            createDeviceIdMapping(true);
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
     * @param fast Whether all available devices shall be enumerated. If set to true, there's a minimal performance gain.
     */
    private void createDeviceIdMapping(boolean fast) {
        if(fast){
            deviceIdMapping.put("Default Camera",0);
            return;
        }

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
        if(matrix != null && !matrix.empty()) {
            // Currently, we don't do image transformations, only tracking transformations
            // matrix = applyImageTransformations(matrix);
            currentShowingImage = ImageDataProcessor.Mat2BufferedImage(matrix);
        }

        // Show Tracking Data
        if(trackingConnectedStatusBox.isSelected()){
            updateTrackingData();
        }

        if (this.captureScheduled) {
            this.captureScheduled = false;
            saveCapturedData(currentShowingImage, lastTrackingData);
        }
        if(matrix != null && !matrix.empty()) {
            videoImagePlot.setImage(ImageDataProcessor.Mat2Image(matrix, ".png"));
        }
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

        lastTrackingData.clear();
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
            var max_num_points = 1;

            var shifted_points = applyTrackingTransformation(point.getX(), point.getY(), point.getZ());
            lastTrackingData.add(new ExportMeasurement(tool.getName(), point.getX(), point.getY(), point.getZ(), shifted_points[0], shifted_points[1], shifted_points[2]));

            data.add(new XYChart.Data<>(shifted_points[0],shifted_points[1]));
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
    private void saveCapturedData(BufferedImage image, List<ExportMeasurement> trackingData) {
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
            var path = inputFile.getAbsolutePath();
            lastMatrixPath = path;
            transformationMatrix = TransformationMatrix.loadFromJSON(path);
            roiDirty = true;
            regMatrixStatusBox.setSelected(true);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the matrix from the last given path (so it's easier to change from the outside)
     */
    @FXML
    public void on_reloadMatrix(){
        if (lastMatrixPath != null && !lastMatrixPath.isEmpty()) {
            try {
                transformationMatrix = TransformationMatrix.loadFromJSON(lastMatrixPath);
                roiDirty = true;
                regMatrixStatusBox.setSelected(true);
            }catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Applies the transformation matrix to the image
     * @param mat The image to be transformed
     * @return The transformed image
     */
    private Mat applyImageTransformations(Mat mat){
        Imgproc.warpAffine(mat, mat, transformationMatrix.getTranslationMat(), mat.size());
        Imgproc.warpAffine(mat, mat, transformationMatrix.getRotationMat(), mat.size());
        Imgproc.warpAffine(mat, mat, transformationMatrix.getScaleMat(), mat.size());

        /*
        var imagePoints = transformationMatrix.getImagePoints();
        var trackingPoints = transformationMatrix.getTrackingPoints();
        var outMat = new Mat();
        if(!imagePoints.empty() && !trackingPoints.empty()) {
            //Mat srcPoints = Converters.vector_Point_to_Mat(imagePoints, CvType.CV_64F);
            //Mat dstPoints = Converters.vector_Point_to_Mat(trackingPoints, CvType.CV_64F);

            var matrix = Imgproc.getPerspectiveTransform(imagePoints, trackingPoints);
            //var matrix = Calib3d.findHomography(imagePoints, trackingPoints, Calib3d.RANSAC);

            Imgproc.warpPerspective(mat, outMat, matrix, new Size());
            mat = outMat;
            mat = outMat;
            Imgproc.warpAffine(mat, outMat, transformationMatrix.getScaleMat(), mat.size());
            mat = outMat;
            Imgproc.warpAffine(mat, outMat, transformationMatrix.getTranslationMat(), mat.size());
        }
        */

        if(roiDirty){
            // Set noExecute to false if the Roi should be calculated (and thus, the image cropped)
            matrixRoi = MatHelper.calculateRoi(mat, true);
            roiDirty = false;
        }
        mat = mat.submat(matrixRoi[0],matrixRoi[1], matrixRoi[2],matrixRoi[3]);
        return mat;
    }

    /**
     * Applies the transformation on a tracking point
     * @param x X-Coordinate of the point
     * @param y Y-Coordinate of the point
     * @param z Z-Coordinate of the point - Currently ignored for transformation
     * @return The transformed point as array
     */
    private double[] applyTrackingTransformation(double x, double y, double z){
        var matrix = transformationMatrix.getTransformMatOpenCvEstimated();
        var vector = new Mat(3,1, CvType.CV_64F);
        vector.put(0,0,x);
        vector.put(1,0,y);
        vector.put(2,0,1);

        var pos_star = new Mat(2,1,CvType.CV_64F);
        Core.gemm(matrix, vector,1, new Mat(),1,pos_star);
        double[] out = new double[3];
        out[0] = pos_star.get(0,0)[0];
        out[1] = pos_star.get(1,0)[0];
        // TODO: Include z-Coordinate in transformation
        out[2] = z;
        return out;
    }


    /**
     * Called, when the user clicks on the live image. Used to get landmarks for transformation.
     * @param x X-Coordinate in the image
     * @param y Y-Coordinate in the image
     */
    private void onImageClicked(double x, double y){
        System.out.println("Image: ("+String.format(Locale.ENGLISH,"%.2f",x)+","+String.format(Locale.ENGLISH,"%.2f",y)+")\nImage (Relative): ("+String.format(Locale.ENGLISH,"%.2f",x)+","+String.format(Locale.ENGLISH,"%.2f",(currentShowingImage.getHeight()-y))+")");
        // We also directly save the tracking-coordinates at this point.
        var trackingData = lastTrackingData;
        for(var measurement : trackingData){
            System.out.println("Tracker "+measurement.toolName+": (" + String.format(Locale.ENGLISH,"%.2f",measurement.x_raw) + "," + String.format(Locale.ENGLISH,"%.2f",measurement.y_raw) + ")\n");
        }
    }
}
