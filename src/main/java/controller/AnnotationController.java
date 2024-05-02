package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import util.AnnotationData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class AnnotationController implements Controller {
    @FXML
    public VBox uploadedImages;     //Where the users see the files
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
    private Set<String> uploadedFilePaths = new HashSet<>();


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
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
            );
            Stage currentStage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            this.selectedImages = fileChooser.showOpenMultipleDialog(currentStage);

            if (selectedImages != null) {
                for (File file : selectedImages) {
                    if (!uploadedFilePaths.contains(file.getAbsolutePath())) {
                        displayImage(file);
                        uploadedFilePaths.add(file.getAbsolutePath());
                    } else {
                        // Optionally, alert the user that the file has already been uploaded
                        showAlert("Duplicate File", "The file " + file.getName() + " has already been uploaded.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImage(File file) throws UnsupportedEncodingException {
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        // Decode the file name to handle URL-encoded spaces and other characters
        String decodedFileName = URLDecoder.decode(file.getName(), StandardCharsets.UTF_8.toString());

        // Create a label with the decoded file name
        Label fileNameLabel = new Label(decodedFileName);
        fileNameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

        // Create a VBox to hold the label and the image view
        VBox vbox = new VBox(5); // 5 is the spacing between the label and the image
        vbox.getChildren().addAll(fileNameLabel, imageView);

        imageView.setOnMouseClicked(event -> {
            selectImage(image, imageView);
        });

        uploadedImages.getChildren().add(vbox);
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
    /**
     * Handles Export based Functionality
     * @param event The Mouse Event
     */
    @FXML
    private void handleExportAction(ActionEvent event) {
        if (selectedImageView != null && annotatedRectangle != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Annotations");

            try {
                String currentImageUrl = selectedImageView.getImage().getUrl();
                String currentImageName = new File(new URL(currentImageUrl).toURI().getPath()).getName();
                String initialFileName = currentImageName.substring(0, currentImageName.lastIndexOf('.')) + "_annotations.txt";
                fileChooser.setInitialFileName(initialFileName);
            } catch (Exception e) {
                fileChooser.setInitialFileName("default_annotations.txt");
                showAlert("Error", "There was an issue processing the image file name.");
                e.printStackTrace();
            }

            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());

            if (file != null) {
                saveAnnotationsToFile(file, AnnotationData.getInstance().getAnnotationEntry(selectedImageView.getImage().getUrl()));
            }
        } else {
            showNoAnnotationAlert();
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///Export-All-Functionality----------START

    @FXML
    private void handleExportAllAction(ActionEvent event) {
        // Checking if there are no images uploaded
        if (uploadedImages.getChildren().isEmpty()) {
            showAlert("Export Error", "There are no images to export.");
            return;
        }

        List<String> unannotatedImages = new ArrayList<>();
        for (Node node : uploadedImages.getChildren()) {
            ImageView imageView = (ImageView) node;
            if (AnnotationData.getInstance().getAnnotation(imageView.getImage().getUrl()) == null) {
                unannotatedImages.add(new File(imageView.getImage().getUrl()).getName());
            }
        }

        if (!unannotatedImages.isEmpty()) {
            showUnannotatedImagesAlert(unannotatedImages);
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Annotations");
        File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedDirectory != null) {
            AnnotationData.getInstance().getAnnotations().forEach((path, annotation) -> {
                File file = null;
                try {
                    file = new File(new URL(path).toURI());
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + "_annotations.txt";
                    File annotationFile = new File(selectedDirectory, fileName);
                    saveAnnotationsToFile(annotationFile, annotation);
                } catch (Exception e) {
                    showAlert("Error", "Failed to save annotations for " + file.getName());
                }
            });
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showUnannotatedImagesAlert(List<String> unannotatedImages) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Unannotated Images");
        alert.setHeaderText("The following images have no annotations:");

        VBox vbox = new VBox(5);
        for (String imageName : unannotatedImages) {
            Text imageText = new Text(imageName);
            vbox.getChildren().add(imageText);
        }

        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setPrefSize(300, 150);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        alert.getDialogPane().setContent(scrollPane);
        alert.setResizable(true);
        alert.showAndWait();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///Export-All-Functionality----------END



    /**
     * Handles the Saving of Annotations to a File
     * @param file The File to save the Annotations to
     * @param annotation The Annotation Data to save
     */

    private void saveAnnotationsToFile(File file, AnnotationData.PublicAnnotation annotation) {
        try (PrintWriter writer = new PrintWriter(file)) {
            String line = String.format("%d %.5f %.5f %.5f %.5f", 0, annotation.getMiddlePointX(), annotation.getMiddlePointY(), annotation.getBoundingBoxWidth(), annotation.getBoundingBoxHeight());
            writer.println(line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void showNoAnnotationAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("No Annotation Found");
        alert.setHeaderText(null);
        alert.setContentText("No Annotation Found to process.");
        alert.showAndWait();
    }

    /**
     * Handles Dark/Light-Mode based Functionality
     * @param event The Mouse Event
     */
    @FXML
    private void handleToggleTheme(ActionEvent event) {
        try {
            Scene scene = ((Node) event.getSource()).getScene();
            String lightModeUrl = getClass().getResource("/css/light-mode.css").toExternalForm();
            String darkModeUrl = getClass().getResource("/css/dark-mode.css").toExternalForm();

            System.out.println("Light Mode URL: " + lightModeUrl);
            System.out.println("Dark Mode URL: " + darkModeUrl);

            if (scene.getStylesheets().contains(darkModeUrl)) {
                scene.getStylesheets().remove(darkModeUrl);
                scene.getStylesheets().add(lightModeUrl);
            } else {
                scene.getStylesheets().remove(lightModeUrl);
                scene.getStylesheets().add(darkModeUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to toggle theme.");
        }
    }
    @FXML
    public void deletionfunctionality(ActionEvent event) {
        if (currentSelectedImageView != null) {
            String imagePath = null;
            try {
                imagePath = new File(new URL(currentSelectedImageView.getImage().getUrl()).toURI()).getAbsolutePath();
            } catch (Exception e) {
                showAlert("Error", "Could not retrieve file path from the image.");
                e.printStackTrace();
            }

            uploadedImages.getChildren().remove(currentSelectedImageView);
            if (imagePath != null && uploadedFilePaths.contains(imagePath)) {
                uploadedFilePaths.remove(imagePath);
            }
            if (selectedImageView.getImage() == currentSelectedImageView.getImage()) {
                clearAnnotations(event);
                selectedImageView.setImage(null);
            }
            currentSelectedImageView = null;
        } else {
            showAlert("No Selection", "No image is currently selected to delete.");
        }
    }
}
