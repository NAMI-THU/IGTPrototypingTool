package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.AnnotationData;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AnnotationController implements Controller {
    @FXML
    public VBox uploadedImages;
    @FXML
    public Button uploadImagesButton;
    @FXML
    public ScrollPane selectedImagePane;
    @FXML
    private ImageView selectedImageView;
    private ImageView currentSelectedImageView;
    @FXML
    private Pane annotationPane;

    private Rectangle annotatedRectangle;
    private Circle middlePoint;
    private boolean dragged = false;
    private double annotationPointX, annotationPointY;

    private List<File> selectedImages;

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

            this.selectedImages = fileChooser.showOpenMultipleDialog(currentStage);

            if (selectedImages != null) {
                for (File file : selectedImages) {
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
            selectImage(image, imageView);
        });

        uploadedImages.getChildren().add(imageView);
    }

    private void selectImage(Image image, ImageView imageView) {
        if (selectedImageView != null && currentSelectedImageView != imageView) {
            selectedImageView.setImage(image);
            selectedImageView.setFitWidth(selectedImageView.getScene().getWidth());
            selectedImageView.setPreserveRatio(true);

            annotationPane.getTransforms().clear();
            // Create a new Scale transformation for the ImageView
            Scale scale = new Scale();
            annotationPane.getTransforms().add(scale);

            // Add a ScrollEvent handler to the ScrollPane
            annotationPane.setOnScroll(event -> {
                if (event.isControlDown()) {


                    // Adjust the pivot points to the mouse's current position
                    scale.setPivotX(event.getX());
                    scale.setPivotY(event.getY());

                    double zoomFactor = 1.05;

                    if (event.getDeltaY() > 0) {
                        // Zoom in
                        scale.setX(scale.getX() * zoomFactor);
                        scale.setY(scale.getY() * zoomFactor);
                    } else {
                        // Zoom out, but do not go below a certain minimum value
                        if (scale.getX() > 1.0 && scale.getY() > 1.0) {
                            scale.setX(scale.getX() / zoomFactor);
                            scale.setY(scale.getY() / zoomFactor);
                        }
                    }

                    event.consume();
                }
            });

            if (currentSelectedImageView != null) {
                currentSelectedImageView.setStyle("");
            }
            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
            currentSelectedImageView = imageView;
            checkForExistingAnnotationData();
            selectedImageView.setOnMouseDragged(this::dragAnnotationEvent);
            selectedImageView.setOnMousePressed(this::pressedAnnotationEvent);
            selectedImageView.setOnMouseReleased(this::releasedAnnotationEvent);
        }
    }

    public void Select_Next_Image(ActionEvent actionEvent) {
        try {
            if (currentSelectedImageView != null) {
                int currentIndex = uploadedImages.getChildren().indexOf(currentSelectedImageView);
                if (currentIndex < uploadedImages.getChildren().size() - 1) {
                    ImageView nextImageView = (ImageView) uploadedImages.getChildren().get(currentIndex + 1);
                    Image image = nextImageView.getImage();
                    selectImage(image, nextImageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Select_Previous_Image(ActionEvent actionEvent) {
        try {
            if (currentSelectedImageView != null) {
                int currentIndex = uploadedImages.getChildren().indexOf(currentSelectedImageView);
                if (currentIndex > 0) {
                    ImageView previousImageView = (ImageView) uploadedImages.getChildren().get(currentIndex - 1);
                    Image image = previousImageView.getImage();
                    selectImage(image, previousImageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAnnotations(ActionEvent actionEvent) {
      if(annotatedRectangle != null){
        annotationPane.getChildren().remove(annotatedRectangle);
        annotationPane.getChildren().remove(middlePoint);
        AnnotationData.getInstance().deleteAnnotation(selectedImageView.getImage().getUrl());
        middlePoint = null;
        annotatedRectangle = null;
      }
    }

    private void checkForExistingAnnotationData() {
        // Get Rectangle and 'unnormalize' the values
        annotationPane.getChildren().remove(annotatedRectangle);
        annotationPane.getChildren().remove(middlePoint);
        annotatedRectangle = AnnotationData.getInstance().getAnnotation(selectedImageView.getImage().getUrl());
        if (middlePoint != null) {
            middlePoint = null;
        }
        if (annotatedRectangle != null) {
            annotatedRectangle.setX(annotatedRectangle.getX() * selectedImageView.getImage().getWidth());
            annotatedRectangle.setY(annotatedRectangle.getY() * selectedImageView.getImage().getHeight());
            annotatedRectangle.setWidth(annotatedRectangle.getWidth() * selectedImageView.getImage().getWidth());
            annotatedRectangle.setHeight(annotatedRectangle.getHeight() * selectedImageView.getImage().getHeight());
            annotationPane.getChildren().add(annotatedRectangle);
            middlePoint = new Circle(0, 0, 2);
            middlePoint.setFill(Color.rgb(6, 207, 236));
            middlePoint.setCenterX(annotatedRectangle.getX() + (annotatedRectangle.getWidth() / 2));
            middlePoint.setCenterY(annotatedRectangle.getY() + (annotatedRectangle.getHeight() / 2));
            annotationPane.getChildren().add(middlePoint);

        }
    }

    private void dragAnnotationEvent(MouseEvent event) {
        if (event.isControlDown()) {
            double x2 = event.getX();
            double y2 = event.getY();
            annotatedRectangle.setX(annotationPointX);
            annotatedRectangle.setY(annotationPointY);
            annotatedRectangle.setWidth(Math.abs(x2 - annotationPointX));
            annotatedRectangle.setHeight(Math.abs(y2 - annotationPointY));
            middlePoint.setCenterX(annotationPointX + annotatedRectangle.getWidth() / 2);
            middlePoint.setCenterY(annotationPointY + annotatedRectangle.getHeight() / 2);
            dragged = true;
        }
    }

    private void pressedAnnotationEvent(MouseEvent event) {
        annotationPointX = event.getX();
        annotationPointY = event.getY();

        if (annotatedRectangle == null) {
            annotatedRectangle = new Rectangle();
            annotatedRectangle.setFill(Color.TRANSPARENT);
            annotatedRectangle.setStroke(Color.rgb(6, 207, 236));
            annotatedRectangle.setStrokeWidth(2);
            annotatedRectangle.setVisible(true);
            annotationPane.getChildren().add(annotatedRectangle); // Add it to the Scene
        }
        if (middlePoint == null) {
            middlePoint = new Circle(0, 0, 2);
            middlePoint.setFill(Color.rgb(6, 207, 236));
            annotationPane.getChildren().add(middlePoint);
        }

    }

    /**
     * Handle the Simple Annotation Event where the user clicks once without dragging.
     * Here the size of the rectangle is fixed
     *
     * @param event The Mouse Event
     */
    private void releasedAnnotationEvent(MouseEvent event) {
        if (!dragged) {
            annotatedRectangle.setX(annotationPointX - 10);
            annotatedRectangle.setY(annotationPointY - 10);
            annotatedRectangle.setWidth(20);
            annotatedRectangle.setHeight(20);
            middlePoint.setCenterX(annotationPointX);
            middlePoint.setCenterY(annotationPointY);
        } else {
            // Calculate the Middle point of the rectangle
            annotationPointX += annotatedRectangle.getWidth() / 2;
            annotationPointY += annotatedRectangle.getHeight() / 2;
        }

        dragged = false;

        AnnotationData.getInstance().addAnnotation(
                selectedImageView.getImage().getUrl(),
                annotationPointX / selectedImageView.getImage().getWidth(),
                annotationPointY / selectedImageView.getImage().getHeight(),
                annotatedRectangle.getWidth() / selectedImageView.getImage().getWidth(),
                annotatedRectangle.getHeight() / selectedImageView.getImage().getHeight()
        );

    }
}
