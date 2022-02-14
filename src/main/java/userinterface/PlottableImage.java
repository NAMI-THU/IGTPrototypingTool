package userinterface;

import javafx.beans.NamedArg;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlottableImage extends ScatterChart<Number, Number> {

    private final ImageView imageView = new ImageView();

    public PlottableImage(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        getPlotChildren().add(imageView);
    }

    public void setImage(Image image) {
        imageView.setImage(image);

        ((NumberAxis) getXAxis()).setUpperBound(image.getWidth()/2);
        ((NumberAxis) getXAxis()).setLowerBound(-image.getWidth()/2);
        ((NumberAxis) getYAxis()).setUpperBound(image.getHeight()/2);
        ((NumberAxis) getYAxis()).setLowerBound(-image.getHeight()/2);

        // TODO: This might conflict with the Transformation Matrix
        prefWidthProperty().bind(image.widthProperty());
        prefHeightProperty().bind(image.heightProperty());

        maxWidthProperty().bind(image.widthProperty());
        maxHeightProperty().bind(image.heightProperty());
    }
}
