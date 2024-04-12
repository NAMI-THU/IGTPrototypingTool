package controller;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
public class AnnotationController implements Controller {
    @FXML
    public VBox uploadedImages;
    @FXML
    private ImageView selectedImageView;
    private ImageView currentSelectedImageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization code goes here
    }
    @FXML
    @Override
    public void close() {
        unregisterController();
    }
    @FXML
    public void Handle_Upload_Functionality(ActionEvent actionEvent) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Images");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.gif", "*.bmp")
            );
            Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            List<File> files = fileChooser.showOpenMultipleDialog(currentStage);
            if (files != null) {
                for (File file : files) {
                    displayImage(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayImage(File file) {
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(event -> {
            if (currentSelectedImageView != null) {
                currentSelectedImageView.setStyle("");
            }
            selectImage(image);
            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
            currentSelectedImageView = imageView;
        });

        uploadedImages.getChildren().add(imageView);
    }
    private void selectImage(Image image) {
        if (selectedImageView != null) {
            selectedImageView.setImage(image);
            selectedImageView.setFitWidth(selectedImageView.getScene().getWidth());
            selectedImageView.setPreserveRatio(true);
        }
    }
}
