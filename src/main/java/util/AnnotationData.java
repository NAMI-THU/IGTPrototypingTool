package util;

import java.util.ArrayList;

public class AnnotationData {

    /**
     * Private Class to save the Annotation Data
     */
    private static class Annotation{
        private double middlePointX;
        private double middlePointY;
        private double boundingBoxWidth;
        private double boundingBoxHeight;
        private String imgName;

        public Annotation(double middlePointX, double middlePointY, double boundingBoxWidth, double boundingBoxHeight, String imgName) {
            this.middlePointX = middlePointX;
            this.middlePointY = middlePointY;
            this.boundingBoxWidth = boundingBoxWidth;
            this.boundingBoxHeight = boundingBoxHeight;
            this.imgName = imgName;
        }
        public double getMiddlePointX() {return middlePointX;}
        public double getMiddlePointY() {return middlePointY;}
        public double getBoundingBoxWidth() {return boundingBoxWidth;}
        public double getBoundingBoxHeight() {return boundingBoxHeight;}
    }

    private static AnnotationData instance = new AnnotationData();

    private ArrayList<Annotation> annotations;
    private AnnotationData(){
        annotations = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation){}

    public static AnnotationData getInstance() {
        return instance;
    }
}
