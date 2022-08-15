package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import algorithm.ImageDataManager;
import inputOutput.VideoSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

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
            // Stop the video stream and also the timeline
            stopVideo();
        }
        unregisterController();
    }

    /**
     * The connection to an image source is created according to the selected
     * option of the choice box.
     */
    @FXML
    public void connectToSource() {
        connectionIndicator.setVisible(true);
        switch(sourceChoiceBox.getValue()) {
        case "Video Source":
            connectToSourceAsync(VideoSource.LIVESTREAM, 0);
            break;
        case "OpenIGTLink":
            connectToSourceAsync(VideoSource.OPENIGTLINK);
            break;
        case "Video File":
            File file = this.loadFile();
            if(file != null) {
                this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
                connectToSourceAsync(VideoSource.FILE);
            }
            break;
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
                    startButton.setDisable(false);
                    startButton.requestFocus();
                }else{
                    statusLabel.setText("Unable to establish connection.");
                    logger.warning("Unable to esatblish connection for connection-id "+connectionId+", openConnection returned false.");
                    new Alert(Alert.AlertType.ERROR, "Unable to establish a connection!").show();
                }
            });
        }).start();
    }

    /**
     * If an image source is connected, image transmission starts.
     */
    @FXML
    public void startVideo() {
        if(dataManager.getDataProcessor() != null && dataManager.getDataProcessor().isConnected()) {
            this.setInitialImageSize();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                         event -> this.update())
            );
            timeline.play();
            stopButton.setDisable(false);
            startButton.setDisable(true);
            connectButton.setDisable(true);
        }
    }

    @FXML
    public void stopVideo() {
        dataManager.closeConnection();
        timeline.stop();
        // Need to reconnect first
        connectButton.setDisable(false);
        stopButton.setDisable(true);
    }

    /**
     * Change ImageView size. If preserveRatio is not explicitly set to true,
     * height and width can be changed independently of each other.
     */
    @FXML
    public void setIvSize() {
        iv.setFitHeight(Double.parseDouble(ivHeight.getText()));
        iv.setFitWidth(Double.parseDouble(ivWidth.getText()));
    }

    private void update() {
        iv.setImage(dataManager.readImg());
    }

    private File loadFile() {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video files","*.avi","*.mp4", "*.mkv", "*.mov", "*.3GP", "*.mpg");
        fc.setSelectedExtensionFilter(filter);

        return fc.showOpenDialog(new Stage());
    }

    /**
     * Set size of imageview according to size of the first transmitted image
     * and display values in text fields that are used to scale the image.
     */
    private void setInitialImageSize() {
        var image = dataManager.readImg();
        var height = image.getHeight();
        var width = image.getWidth();
        iv.setFitHeight(height);
        iv.setFitWidth(width);
        ivHeight.setText(Double.toString(height));
        ivWidth.setText(Double.toString(width));

        topSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) height));
        bottomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) height));
        rightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) width));
        leftSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,(int) width));

        // We don't need to remove the old listeners since the valueProperty will be a new one than before (because of the new ValueFactory)
        setCropListener();
    }

    /**
     * Add ChangeListeners to all spinners, so images from source are being cropped
     * before they are displayed.
     */
    private void setCropListener() {
        this.topSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setTopCrop(newValue));
        this.bottomSpinner.valueProperty().addListener((observable ,oldValue, newValue) -> this.dataManager.getDataProcessor().setBottomCrop(newValue));
        this.rightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setRightCrop(newValue));
        this.leftSpinner.valueProperty().addListener((observable ,oldValue, newValue) -> this.dataManager.getDataProcessor().setLeftCrop(newValue));
    }
}
