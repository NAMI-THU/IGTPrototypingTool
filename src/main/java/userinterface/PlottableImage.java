package userinterface;

import javafx.beans.NamedArg;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PlottableImage extends ScatterChart<Number, Number> {

    private final ImageView imageView = new ImageView();
    private final List<PlottableImageCallback> clickHandler = new ArrayList<>();

    public PlottableImage(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        getPlotChildren().add(imageView);
        setAnimated(false);

        imageView.setPreserveRatio(false);
        imageView.setOnMouseClicked(mouseEvent -> clickHandler.forEach(handler -> handler.onMouseClicked(xAxis.getValueForDisplay(mouseEvent.getX()).doubleValue(), yAxis.getValueForDisplay(mouseEvent.getY()).doubleValue())));
    }

    public void setImage(Image image) {
        imageView.setImage(image);

        ((NumberAxis) getXAxis()).setUpperBound(image.getWidth());
        ((NumberAxis) getXAxis()).setLowerBound(0);
        ((NumberAxis) getYAxis()).setUpperBound(image.getHeight());
        ((NumberAxis) getYAxis()).setLowerBound(0);

        imageView.minWidth(image.getWidth());
        imageView.maxWidth(image.getWidth());
        imageView.minHeight(image.getHeight());
        imageView.maxHeight(image.getHeight());

        maxWidthProperty().bind(image.widthProperty());
        maxHeightProperty().bind(image.heightProperty());
        minWidthProperty().bind(image.widthProperty());
        minHeightProperty().bind(image.heightProperty());

        updateAxisRange();
    }

    public void registerImageClickedHandler(PlottableImageCallback handler){
        clickHandler.add(handler);
    }
}
