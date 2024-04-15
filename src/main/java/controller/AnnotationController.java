package controller;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.AnnotationData;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
public class AnnotationController implements Controller {
    @FXML
    public VBox uploadedImages;
    @FXML
    private ImageView selectedImageView;
    private ImageView currentSelectedImageView;
    @FXML
    private Pane annotationPane;

    private Rectangle annotatedRectangle;

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
            selectedImageView.setOnMouseClicked(this::annotationEvent);

        }
    }

    /**
     * Handle the Annotation Event
     * @param event
     */
    private void annotationEvent(MouseEvent event) {
        System.out.println("MouseEvent: " + event);
        //To not create Duplicates
        if(annotatedRectangle == null) {
            annotatedRectangle = new Rectangle(event.getX() - 10, event.getY() - 10, 20, 20);
            annotatedRectangle.setFill(Color.TRANSPARENT);
            annotatedRectangle.setStroke(Color.rgb(6, 207, 236));
            annotatedRectangle.setStrokeWidth(2);
            annotatedRectangle.setVisible(true);
            annotationPane.getChildren().add(annotatedRectangle); // Add it to the Scene
        }else{
            annotatedRectangle.setX(event.getX() - 10);
            annotatedRectangle.setY(event.getY() - 10);
            annotatedRectangle.setWidth(20);
            annotatedRectangle.setHeight(20);
        }

        //AnnotationData.getInstance().addAnnotation(new AnnotationData.Annotation(event.getX() - 10, event.getY() - 10, ));
    }

}
