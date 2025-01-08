package controller;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import algorithm.ImageDataProcessor;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AiControllerOnnx {
    @FXML
    private Button clearAllPoints;

    @FXML
    private Label distanceLabel;
    @FXML
    private Label instructionsLabel;
    @FXML
    private Label navigationStatus;

    @FXML
    private CheckBox enablePathMode;

    @FXML
    private ImageView videoImagePlot = new ImageView();
    private int cameraSourceIndex;
    private YOLOv8Model yoloModel;
    private OrtEnvironment env;
    private Image placeholderImage = new Image("THU_Nami.jpg",640, 480, false, true);

    private Point detectionPoint;
    private List<Point> pathPoints = new ArrayList<>(); // List to hold the path points

    private double totalDistance;

    @FXML
    TextField widthInput;
    @FXML
    TextField heightInput;
    @FXML
    Button resizeButton;

    public void initialize() {

        OpenCV.loadLocally();
        clearAllPoints.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                pathPoints.clear();
                totalDistance = 0.0;
                distanceLabel.setText("Distance: 0.0");
                navigationStatus.setText("Status: navigating...");
                instructionsLabel.setText("Insructions: Unknown direction");
            }
        });
        videoImagePlot.setImage(placeholderImage);
        resizeButton.setOnAction(event -> {
            String widthText = widthInput.getText();
            String heightText = heightInput.getText();

            if (widthText != null && !widthText.isEmpty() && heightText != null && !heightText.isEmpty()) {
                double width = Double.parseDouble(widthText);
                double height = Double.parseDouble(heightText);
                updateResolution(width, height);

            } else {
                System.out.println("Width and height must not be empty.");
            }
        });



        // Set up mouse click event on the videoImagePlot
        videoImagePlot.setOnMouseClicked(this::handleVideoImageClick);

        // Initialize ONNX Runtime environment and YOLOv8 model
        try {
            env = OrtEnvironment.getEnvironment();
            yoloModel = new YOLOv8Model("Python/best.onnx");
        } catch (OrtException e) {
            e.printStackTrace();
        }
    }

    // The method processFrame is called from videoController to update the videoimageplot of the current instance of this class
    public void processFrame(Mat frame) {
        // Task to handle AI processing and UI updates asynchronously
        Task<Void> videoTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Step 1: Convert the frame to grayscale if needed (optional)
                    Mat grayscaleFrame = new Mat();
                    Imgproc.cvtColor(frame, grayscaleFrame, Imgproc.COLOR_BGR2GRAY);

                    // Step 2: Keep the grayscale frame separate for the AI processing part
                    Mat processedFrame = new Mat();
                    // Only convert back to BGR for overlay or AI processing if necessary
                    Imgproc.cvtColor(grayscaleFrame, processedFrame, Imgproc.COLOR_GRAY2BGR);

                    // Step 3: Pre-process the frame for YOLOv8 model (use processed frame here)
                    OnnxTensor inputTensor = preprocessFrame(processedFrame, env);

                    // Step 4: Run YOLOv8 model on the frame, with a session check
                    if (yoloModel.isSessionOpen()) {  // Check if the session is open before running the model
                        OrtSession.Result result = yoloModel.runOnnxModel(inputTensor);

                        // Step 5: Post-process the result (apply NMS, IoU, etc.)
                        postProcess(result, processedFrame);

                        // Step 6: Convert the processed frame to RGB for JavaFX ImageView
                        Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_BGR2RGB);

                        // Step 7: Convert the processed Mat frame to Image for JavaFX
                        Image imageToShow = matToImage(processedFrame);

                        // Step 8: Update the JavaFX ImageView on the JavaFX Application Thread
                        Platform.runLater(() -> videoImagePlot.setImage(imageToShow));

                        // Clean up resources
                        inputTensor.close();
                        result.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        // Start the task in a background thread to avoid blocking the UI
        new Thread(videoTask).start();
    }


    // the method updateResolution is called from videoController to update the resolution of the videoImagePlot
    public void updateResolution(double width, double height) {
        videoImagePlot.setFitWidth(width);
        videoImagePlot.setFitHeight(height);
    }


    // Pre-process the frame for YOLOv8 (resize, normalize, etc.)
    private OnnxTensor preprocessFrame(Mat frame, OrtEnvironment env) throws OrtException {
        // Step 1: Resize the image to 640x640
        Mat resizedImage = new Mat();
        Imgproc.resize(frame, resizedImage, new Size(640, 640));

        // Step 2: Convert the image to a float array (normalized between 0 and 1)
        int width = resizedImage.width();
        int height = resizedImage.height();
        int channels = resizedImage.channels();  // RGB -> 3 channels

        float[][][] imageData = new float[channels][height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] pixel = resizedImage.get(y, x);  // Get pixel values (BGR format in OpenCV)
                imageData[0][y][x] = (float) (pixel[2] / 255.0);  // R channel
                imageData[1][y][x] = (float) (pixel[1] / 255.0);  // G channel
                imageData[2][y][x] = (float) (pixel[0] / 255.0);  // B channel
            }
        }

        long[] inputShape = {1, 3, 640, 640};

        FloatBuffer floatBuffer = FloatBuffer.allocate(1 * 3 * 640 * 640);
        for (int c = 0; c < 3; c++) {
            for (int y = 0; y < 640; y++) {
                for (int x = 0; x < 640; x++) {
                    floatBuffer.put(imageData[c][y][x]);
                }
            }
        }
        floatBuffer.rewind();

        return OnnxTensor.createTensor(env, floatBuffer, inputShape);
    }


    // Post-process the model output (detect one object at a time)
    private void postProcess(OrtSession.Result result, Mat frame) {
        // Draw path from detection point to first point, then between other points
        if (!pathPoints.isEmpty()) {
            drawPath(frame);
        }

        float frameWidth = frame.width();
        float frameHeight = frame.height();
        float widthScaleFactor= frameWidth/640.0f;
        float heightScaleFactor= frameHeight/640.0f;

        if (result.size() == 0) {
            System.out.println("No outputs from the model.");
            return;
        }

        OnnxTensor outputTensor = (OnnxTensor) result.get(0);
        float[][][] output;
        try {
            output = (float[][][]) outputTensor.getValue();
        } catch (OrtException e) {
            throw new RuntimeException(e);
        }

        if (output == null || output.length == 0) {
            System.out.println("Invalid output data.");
            return;
        }

        int numDetections = 8400; // Number of detections based on YOLO output size
        float[][] outputTr = output[0]; // The output tensor contains the detection information

        List<Detection> detections = new ArrayList<>();
        for (int i = 0; i < numDetections; i++) {
            if (outputTr[4][i] < 0.3) {
                continue;  // Filter out low-confidence detections
            }

            // Extract bounding box and detection information
            float xCenter = outputTr[0][i];
            float yCenter = outputTr[1][i];
            float width = outputTr[2][i];
            float height = outputTr[3][i];
            float confidence = outputTr[4][i];

            // Convert center coordinates to top-left corner for OpenCV drawing
            float x =  (xCenter - (width / 2)) * widthScaleFactor;
            float y =  (yCenter - (height / 2)) * heightScaleFactor;

            detections.add(new Detection(x, y, width, height, confidence));

        }

        // If there are no detections, skip further processing
        if (detections.isEmpty()) {
            return;
        }

        // Sort detections by confidence, highest confidence first == applying nms
        detections.sort(Comparator.comparingDouble(d -> -d.confidence));

        // Pick the detection with the highest confidence
        Detection bestDetection = detections.get(0);


        // Set the detectionPoint to the center of the bounding box
        detectionPoint = new Point(bestDetection.x + bestDetection.width / 2,
                bestDetection.y + bestDetection.height / 2);
        if (!pathPoints.isEmpty()){
        String instruction = getDirection(detectionPoint, pathPoints.get(pathPoints.size()-1));

        drawLine(detectionPoint,pathPoints.get(0),frame);
        totalDistance = calculateDistance();
        checkCollision();
        if(!detectCollision(detectionPoint, pathPoints.get(pathPoints.size()-1))){
                Platform.runLater(()->navigationStatus.setText("Status: navigating..."));
        }
        Platform.runLater(() ->distanceLabel.setText(String.format("Distance: %.2f", totalDistance)));
        Platform.runLater(() ->instructionsLabel.setText("Instructions: " + instruction));
        }

        // Draw the center point of the detection box with a red outline
        int radius = 6;
        int thickness = 2;

        // Draw the outer circle
        Imgproc.circle(frame, detectionPoint, radius, new Scalar(255, 0, 0), thickness);

        // Draw the inner circle
        Imgproc.circle(frame, detectionPoint, radius - thickness, new Scalar(255, 255, 255), -15); // White fill

        // Draw bounding box for the highest confidence detection only
        Imgproc.rectangle(frame,
                new Point(bestDetection.x, bestDetection.y),
                new Point(bestDetection.x + bestDetection.width, bestDetection.y + bestDetection.height),
                new Scalar(255, 0, 255), 2); // Draw green bounding box

        // Draw label for the detection
        Imgproc.putText(frame, "tip " + String.format("%.2f", bestDetection.confidence),
                new Point(bestDetection.x, bestDetection.y - 5),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.75,
                new Scalar(255, 255, 255), 2); // White label

    }


    private void handleVideoImageClick(javafx.scene.input.MouseEvent event) {
        if (enablePathMode.isSelected()) {
            // Get the image currently displayed in the ImageView
            Image image = videoImagePlot.getImage();
            if (image == null) {
                System.out.println("No image loaded.");
                return;
            }

            // Image dimensions (actual resolution)
            double imageWidth = image.getWidth();
            double imageHeight = image.getHeight();

            // ImageView dimensions
            double viewWidth = videoImagePlot.getBoundsInLocal().getWidth();
            double viewHeight = videoImagePlot.getBoundsInLocal().getHeight();


            // Calculate aspect ratios
            double imageAspectRatio = imageWidth / imageHeight;
            double viewAspectRatio = viewWidth / viewHeight;

            // Calculate displayed image dimensions within the ImageView
            double displayedImageWidth, displayedImageHeight;
            double offsetX = 0, offsetY = 0;

            if (viewAspectRatio > imageAspectRatio) {
                // Image is constrained by height; black bars on the sides
                displayedImageHeight = viewHeight;
                displayedImageWidth = imageAspectRatio * displayedImageHeight;
                offsetX = (viewWidth - displayedImageWidth) / 2; // Horizontal padding
            } else {
                // Image is constrained by width; black bars on the top/bottom
                displayedImageWidth = viewWidth;
                displayedImageHeight = displayedImageWidth / imageAspectRatio;
                offsetY = (viewHeight - displayedImageHeight) / 2; // Vertical padding
            }


            // Map mouse click coordinates to the image coordinates
            double clickX = event.getX();
            double clickY = event.getY();

            // Check if click is within the displayed image area
            if (clickX < offsetX || clickX > offsetX + displayedImageWidth ||
                    clickY < offsetY || clickY > offsetY + displayedImageHeight) {
                return;
            }

            // Adjust click coordinates to image space
            double adjustedX = (clickX - offsetX) * (imageWidth / displayedImageWidth);
            double adjustedY = (clickY - offsetY) * (imageHeight / displayedImageHeight);


            // Ensure adjusted coordinates are within the image bounds
            if (adjustedX < 0 || adjustedX > imageWidth || adjustedY < 0 || adjustedY > imageHeight) {
                return;
            }

            // Add the adjusted point to the path
            Point newPoint = new Point(adjustedX, adjustedY);
            pathPoints.add(newPoint);

        }
    }


    // Method to draw the entire path (all points and lines)
    private void drawPath(Mat frame) {
        // Iterate through the list of points and draw them
        for (int i = 0; i < pathPoints.size(); i++) {
            Point point = pathPoints.get(i);
            int radius;
            int thickness;

            // Check if it's the last point in the list
            if (i == pathPoints.size() - 1) {
                // Make the last point bigger with an outline
                radius = 7;
                thickness = 3;
                // Draw the outer circle (red outline)
                Imgproc.circle(frame, point, radius, new Scalar(255, 0, 0), thickness);
                // Draw the inner circle (white fill)
                Imgproc.circle(frame, point, radius - thickness, new Scalar(255, 255, 255), -2); // White fill
            } else {
                // Regular points
                radius = 5; // Regular size for other points
                thickness = -1; // Filled circle
                Imgproc.circle(frame, point, radius, new Scalar(0, 0, 255), thickness);
            }

            // Draw line to the previous point if this is not the first point
            if (i > 0) {
                Point prevPoint = pathPoints.get(i - 1);
                Imgproc.line(frame, prevPoint, point, new Scalar(255, 0, 0), 2);
            }
        }
    }

    private double calculateDistance() {
        totalDistance = 0.0;
        // Check if there are enough points to calculate distance
        if (pathPoints.isEmpty()) {
            return totalDistance;
        }

        // Check if the detection point is not null
        if (detectionPoint == null) {
            return totalDistance;
        }

        // Start from the detection point
        Point previousPoint = detectionPoint;

        // Iterate through the path points
        for (Point currentPoint : pathPoints) {
            totalDistance += calculateEuclideanDistance(previousPoint, currentPoint);
            previousPoint = currentPoint; // Move to the next point
        }

        // Also add distance from the last path point back to the first path point
        if (!pathPoints.isEmpty()) {
            totalDistance += calculateEuclideanDistance(previousPoint, pathPoints.get(0));
        }

        return totalDistance;
    }

    // Helper method to calculate Euclidean distance between two points
    private double calculateEuclideanDistance(Point pt1, Point pt2) {
        double dx = pt2.x - pt1.x;
        double dy = pt2.y - pt1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


    private boolean detectCollision(Point pt1, Point pt2) {
        double x1 = pt1.x;
        double y1 = pt1.y;
        double x2 = pt2.x;
        double y2 = pt2.y;

        return ((x1 >= x2 - 10) && (x1 <= x2 + 10)) && ((y1 >= y2 - 10) && (y1 <= y2 + 10));
    }

    public void checkCollision() {
        for (int i = 0; i < pathPoints.size(); i++) {
            Point point = pathPoints.get(i);
            if (detectCollision(point, detectionPoint)) {
                if (i == pathPoints.size() - 1) {
                    // Collision with the thrombus
                    Platform.runLater(()->navigationStatus.setText("Status: Thrombus reached!"));
                } else {
                    Platform.runLater(()->navigationStatus.setText("Status: navigating"));
                    // Collision with a point other than the last
                    pathPoints.subList(0, i + 1).clear(); // Remove points from index 0 to i
                }
                break;
            }
        }
    }

    public String getDirection(Point pt1, Point pt2) {
        double x1 = pt1.x;
        double x2 = pt2.x;
        double y1 = pt1.y;
        double y2 = pt2.y;
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (detectCollision(detectionPoint, pathPoints.get(pathPoints.size()-1))) {
            return "Already at the target";
        }

        if (dx > 0 && dy == 0) {
            return "Move Right";
        } else if (dx < 0 && dy == 0) {
            return "Move Left";
        } else if (dx == 0 && dy > 0) {
            return "Move Down";
        } else if (dx == 0 && dy < 0) {
            return "Move Up";
        } else if (dx > 0 && dy > 0) {
            return "Move Down-Right";
        } else if (dx > 0 && dy < 0) {
            return "Move Up-Right";
        } else if (dx < 0 && dy > 0) {
            return "Move Down-Left";
        } else if (dx < 0 && dy < 0) {
            return "Move Up-Left";
        }

        return "Unknown direction";
    }


    private void drawLine(Point start, Point end, Mat frame) {
        Imgproc.line(frame, start, end, new Scalar(255, 0, 0), 2);
    }


    // Borrowed utility method to convert OpenCV Mat to JavaFX Image
    private Image matToImage(Mat frame) {
        try {
            return ImageDataProcessor.Mat2Image(frame, ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setStatusLabel(Label status) {
    }


    public void close() {

    }
}
