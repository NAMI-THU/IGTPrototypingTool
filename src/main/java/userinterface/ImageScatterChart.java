package userinterface;

import javafx.beans.NamedArg;
import javafx.event.EventHandler;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/*
 * this class consists of scatter chart and image view, so data can be
 * displayed on top of an image.
 * the image position and size can be adjusted so the tracking data can
 * be displayed in relation to it.
 * do not override layoutplotchildren method, it is needed to display tracking data
 */
public class ImageScatterChart extends ScatterChart<Number, Number> {

    private ImageView iv;
//    @FXML NumberAxis xAxis, yAxis;
    private Double oldX = 0.0;
    private Double oldY = 0.0;

    // this works but not w fxml loader so far
    public ImageScatterChart(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
    }

    public ImageView getIv() {
        return iv;
    }

    /**
     * sets imageview and adds listeners so size and position can be changed
     * @param iv imageview of image to be displayed in the chart
     */
    public void setIv(ImageView iv) {
        // remove old image
        if (this.iv != null) this.iv.setImage(null);

        this.iv = iv;
        getPlotChildren().add(iv);
        if(iv.getImage() != null) {
            iv.setOnMousePressed(grabImage());
            iv.setOnMouseDragged(dragImage());
            // bind y to x scale, so image stays proportional
            iv.scaleYProperty().bind(iv.scaleXProperty());
            setOnMouseEntered(e -> {setOnScroll(zoom());});
        }
    }

    /**
     * sets old x and y values for reference when mouse is pressed
     * @return mouseevent on mouse pressed
     */
    private EventHandler<MouseEvent> grabImage() {
        return event -> {
            oldX = event.getScreenX();
            oldY = event.getScreenY();
        };
    }

    /**
     * moves imageview according to mouse movement
     * @return mouseevent on mouse dragged
     */
    private EventHandler<MouseEvent> dragImage() {
        return event -> {
            double deltaX = event.getScreenX() - oldX;
            double deltaY = event.getScreenY() - oldY;
            iv.setX(iv.getX() + deltaX);
            iv.setY(iv.getY() + deltaY);
            oldX = event.getScreenX();
            oldY = event.getScreenY();
        };
    }

    /**
     * increases scale of imageview on scroll up
     * decreases scale of imageview on scroll down
     * @return scrollevent
     */
    private EventHandler<ScrollEvent> zoom() {
        return event -> {
            double delta = iv.getScaleX();
            // move mouse wheel up: zoom in
            if(event.getDeltaY() > 0) {
                iv.setScaleX(delta + 0.01);
            // move mouse wheel down: zoom out
            } else if(event.getDeltaY() < 0) {
                iv.setScaleX(delta - 0.01);
            }
        };
    }
}
