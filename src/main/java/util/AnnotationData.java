package util;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

/**
 * Annotation Data handler responsible for collecting and saving the annotated data
 * Singleton because we only want one instance of this, at a time.
 *
 */
public class AnnotationData {

    /**
     * Private Class to save the Annotation Data
     */
    private static class Annotation{
        private final double middlePointX;
        private final double middlePointY;
        private final double boundingBoxWidth;
        private final double boundingBoxHeight;

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

    private static final AnnotationData instance = new AnnotationData();

    private final Map<String, Annotation> annotations;
    private AnnotationData(){
        annotations = new HashMap<>();
    }

    public void addAnnotation(String path,
                              double middlePointX,
                              double middlePointY,
                              double boundingBoxWidth,
                              double boundingBoxHeight){
        if(!annotations.containsKey(path)){
            annotations.put(path, new Annotation(middlePointX, middlePointY, boundingBoxWidth, boundingBoxHeight));
        }else{
            annotations.replace(path, new Annotation(middlePointX, middlePointY, boundingBoxWidth, boundingBoxHeight));
        }
    }

    public Rectangle getAnnotation(String path){
        if(annotations.containsKey(path)){
            Annotation annotation = annotations.get(path);
            // Calculate the Rectangle based on the annotated data
            Rectangle rectangle = new Rectangle();
            rectangle.setWidth(annotation.getBoundingBoxWidth());
            rectangle.setHeight(annotation.getBoundingBoxHeight());
            rectangle.setX(annotation.getMiddlePointX()-(annotation.getBoundingBoxWidth()/2));
            rectangle.setY(annotation.getMiddlePointY()-(annotation.getBoundingBoxHeight()/2));
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.rgb(6, 207, 236));
            rectangle.setStrokeWidth(2);
            rectangle.setVisible(true);
          return rectangle;
        }
        return null;
    }

    /**
     * Function to delete the Annotation of one Image
     * @param path Image path (Key for the Map)
     */
    public void deleteAnnotation(String path){
        if(annotations.containsKey(path)){
            annotations.remove(path);
        }
    }

    public static AnnotationData getInstance() {
        return instance;
    }
}
