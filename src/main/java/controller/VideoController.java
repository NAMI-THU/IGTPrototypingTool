package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import algorithm.EnhanceImage;
import algorithm.ImageDataManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
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
    @FXML CheckBox denoiseCheckbox;
    @FXML Slider sigmaColor;
    @FXML Slider sigmaSpace;

    ImageDataManager dataManager = new ImageDataManager();
    Timeline timeline = new Timeline();

    private double imageWidth = 960;
    private double imageHeight = 540;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.sourceChoiceBox.getSelectionModel().selectFirst();
        this.setCropListener();
    }

    @Override
    public void close() {
        if(this.dataManager.getDataProcessor().isConnected()) {
            this.dataManager.closeConnection();
        }
    }

    /**
     * The connection to an image source is created according to the selected
     * option of the choice box.
     */
    @FXML
    public void connectToSource() {
        switch(sourceChoiceBox.getValue()) {
        case "Video Source":
            this.dataManager.openConnection(0);
            break;
        case "OpenIGTLink":
            this.dataManager.openConnection(1);
            break;
        case "Video File":
            File file = this.loadFile();
            if(file != null) {
                this.dataManager.getDataProcessor().setFilePath(file.getAbsolutePath());
                this.dataManager.openConnection(2);
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
        var width = Double.parseDouble(ivWidth.getText());
        var height = Double.parseDouble(ivHeight.getText());
        imageHeight = height;
        imageWidth = width;
        iv.setFitHeight(height);
        iv.setFitWidth(width);
    }

    private void update() {
        if(denoiseCheckbox.isSelected()){
            Image newImage = EnhanceImage.SharpenImage(dataManager.readMatImg(), sigmaColor.getValue(), sigmaColor.getValue(), imageWidth, imageHeight);
            iv.setImage(newImage);
        }
        else{
            iv.setImage(dataManager.readImg());
        }
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
        double initialValueW = Math.min(imageWidth, i.getWidth());
        double initialValueH = Math.min(imageHeight, i.getHeight());
        iv.setFitHeight(initialValueH);
        iv.setFitWidth(initialValueW);
        ivHeight.setText(Double.toString(initialValueH));
        ivWidth.setText(Double.toString(initialValueW));
    }

    /**
     * Add ChangeListeners to all spinners, so images from source are being cropped
     * before they are displayed.
     */
    private void setCropListener() {
        this.topSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setTopCrop(newValue.intValue()));
        this.bottomSpinner.valueProperty().addListener((o ,oldValue, newValue) -> this.dataManager.getDataProcessor().setBottomCrop(newValue.intValue()));
        this.rightSpinner.valueProperty().addListener((observable, oldValue, newValue) -> this.dataManager.getDataProcessor().setRightCrop(newValue.intValue()));
        this.leftSpinner.valueProperty().addListener((o ,oldValue, newValue) -> this.dataManager.getDataProcessor().setLeftCrop(newValue.intValue()));
    }
}
