package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import algorithm.ImageDataManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoController implements Controller {

    @FXML Button connectButton;
    @FXML Button startButton;
    @FXML Button stopButton;
    @FXML ImageView iv;
    @FXML ChoiceBox<String> sourceChoiceBox;
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
        case "Video file":
            this.loadFile();
            break;
        case "External device":
            break;
        }
    }

    @FXML
    public void startVideo() {
        if(dataManager.getDataProcessor() != null && dataManager.getDataProcessor().isConnected()) {
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

    private void update() {
// old way to load images with awt
//        BufferedImage bufImg = dataManager.readBufImg();
//        Image img = SwingFXUtils.toFXImage(bufImg, null);
//        iv.setImage(img);

        iv.setImage(dataManager.readImg());
    }

    private void loadFile() {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Video files","*.avi","*.mp4", "*.mkv", "*.mov", "*.3GP", "*.mpg");
        fc.setSelectedExtensionFilter(filter);
        File file = fc.showOpenDialog(new Stage());

        if(file != null) {
            this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
            this.dataManager.openConnection(2);
        }
    }

}
