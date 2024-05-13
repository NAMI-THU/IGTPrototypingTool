package controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
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
    private final Set<String> uploadedFilePaths = new HashSet<>();
    // store the paths of the selected Image, so you can Export the data based on these keys
    private final Set<String> selectedFilePaths = new HashSet<>();
    private int currentImageIndex = 0; // Default to the first image
    private final List<Image> imageList = new ArrayList<>(); // Store all loaded images
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing Controller");
        if (annotationPane != null) {
            setupAnnotationHandlers();
        }

        uploadedImages.setFocusTraversable(true);
        uploadedImages.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT:
                    selectNextImage();
                    break;
                case LEFT:
                    selectPreviousImage();
                    break;
                default:
                    break;
            }
        });
    }
    private void setupAnnotationHandlers() {
        annotationPane.setOnMousePressed(this::pressedAnnotationEvent);
        annotationPane.setOnMouseDragged(this::dragAnnotationEvent);
        annotationPane.setOnMouseReleased(this::releasedAnnotationEvent);
    }
    public void selectNextImage() {
        // Check if there's a next image in the list
        if (currentImageIndex < imageList.size() - 1) {
            currentImageIndex++;
            Image nextImage = imageList.get(currentImageIndex);
            ImageView nextImageView = findImageViewForImage(nextImage);
            selectImage(nextImage, nextImageView);
        } else {
            System.out.println("No next image available.");
        }
    }

    public void selectPreviousImage() {
        // Check if there's a previous image in the list
        if (currentImageIndex > 0) {
            currentImageIndex--;
            Image prevImage = imageList.get(currentImageIndex);
            ImageView prevImageView = findImageViewForImage(prevImage);
            selectImage(prevImage, prevImageView);
        } else {
            System.out.println("No previous image available.");
        }
    }

    private ImageView findImageViewForImage(Image image) {
        for (Node node : uploadedImages.getChildren()) {
            if (node instanceof HBox) {
                ImageView imageView = (ImageView) ((HBox) node).getChildren().get(0);
                if (imageView.getImage().equals(image)) {
                    return imageView;
                }
            }
        }
        return null; // Not found
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

            List<File> selectedImages = fileChooser.showOpenMultipleDialog(currentStage);

            if (selectedImages != null) {
                for (File file : selectedImages) {
                    if (!uploadedFilePaths.contains(file.getAbsolutePath())) {
                        displayImage(file);
                        uploadedFilePaths.add(file.getAbsolutePath());
                    } else {
                        showAlert("Duplicate File", "The file " + file.getName() + " has already been uploaded.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error while choosing File: " + e.getMessage());
        }
    }
    private void displayImage(File file) {
        HBox hbox = new HBox();
        hbox.setSpacing(10);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(false);

        HBox.setMargin(checkBox, new Insets(10, 30, 10, 10));

        Image image = new Image(file.toURI().toString());
        imageList.add(image); // Adding the image to the list
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(event -> {
            selectImage(image, imageView);
        });
        // Add or Remove the selected file paths to the HasSet
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Checkbox is now selected
                selectedFilePaths.add(imageView.getImage().getUrl());
            } else { // Checkbox is now unselected
                selectedFilePaths.remove(imageView.getImage().getUrl());
            }
        });

        hbox.getChildren().add(imageView);
        hbox.getChildren().add(checkBox);
        uploadedImages.getChildren().add(hbox);
    }
    private void selectImage(Image image, ImageView imageView) {
        try {
            if (imageList.isEmpty()) {
                selectedImageView.setImage(null);
                System.out.println("No images to display.");
                return;
            }
            currentImageIndex = imageList.indexOf(image);
            if (currentImageIndex == -1) {
                System.out.println("Selected image is not in the image list.");
                return;
            }
            if (selectedImageView == null) {
                System.out.println("Error: selectedImageView is not initialized.");
                return;
            }
            selectedImageView.setImage(image);
            selectedImageView.setFitWidth(selectedImageView.getScene().getWidth());
            selectedImageView.setPreserveRatio(true);
            if (currentSelectedImageView != null && currentSelectedImageView != imageView) {
                currentSelectedImageView.setStyle("");
            }

            if (imageView != null) {
                currentSelectedImageView = imageView;
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, deepskyblue, 10, 0, 0, 0); -fx-border-color: blue; -fx-border-width: 2;");
            }
            scrollToSelectedImage();
            if (annotationPane != null) {
                annotationPane.getTransforms().clear();
                Scale scale = new Scale();
                annotationPane.getTransforms().add(scale);
                annotationPane.setOnScroll(event -> {
                    if (event.isControlDown()) {
                        double zoomFactor = 1.05;
                        scale.setPivotX(event.getX());
                        scale.setPivotY(event.getY());

                        if (event.getDeltaY() > 0) {
                            scale.setX(scale.getX() * zoomFactor);
                            scale.setY(scale.getY() * zoomFactor);
                        } else if (scale.getX() > 1.0 && scale.getY() > 1.0) {
                            scale.setX(scale.getX() / zoomFactor);
                            scale.setY(scale.getY() / zoomFactor);
                        }
                        event.consume();
                    }
                });
            }
            checkForExistingAnnotationData();
        } catch (Exception e) {
            System.out.println("Exception in selectImage: " + e.getMessage());
        }
    }
    private void scrollToSelectedImage() {
        if (currentSelectedImageView != null && selectedImagePane != null) {
            double viewportHeight = selectedImagePane.getHeight();
            double imageY = currentSelectedImageView.localToScene(currentSelectedImageView.getBoundsInLocal()).getMinY();
            double offsetY = imageY - selectedImagePane.getScene().getY() - viewportHeight / 2 + currentSelectedImageView.getBoundsInLocal().getHeight() / 2;
            double vValue = offsetY / (uploadedImages.getHeight() - viewportHeight);
            selectedImagePane.setVvalue(Math.max(0, Math.min(vValue, 1)));  // Clamp vValue to be between 0 and 1
        }
    }
    public void clearAnnotations() {
        if (annotatedRectangle != null) {
            annotationPane.getChildren().remove(annotatedRectangle);
            annotationPane.getChildren().remove(middlePoint);
            AnnotationData.getInstance().deleteAnnotation(selectedImageView.getImage().getUrl());
            middlePoint = null;
            annotatedRectangle = null;
        }
    }
    private void checkForExistingAnnotationData() {
        // Clear existing annotations if any
        if (annotatedRectangle != null) {
            annotationPane.getChildren().remove(annotatedRectangle);
            annotatedRectangle = null;
        }
        if (middlePoint != null) {
            annotationPane.getChildren().remove(middlePoint);
            middlePoint = null;
        }

        // Reload annotation if it exists
        annotatedRectangle = AnnotationData.getInstance().getAnnotation(selectedImageView.getImage().getUrl());
        if (annotatedRectangle != null) {
            // Apply the transformations to the rectangle to match the image dimensions
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
    ///Export-Functionality---------- START
    @FXML
    private void handleExportAction(ActionEvent event) {
        if (uploadedImages.getChildren().isEmpty()) {
            showAlert("Export Error", "There are no images to export.");
            return;
        }
        Set<String> selectedImagesUrls = uploadedImages.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .filter(hbox -> ((CheckBox) hbox.getChildren().get(1)).isSelected())
                .map(hbox -> (ImageView) hbox.getChildren().get(0))
                .map(ImageView::getImage)
                .map(Image::getUrl)
                .collect(Collectors.toSet());

        if (selectedImagesUrls.isEmpty()) {
            showAlert("No Selection", "No images have been check-marked for export.");
            return;
        }
        Map<String, AnnotationData.PublicAnnotation> annotations = AnnotationData.getInstance().getAnnotations();
        Set<String> annotatedAndSelectedUrls = selectedImagesUrls.stream()
                .filter(annotations::containsKey)
                .collect(Collectors.toSet());
        List<String> unannotatedSelectedImages = selectedImagesUrls.stream()
                .filter(url -> !annotations.containsKey(url))
                .map(url -> new File(url).getName())
                .collect(Collectors.toList());
        if (!unannotatedSelectedImages.isEmpty()) {
            showUnannotatedImagesAlert(unannotatedSelectedImages);
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Annotations");
        File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedDirectory != null) {
            annotatedAndSelectedUrls.forEach(url -> {
                try {
                    File file = new File(new URL(url).toURI());
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".txt";
                    File annotationFile = new File(selectedDirectory, fileName);
                    saveAnnotationsToFile(annotationFile, annotations.get(url));
                } catch (Exception e) {
                    showAlert("Error", "Failed to save annotations for " + url);
                }
            });
        }
    }
    @FXML
    private void handleExportAllAction(ActionEvent event) {
        if (uploadedImages.getChildren().isEmpty()) {
            showAlert("Export Error", "There are no images to export.");
            return;
        }
        Set<String> displayedImagesUrls = uploadedImages.getChildren().stream()
                .filter(node -> node instanceof HBox)
                .map(node -> (HBox) node)
                .map(hbox -> (ImageView) hbox.getChildren().get(0))
                .map(ImageView::getImage)
                .map(Image::getUrl)
                .collect(Collectors.toSet());
        List<String> unannotatedImages = displayedImagesUrls.stream()
                .filter(url -> AnnotationData.getInstance().getAnnotation(url) == null)
                .map(url -> new File(url).getName())
                .collect(Collectors.toList());

        if (!unannotatedImages.isEmpty()) {
            showUnannotatedImagesAlert(unannotatedImages);
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save Annotations");
        File selectedDirectory = directoryChooser.showDialog(((Node) event.getSource()).getScene().getWindow());
        if (selectedDirectory != null) {
            AnnotationData.getInstance().getAnnotations().entrySet().stream()
                    .filter(entry -> displayedImagesUrls.contains(entry.getKey()))
                    .forEach(entry -> {
                        try {
                            String path = entry.getKey();
                            File file = new File(new URL(path).toURI());
                            String fileName = file.getName().substring(0, file.getName().lastIndexOf('.')) + ".txt";
                            File annotationFile = new File(selectedDirectory, fileName);
                            saveAnnotationsToFile(annotationFile, entry.getValue());
                        } catch (Exception e) {
                            showAlert("Error", "Failed to save annotations for " + entry.getKey());
                        }
                    });
        }
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
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///Export-Functionality----------END
    /**
     * Handles the Saving of Annotations to a File
     *
     * @param file       The File to save the Annotations to
     * @param annotation The Annotation Data to save
     */
    private void saveAnnotationsToFile(File file, AnnotationData.PublicAnnotation annotation) {
        try (PrintWriter writer = new PrintWriter(file)) {
            String line = String.format("%d %.5f %.5f %.5f %.5f",
                    0, annotation.getMiddlePointX(), annotation.getMiddlePointY(),
                    annotation.getBoundingBoxWidth(), annotation.getBoundingBoxHeight());
            writer.println(line);
        } catch (FileNotFoundException e) {
            System.err.println("Error while saving Annotation to File: " + e.getMessage());
        }
    }
    @FXML
    public void deletionfunctionality() {
        List<Node> toRemove = new ArrayList<>();
        List<Image> imagesToRemove = new ArrayList<>();
        boolean currentDisplayedRemoved = false;
        boolean atLeastOneSelected = false;
        for (Node node : uploadedImages.getChildren()) {
            if (node instanceof HBox hbox) {
                ImageView imageView = (ImageView) hbox.getChildren().get(0);
                CheckBox checkBox = (CheckBox) hbox.getChildren().get(1);
                if (checkBox.isSelected()) {
                    atLeastOneSelected = true;
                    String imagePath = null;
                    try {
                        imagePath = new File(new URL(imageView.getImage().getUrl()).toURI()).getAbsolutePath();
                        imagesToRemove.add(imageView.getImage());
                        toRemove.add(node);
                        uploadedFilePaths.remove(imagePath);
                        selectedFilePaths.remove(imagePath);
                        if (imageView.equals(currentSelectedImageView)) {
                            currentDisplayedRemoved = true;
                        }
                    } catch (Exception e) {
                        showAlert("Error", "Could not retrieve file path from the image: " + e.getMessage());
                        System.err.println("Error while retrieving filepath during deletion: " + e.getMessage());
                    }
                }
            }
        }
        if (atLeastOneSelected) {
            uploadedImages.getChildren().removeAll(toRemove);
            imageList.removeAll(imagesToRemove);
            if (currentDisplayedRemoved) {
                selectedImageView.setImage(null);
                currentSelectedImageView = null;
                currentImageIndex = Math.min(currentImageIndex, imageList.size() - 1);
            }
        } else {
            showAlert("Notice", "No images selected for deletion.");
        }
        // Notify if all images have been deleted
        if (uploadedImages.getChildren().isEmpty()) {
            showAlert("Notice", "All images have been deleted.");
        }
        clearAnnotations();
    }
}
