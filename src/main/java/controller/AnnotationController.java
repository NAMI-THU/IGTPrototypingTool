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
    private boolean dragged = false;
    private double annotationPointX, annotationPointY;

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
            selectedImageView.setOnMousePressed(this::pressedAnnotationEvent);
            selectedImageView.setOnMouseReleased(this::releasedAnnotationEvent);
            selectedImageView.setOnMouseDragged(this::dragAnnotationEvent);

            // Get Rectangle and 'unnormalize' the values
            annotationPane.getChildren().remove(annotatedRectangle);
            annotatedRectangle = AnnotationData.getInstance().getAnnotation(selectedImageView.getImage().getUrl());
            if(annotatedRectangle != null) {
                annotatedRectangle.setX(annotatedRectangle.getX()*selectedImageView.getImage().getWidth());
                annotatedRectangle.setY(annotatedRectangle.getY()*selectedImageView.getImage().getHeight());
                annotatedRectangle.setWidth(annotatedRectangle.getWidth()*selectedImageView.getImage().getWidth());
                annotatedRectangle.setHeight(annotatedRectangle.getHeight()*selectedImageView.getImage().getHeight());
                annotationPane.getChildren().add(annotatedRectangle);
            }
        }
    }

    private void dragAnnotationEvent(MouseEvent event) {
        if(event.isControlDown()){
            double x2 = event.getX();
            double y2 = event.getY();
            annotatedRectangle.setX(annotationPointX);
            annotatedRectangle.setY(annotationPointY);
            annotatedRectangle.setWidth(Math.abs(x2 - annotationPointX));
            annotatedRectangle.setHeight(Math.abs(y2 - annotationPointY));
            dragged = true;
        }
    }

    private void pressedAnnotationEvent(MouseEvent event) {
        //To not create Duplicates
        annotationPointX = event.getX();
        annotationPointY = event.getY();

        if(annotatedRectangle == null) {
            annotatedRectangle = new Rectangle();
            annotatedRectangle.setFill(Color.TRANSPARENT);
            annotatedRectangle.setStroke(Color.rgb(6, 207, 236));
            annotatedRectangle.setStrokeWidth(2);
            annotatedRectangle.setVisible(true);
            annotationPane.getChildren().add(annotatedRectangle); // Add it to the Scene
        }

    }
    /**
     * Handle the Simple Annotation Event where the user clicks once without dragging.
     * Here the size of the rectangle is fixed
     * @param event The Mouse Event
     */
    private void releasedAnnotationEvent(MouseEvent event) {
        if(!dragged){
            annotatedRectangle.setX(annotationPointX - 10);
            annotatedRectangle.setY(annotationPointY - 10);
            annotatedRectangle.setWidth(20);
            annotatedRectangle.setHeight(20);
        }else {
            // Calculate the Middle point of the rectangle
            annotationPointX += annotatedRectangle.getWidth()/2;
            annotationPointY += annotatedRectangle.getHeight()/2;
        }
        dragged = false;
        AnnotationData.getInstance().addAnnotation(
                selectedImageView.getImage().getUrl(),
                annotationPointX /selectedImageView.getImage().getWidth(),
                annotationPointY /selectedImageView.getImage().getHeight(),
                annotatedRectangle.getWidth()/selectedImageView.getImage().getWidth(),
                annotatedRectangle.getHeight()/selectedImageView.getImage().getHeight()
        );

    }
}
