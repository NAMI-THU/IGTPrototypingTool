package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import algorithm.ImageDataManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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
    Label status;
    ImageDataManager dataManager = new ImageDataManager();
    Timeline timeline = new Timeline();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void close() {
        if(this.dataManager.getDataProcessor().isConnected()) {
            this.dataManager.closeConnection();
        }
    }

    @FXML
    public void connectToSource() {
        switch(sourceChoiceBox.getValue()) {
        case "Webcam":
            this.dataManager.openConnection(0);
            break;
        case "OpenIGTLink":
        	this.dataManager.openConnection(1);
            break;
        case "Video file":
        	File file = this.loadFile();
            if(file != null) {
                this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
                this.dataManager.openConnection(2);
            }
            break;
        case "External device":
        	break;

        }
    }

    @FXML
    public void startVideo() {
        if(dataManager.getDataProcessor() != null && dataManager.getDataProcessor().isConnected()) {
        	// set iv size according to image size from source
        	setInitialImageSize();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().add(
                new KeyFrame(Duration.millis(100),
                         event -> update())
            );
            timeline.play();
        }
    }

    @FXML
    public void stopVideo() {
        dataManager.closeConnection();
        timeline.stop();
    }

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

    private void setInitialImageSize() {
    	Image i = dataManager.readImg();
    	iv.setFitHeight(i.getHeight());
    	iv.setFitWidth(i.getWidth());
    }

}
