package controller;

import algorithm.DataService;
import algorithm.ImageDataManager;
import algorithm.ImageDataProcessor;
import algorithm.ToolMeasure;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import inputOutput.VideoSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AutoTrackController implements Controller {

    @FXML
    public ImageView videoImage;

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
    public ComboBox<Integer> captureRateComboBox;
    @FXML
    public Button autoCaptureToggleButton;

    @FXML
    public ProgressIndicator captureProgressSpinner;
    @FXML
    public ProgressIndicator connectionProgressSpinner;

    private TrackingDataController trackingDataController;
    private final DataService dataService = new DataService();
    private final ImageDataManager imageDataManager = new ImageDataManager();
    private Label statusLabel;

    private Map<String, Integer> deviceIdMapping = new LinkedHashMap<>();
    private Timeline videoTimeline;
    private BufferedImage currentShowingImage;
    private boolean captureScheduled = false;
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        connectionProgressSpinner.setVisible(false);
        captureProgressSpinner.setVisible(false);
        sourceChoiceBox.getSelectionModel().selectedItemProperty().addListener(x -> changeVideoView());
        captureRateComboBox.getItems().addAll(1000, 2000, 5000, 10000, 30000);

        loadAvailableVideoDevicesAsync();
    }

    @Override
    public void close() {
        unregisterController();
        videoTimeline.stop();
        imageDataManager.closeConnection();
    }

    public void setTrackingDataController(TrackingDataController trackingDataController) {
        this.trackingDataController = trackingDataController;
        this.dataService.setTrackingDataSource(trackingDataController.getSource());

        var trackingOkExpression = trackingDataController.sourceConnected.and(trackingDataController.visualizationRunning);
        trackingConnectedStatusBox.selectedProperty().bind(trackingOkExpression);
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
        this.statusLabel.setText("");
    }


    private void loadAvailableVideoDevicesAsync(){
        connectionProgressSpinner.setVisible(true);
        new Thread(() -> {
            createDeviceIdMapping();
            Platform.runLater(() -> {
                sourceChoiceBox.getItems().addAll(deviceIdMapping.keySet());
                if(!deviceIdMapping.isEmpty()) {
                    sourceChoiceBox.getSelectionModel().select(0);
                }else{
                    statusLabel.setText("No video devices found!");
                }
                connectionProgressSpinner.setVisible(false);
            });
        }).start();
    }

    private void createDeviceIdMapping(){
        int currentDevice = 0;
        boolean deviceExists = imageDataManager.openConnection(VideoSource.LIVESTREAM, currentDevice);
        imageDataManager.closeConnection();
        while(deviceExists){
            deviceIdMapping.put("Camera "+currentDevice, currentDevice);
            currentDevice++;
            deviceExists = imageDataManager.openConnection(VideoSource.LIVESTREAM, currentDevice);
            imageDataManager.closeConnection();
        }
    }

    private void changeVideoView(){
        if(!sourceChoiceBox.getSelectionModel().isEmpty()){
            var selectedItem = sourceChoiceBox.getSelectionModel().getSelectedItem();
            int deviceId = deviceIdMapping.get(selectedItem);
            imageDataManager.closeConnection();
            imageDataManager.openConnection(VideoSource.LIVESTREAM, deviceId);
            if(videoTimeline == null){
                videoTimeline = new Timeline();
                videoTimeline.setCycleCount(Animation.INDEFINITE);
                videoTimeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(100),
                                event -> this.updateVideoImage())
                );
                videoTimeline.play();
            }
        }else{
            videoTimeline.stop();
            videoTimeline = null;
        }
    }

    private void updateVideoImage(){
        var matrix = imageDataManager.readMat();
        currentShowingImage = ImageDataProcessor.Mat2BufferedImage(matrix);
        if(this.captureScheduled){
            this.captureScheduled = false;
            dataService.getDataManager().restartMeasurements();
            var trackingData = dataService.getDataManager().getNextData(1);
            saveCapturedData(currentShowingImage, trackingData);
        }
        videoImage.setImage(ImageDataProcessor.Mat2Image(matrix, ".png"));
    }

    private void doSingleCaptureOnNextFrame(){
        // we don't want to write out the current image and tracking position, as they might not match exactly (-> refresh rate of the video view)
        // Therefore, we schedule to capture the next measurement once the video image is updated next.
        this.captureScheduled = true;
    }

    private void saveCapturedData(BufferedImage image, List<ToolMeasure> trackingData){
        try {
            File outputDirectory = new File(outputPathField.getText());
            var files = outputDirectory.listFiles();
            var nextIndex = ((files != null ? files.length : 0) /2)+1;
            ImageIO.write(image, "png", Path.of(outputDirectory.getAbsolutePath(), "capture_"+nextIndex+".png").toFile());
            try(FileWriter fw = new FileWriter(Path.of(outputDirectory.getAbsolutePath(), "capture_"+nextIndex+".json").toFile())) {
                gson.toJson(trackingData, fw);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    public void on_browseOutputDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Directory");
        var directory = directoryChooser.showDialog(null);
        if(directory != null){
            outputPathField.setText(directory.getAbsolutePath());
            outputPathField.positionCaret(directory.getAbsolutePath().length());
        }
    }

    @FXML
    public void on_openOutputDirectory(){
        var directory = outputPathField.getText();
        if(directory != null){
            try {
                Desktop.getDesktop().open(new File(directory));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void on_doSingleCapture(){
        doSingleCaptureOnNextFrame();
    }
}