package controller;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import algorithm.ImageDataProcessor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import userinterface.PlottableImage;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AiControllerOnnx {
    @FXML
    public ChoiceBox sourceChoiceBox;

    @FXML
    private PlottableImage videoImagePlot;
    private int cameraSourceIndex;
    private VideoCapture capture;
    private YOLOv8Model yoloModel;
    private OrtEnvironment env;


    public void initialize() {
        sourceChoiceBox.setValue("Select Source");
        // Set up a listener for the ChoiceBox
        sourceChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            handleCameraSourceChange(newValue.intValue()); // Pass the index directly
        });

        videoImagePlot.setLegendVisible(true);
        OpenCV.loadLocally();
        //capture = new VideoCapture(0);  // Open default camera (index 0)

        /*if (!capture.isOpened()) {
            System.out.println("Error: Could not open video capture.");
            return;
        }*/

        // Initialize ONNX Runtime environment and YOLOv8 model
        try {
            env = OrtEnvironment.getEnvironment();
            yoloModel = new YOLOv8Model("Python/best.onnx");
        } catch (OrtException e) {
            e.printStackTrace();
        }

        // Start a background task for the video stream processing
        //startVideoStream();
    }

    // Method to handle camera source changes
    private void handleCameraSourceChange(int newSourceIndex) {
        // Stop the current video stream if already running
        if (capture != null && capture.isOpened()) {
            stopCam();
        }

        // Update the camera source index based on the selection
        cameraSourceIndex = newSourceIndex;

        // Try to open the selected camera
        capture = new VideoCapture(cameraSourceIndex);
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open camera source.");
            return; // Exit if the camera source fails to open
        }

        // Start the video stream with the new camera source
        startVideoStream();
    }

    // Method to start the video stream in a background thread
    private void startVideoStream() {
        Task<Void> videoTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Mat frame = new Mat();
                while (!isCancelled()) {
                    if (capture.read(frame)) {
                        try {
                            // Pre-process the frame for YOLOv8
                            OnnxTensor inputTensor = preprocessFrame(frame, env);

                            // Run the YOLOv8 model on the frame
                            OrtSession.Result result = yoloModel.runOnnxModel(inputTensor);

                            // Post-process the output (apply NMS and IoU)
                            postProcess(result, frame);

                            // Convert the frame to a format that PlottableImage can use
                            Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2RGB);  // Convert to RGB
                            Image imageToShow = matToImage(frame);


                            // Update the PlottableImage with the new frame
                            Platform.runLater(() -> videoImagePlot.setImage(imageToShow));

                            inputTensor.close();
                            result.close();
                        } catch (OrtException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }
        };

        Thread videoThread = new Thread(videoTask);
        videoThread.setDaemon(true);  // This ensures the thread closes when the application closes
        videoThread.start();
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

    // Post-process the model output (apply NMS and IoU filtering)
    // Post-process the model output (detect one object at a time)
    private void postProcess(OrtSession.Result result, Mat frame) {
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
            if (outputTr[4][i] < 0.25) {
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


    // Utility method to convert OpenCV Mat to JavaFX Image
    private Image matToImage(Mat frame) {
        try {
            return ImageDataProcessor.Mat2Image(frame, ".png");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stopCam() {
        // Close the video capture if it's open
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
    }
    // Stop the video stream when exiting
    public void stopVideo() {
        // Close the video capture if it's open
        if (capture != null && capture.isOpened()) {
            capture.release();
        }

        // Close the YOLO model if it is initialized and not already closed
        if (yoloModel != null) {
            try {
                yoloModel.close();
            } catch (OrtException e) {
                // Handle exception as needed, for example log the error
                e.printStackTrace();
            }
        }
    }




    public void setStatusLabel(Label status) {
        // Set the status label if needed
    }

    public void close() {
        stopVideo();
    }
}
