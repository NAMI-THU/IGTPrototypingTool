package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import algorithm.ImageDataManager;
import algorithm.ImageDataProcessor;
import inputOutput.VideoSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opencv.core.Mat;

import static inputOutput.VideoSource.LIVESTREAM;
import static inputOutput.VideoSource.OPENIGTLINK;

public class VideoController implements Controller {

    @FXML ProgressIndicator connectionIndicator;
    @FXML Button connectButton;
    @FXML Button startButton;
    @FXML Button stopButton;
    @FXML TextField ivHeight;
    @FXML TextField ivWidth;
    @FXML ImageView iv;
    @FXML ChoiceBox<String> sourceChoiceBox;
    @FXML Spinner<Integer> topSpinner;
    @FXML Spinner<Integer> bottomSpinner;
    @FXML Spinner<Integer> rightSpinner;
    @FXML Spinner<Integer> leftSpinner;

    ImageDataManager dataManager = new ImageDataManager();
    Timeline timeline = new Timeline();

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private Label statusLabel;

    private AiControllerOnnx aiController;
    private int sourceTracker;

    // measurement variables
    private int frameCount = 0;
    private long lastSecondTime = System.currentTimeMillis();
    private long lastMinuteTime = System.currentTimeMillis();
    private final List<Integer> fpsBuffer = new ArrayList<>();
    private final String fpsLogFile = "logs/logs2.txt";

    public void setAiController(AiControllerOnnx aiController) {
        this.aiController = aiController;
    }

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        this.sourceChoiceBox.getSelectionModel().selectFirst();
        this.setCropListener();
    }

    @Override
    public void injectStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    @Override
    public void close() {
        if(this.dataManager.getDataProcessor().isConnected()) {
            stopVideo();
        }
        unregisterController();
    }

    @FXML
    public void connectToSource() {
        connectionIndicator.setVisible(true);
        switch(sourceChoiceBox.getValue()) {
            case "Video Source" -> connectToSourceAsync(LIVESTREAM, 0);
            case "OpenIGTLink" -> connectToSourceAsync(OPENIGTLINK);
            case "Video File" -> {
                File file = this.loadFile();
                if(file != null) {
                    this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
                    connectToSourceAsync(VideoSource.FILE);
                }
            }
        }
    }

    private void connectToSourceAsync(VideoSource connectionId){
        connectToSourceAsync(connectionId, 0);
    }

    private void connectToSourceAsync(VideoSource connectionId, int deviceId){
        new Thread(() -> {
            var success = dataManager.openConnection(connectionId, deviceId);
            Platform.runLater(() -> {
                connectionIndicator.setVisible(false);
                if(success) {
                    if (connectionId == LIVESTREAM) {
                        sourceTracker = 0;
                        mainController.handleChangeStatus(0, 1);
                    } else if (connectionId == OPENIGTLINK) {
                        sourceTracker = 1;
                        mainController.handleChangeStatus(1, 1);
                    }
                    startButton.setDisable(false);
                    startButton.requestFocus();
                } else {
                    statusLabel.setText("Unable to establish connection.");
                    logger.warning("Unable to establish connection for connection-id " + connectionId);
                    new Alert(Alert.AlertType.ERROR, "Unable to establish a connection!").show();
                }
            });
        }).start();
    }

    @FXML
    public void startVideo() {
        if(dataManager.getDataProcessor() != null && dataManager.getDataProcessor().isConnected()) {
            switch (sourceTracker){
                case 0 -> mainController.handleChangeStatus(0,2);
                case 1 -> mainController.handleChangeStatus(1,2);
            }

            this.setInitialImageSize();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100), event -> this.update())
            );
            timeline.play();
            stopButton.setDisable(false);
            startButton.setDisable(true);
            connectButton.setDisable(true);
        }
    }

    @FXML
    public void stopVideo() {
        switch (sourceTracker){
            case 0 -> mainController.handleChangeStatus(0,0);
            case 1 -> mainController.handleChangeStatus(1,0);
        }
        dataManager.closeConnection();
        timeline.stop();
        connectButton.setDisable(false);
        stopButton.setDisable(true);
    }

    @FXML
    public void setIvSize() {
        iv.setFitHeight(Double.parseDouble(ivHeight.getText()));
        iv.setFitWidth(Double.parseDouble(ivWidth.getText()));
        if (aiController != null) {
            aiController.updateResolution(Double.parseDouble(ivHeight.getText()), Double.parseDouble(ivWidth.getText()));
        }
    }

    public void update() {
        Mat matrix = dataManager.readMat();
        if (matrix == null || matrix.empty()) return;

        // Update FPS logic
        frameCount++;
        long now = System.currentTimeMillis();

        if (now - lastSecondTime >= 1000) {
            fpsBuffer.add(frameCount);
            frameCount = 0;
            lastSecondTime = now;
        }

        if (now - lastMinuteTime >= 60_000) {
            double avgFps = fpsBuffer.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            fpsBuffer.clear();
            lastMinuteTime = now;

            String logLine = String.format("%s - Average FPS: %.2f%n",
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                    avgFps);

            System.out.print(logLine);

            File logFile = new File(fpsLogFile);
            try {
                logFile.getParentFile().mkdirs();
            } catch (Exception ignored) {}

            try (FileWriter writer = new FileWriter(fpsLogFile, true)) {
                writer.append(logLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create a copy of the matrix for ImageView
        Mat matrixCopy = matrix.clone();
        Image frame = matToImage(matrixCopy);
        iv.setImage(frame);

        // Send original matrix to AI controller
        if (aiController != null) {
            aiController.processFrame(matrix);
        }
    }

    private Image matToImage(Mat frame) {
        try {
            return ImageDataProcessor.Mat2Image(frame, ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File loadFile() {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
                "Video files","*.avi","*.mp4", "*.mkv", "*.mov", "*.3GP", "*.mpg");
        fc.setSelectedExtensionFilter(filter);
        return fc.showOpenDialog(new Stage());
    }

    private void setInitialImageSize() {
        var image = dataManager.readImg();
        var height = image.getHeight();
        var width = image.getWidth();
        iv.setFitHeight(height);
        iv.setFitWidth(width);
        ivHeight.setText(Double.toString(height));
        ivWidth.setText(Double.toString(width));

        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) height-1));
        bottomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) height-1));
        rightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) width-1));
        leftSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) width-1));

        setCropListener();
    }

    private void setCropListener() {
        this.topSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setTopCrop(newValue));
        this.bottomSpinner.valueProperty().addListener((observable ,oldValue, newValue) -> this.dataManager.getDataProcessor().setBottomCrop(newValue));
        this.rightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setRightCrop(newValue));
        this.leftSpinner.valueProperty().addListener((observable ,oldValue, newValue) -> this.dataManager.getDataProcessor().setLeftCrop(newValue));
    }
}
