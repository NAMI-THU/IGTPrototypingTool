package userinterface;

import javafx.beans.NamedArg;
import javafx.collections.ListChangeListener;
import javafx.scene.Cursor;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;

import java.util.ArrayList;
import java.util.List;

public class PlottableImage extends ScatterChart<Number, Number> {
    private final ImageView imageView = new ImageView();
    private final List<PlottableImageCallback> clickHandler = new ArrayList<>();

    private double panel_x_extra = 0; //57;
    private double panel_y_extra = 0; //54;
    private boolean panelAlreadyAdapted = false;

    public PlottableImage(@NamedArg("xAxis") NumberAxis xAxis, @NamedArg("yAxis") NumberAxis yAxis) {
        super(xAxis, yAxis);
        getPlotChildren().add(imageView);
        setAnimated(false);

        imageView.setPreserveRatio(false);
        imageView.setOnMouseClicked(mouseEvent -> clickHandler.forEach(handler -> handler.onMouseClicked(xAxis.getValueForDisplay(mouseEvent.getX()).doubleValue(), yAxis.getValueForDisplay(mouseEvent.getY()).doubleValue())));
        imageView.setOnMouseEntered(mouseEvent -> getScene().setCursor(Cursor.CROSSHAIR));
        imageView.setOnMouseExited(mouseEvent -> getScene().setCursor(Cursor.DEFAULT));
    }

    public void setImage(Image image) {
        if(imageView.getImage() != null && !panelAlreadyAdapted){
            // This extracts the size of the plot background. We use this to calculate how big the PlottableImage element should be so that the image is completely visible
            // This is indeed quite hacky. However, this is the only way besides hardcoding the offsets.
            var bounds = getChartChildren().get(0).getLayoutBounds();
            panel_x_extra = image.getWidth()-bounds.getWidth();
            panel_y_extra = image.getHeight()-bounds.getHeight();

            // Only adapt the sizes once
            panelAlreadyAdapted = true;
        }

        imageView.setImage(image);

        ((NumberAxis) getXAxis()).setUpperBound(image.getWidth());
        ((NumberAxis) getXAxis()).setLowerBound(0);
        ((NumberAxis) getYAxis()).setUpperBound(image.getHeight());
        ((NumberAxis) getYAxis()).setLowerBound(0);

        imageView.minWidth(image.getWidth());
        imageView.maxWidth(image.getWidth());
        imageView.minHeight(image.getHeight());
        imageView.maxHeight(image.getHeight());

        // The scatter plot has some overhead (padding, axis and so on) we need to compensate so that our image is of true size.
        maxWidthProperty().bind(image.widthProperty().add(panel_x_extra));
        maxHeightProperty().bind(image.heightProperty().add(panel_y_extra));
        minWidthProperty().bind(image.widthProperty().add(panel_x_extra));
        minHeightProperty().bind(image.heightProperty().add(panel_y_extra));

        updateAxisRange();
    }

    public void initSensorCurve(XYChart.Series<Number, Number> dataSeries){
        var sensorCurve = new CubicCurve();
        sensorCurve.setStroke(Color.BLUEVIOLET);
        sensorCurve.setStrokeWidth(4);
        sensorCurve.setStrokeLineCap(StrokeLineCap.ROUND);
        getPlotChildren().add(sensorCurve);
        dataSeries.getData().addListener((ListChangeListener<? super Data<Number, Number>>) c -> {
            var points = c.getList();

            if(points.size() < 4){
                // Don't do anything, and we do not want to throw an exception as this would cause cold-start problems
                return;
            }


            sensorCurve.setStartX(points.get(0).getXValue().doubleValue());
            sensorCurve.setStartY(points.get(0).getYValue().doubleValue());

            sensorCurve.setControlX1(points.get(1).getXValue().doubleValue());
            sensorCurve.setControlY1(points.get(1).getYValue().doubleValue());

            sensorCurve.setControlX2(points.get(2).getXValue().doubleValue());
            sensorCurve.setControlY2(points.get(2).getYValue().doubleValue());

            sensorCurve.setEndX(points.get(3).getXValue().doubleValue());
            sensorCurve.setEndY(points.get(3).getYValue().doubleValue());

        });
    }

    public void registerImageClickedHandler(PlottableImageCallback handler){
        clickHandler.add(handler);
    }
}
