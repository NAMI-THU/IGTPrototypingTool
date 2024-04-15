package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnnotationData {

    /**
     * Private Class to save the Annotation Data
     */
    private static class Annotation{
        private double middlePointX;
        private double middlePointY;
        private double boundingBoxWidth;
        private double boundingBoxHeight;

        public Annotation(double middlePointX, double middlePointY, double boundingBoxWidth, double boundingBoxHeight) {
            this.middlePointX = middlePointX;
            this.middlePointY = middlePointY;
            this.boundingBoxWidth = boundingBoxWidth;
            this.boundingBoxHeight = boundingBoxHeight;
        }
        public double getMiddlePointX() {return middlePointX;}
        public double getMiddlePointY() {return middlePointY;}
        public double getBoundingBoxWidth() {return boundingBoxWidth;}
        public double getBoundingBoxHeight() {return boundingBoxHeight;}
    }

    private static AnnotationData instance = new AnnotationData();

    private Map<String, Annotation> annotations;
    private AnnotationData(){
        annotations = new HashMap();
    }

    public void addAnnotation(String path,
                              double middlePointX,
                              double middlePointY,
                              double boundingBoxWidth,
                              double boundingBoxHeight){

    }

    public static AnnotationData getInstance() {
        return instance;
    }
}
