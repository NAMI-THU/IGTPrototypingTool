package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import algorithm.ImageDataManager;
import inputOutput.VideoSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoController implements Controller {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerController();
        this.sourceChoiceBox.getSelectionModel().selectFirst();
        this.setCropListener();
    }

    @Override
    public void close() {
        if(this.dataManager.getDataProcessor().isConnected()) {
            this.dataManager.closeConnection();
        }
        unregisterController();
    }

    /**
     * The connection to an image source is created according to the selected
     * option of the choice box.
     */
    @FXML
    public void connectToSource() {
        switch(sourceChoiceBox.getValue()) {
        case "Video Source":
            this.dataManager.openConnection(VideoSource.LIVESTREAM, 0);
            break;
        case "OpenIGTLink":
            this.dataManager.openConnection(VideoSource.OPENIGTLINK);
            break;
        case "Video File":
            File file = this.loadFile();
            if(file != null) {
                this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
                this.dataManager.openConnection(VideoSource.FILE);
            }
            break;
        }
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
        }
    }

    @FXML
    public void stopVideo() {
        dataManager.closeConnection();
        timeline.stop();
    }

    /**
     * Change ImageView size. If preserveRatio is not explicitly set to true,
     * height and width can be changed independently from each other.
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
        File file = fc.showOpenDialog(new Stage());

        return file;
    }

    /**
     * Set size of imageview according to size of the first transmitted image
     * and display values in text fields that are used to scale the image.
     */
    private void setInitialImageSize() {
        Image i = dataManager.readImg();
        iv.setFitHeight(i.getHeight());
        iv.setFitWidth(i.getWidth());
        ivHeight.setText(Double.toString(i.getHeight()));
        ivWidth.setText(Double.toString(i.getWidth()));
    }

    /**
     * Add ChangeListeners to all spinners, so images from source are being cropped
     * before they are displayed.
     */
    private void setCropListener() {
        this.topSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setTopCrop(newValue));
        this.bottomSpinner.valueProperty().addListener((o ,oldValue, newValue) -> this.dataManager.getDataProcessor().setBottomCrop(newValue));
        this.rightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setRightCrop(newValue));
        this.leftSpinner.valueProperty().addListener((o ,oldValue, newValue) -> this.dataManager.getDataProcessor().setLeftCrop(newValue));
    }
}
