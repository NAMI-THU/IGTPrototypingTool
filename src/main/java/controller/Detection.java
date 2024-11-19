package controller;

public class Detection {
    public float x;        // Top-left corner x-coordinate
    public float y;        // Top-left corner y-coordinate
    public float width;    // Width of the bounding box
    public float height;   // Height of the bounding box
    public float confidence;  // Confidence score for the detection

    // Constructor
    public Detection(float x, float y, float width, float height, float confidence) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.confidence = confidence;
    }
}

