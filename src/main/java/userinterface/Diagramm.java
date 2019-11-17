package userinterface;

import algorithm.DataService;
import algorithm.Measurement;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.OpenIGTLinkConnection;
import inputOutput.TrackingDataSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class Diagramm extends Application {

    /*
     * Create new axes and fix the scale for the axes: plotting a range of
     * numbers.
     */
    final NumberAxis xAxis = new NumberAxis(-500, 500, 100);
    final NumberAxis yAxis = new NumberAxis(-500, 500, 100);
    final NumberAxis xAxis1 = new NumberAxis(-500, 500, 100);
    final NumberAxis yAxis1 = new NumberAxis(-500, 500, 100);
    final NumberAxis xAxis2 = new NumberAxis(-500, 500, 100);
    final NumberAxis yAxis2 = new NumberAxis(-500, 500, 100);
    /* Create new scatter-charts and put the created axes on them. */
    final ScatterChart<Number, Number> s1 = new ScatterChart<Number, Number>(xAxis, yAxis);
    final ScatterChart<Number, Number> s2 = new ScatterChart<Number, Number>(xAxis1, yAxis1);
    final ScatterChart<Number, Number> s3 = new ScatterChart<Number, Number>(xAxis2, yAxis2);
    /*
     * Create a button named "add" create a button named "start".
     */
    Button start, choose, network, measurement;
    File f;
    Stage stage;
    FileChooser fp;
    File file;
    TrackingDataSource source;
    DataService da;
    MeasurementView myMeasurementView;
    Timeline timeline;
    // series3.setName("YZ-Diagramm");
    /*
     * Create new series for the coordinate system and set name.
     */
    XYChart.Series series1 = new XYChart.Series();
    // series1.setName("XY-Diagramm");
    XYChart.Series series2 = new XYChart.Series();
    // series2.setName("XZ-Diagramm");
    XYChart.Series series3 = new XYChart.Series();
    Label pos = new Label();
    Label rot = new Label();

    @SuppressWarnings("unchecked")

    /*
     * Create a method which contains the stage. Stage is the main window and
     * all elements of the window are entered there
     */

    @Override
    public void start(Stage stage) throws InterruptedException {


        stage.setTitle("Tracking Data Analysis Tool");

        /* ############# create charts #############  */
        /* create variables x, y and z from type string to store each axis */
        String x = "X-axis";
        String y = "Y-axis";
        String z = "Z-axis";

        /* Set labels for the axes (with declared string-variables). */
        xAxis.setLabel(x);
        yAxis.setLabel(y);
        xAxis1.setLabel(x);
        yAxis1.setLabel(z);
        xAxis2.setLabel(z);
        yAxis2.setLabel(y);

        /* Set title for the scatter-charts. */
        s1.setTitle("XY-plane");
        s2.setTitle("XZ-plane");
        s3.setTitle("YZ-plane");

        /*
         * set size for each scatter-chart and add the series on the
         * scatter-charts, which contain the axes
         */
        s1.setPrefSize(400, 300);
        s1.getData().addAll(series1);

        s2.setPrefSize(400, 300);
        s2.getData().addAll(series2);

        s3.setPrefSize(400, 300);
        s3.getData().addAll(series3);

        /* create a new scene */
        Scene scene = new Scene(new Group());


        /* #############  create buttons  ############# */
        choose = new Button("Load Data From MITK CSV File");
        choose.setMinWidth(280);

        choose.setOnAction((event) -> { // handle button event
            fp = new FileChooser();
            fp.setTitle("Load Data");
            fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

            file = fp.showOpenDialog(stage);
            CSVFileReader newSource = new CSVFileReader();
            if (file != null) {
                newSource.setPath(file.getAbsolutePath());
                newSource.setRepeatMode(true);
                source = newSource;
                if (myMeasurementView != null)
                    myMeasurementView.setTrackingDeviceSource(source);
            }

        });

        network = new Button("Connect via OpenIGTLink (localhost)");
        network.setMinWidth(280);

        // handle button event
        network.setOnAction((event) -> {
            OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
            source = newSource;
            if (myMeasurementView != null)
                myMeasurementView.setTrackingDeviceSource(source);
        });

        /* create new button "start" with the name "Start" */
        start = new Button("Start Tracking Data Visualization");
        start.setMinWidth(280);

        /*
         * add action on the button "start" if the button is clicked, there will
         * be shown the values x, y and z on the axes of the scatter-charts
         */
        start.setOnAction((event) -> {

            if (timeline == null && source != null) {
                series1.getData().clear();
                series2.getData().clear();
                series3.getData().clear();

                // create an object from the class "DataService" in package
                // algorithm
                da = new DataService(source);

                timeline = new Timeline();
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(100),
                                event2 -> updateDiagrams())
                );
                timeline.play();
                updateDiagrams();
            }

        });

        /* create new button "start" with the name "Start" */
        measurement = new Button("Open Measurement View");
        measurement.setMinWidth(280);

        /*
         * add action on the button "start" if the button is clicked, there will
         * be shown the values x, y and z on the axes of the scatter-charts
         */
        measurement.setOnAction((event) -> {
            myMeasurementView = new MeasurementView(source);
            myMeasurementView.setSize(1000, 1000);
            myMeasurementView.setTitle("TrackingDataAnalysisTool");
            myMeasurementView.setLocation(600, 30);
            myMeasurementView.setVisible(true);
            myMeasurementView.validate();
        });

        /* create layouts */
        VBox vbox = new VBox();
        VBox v2box = new VBox();
        VBox v3box = new VBox();
        HBox hbox = new HBox();


        pos.setText("Position: [-]");
        rot.setText("Orientation: [-]");

        /* Add buttons and charts to the layout */
        vbox.setSpacing(10);
        vbox.getChildren().addAll(choose, network, start, measurement);
        v2box.getChildren().addAll(s1, s2);
        v3box.getChildren().addAll(s3, pos, rot);
        hbox.getChildren().addAll(v2box, v3box, vbox);

        ((Group) scene.getRoot()).getChildren().add(hbox);
        stage.setScene(scene);
        stage.show();

    }

    public void updateDiagrams() {
        // all Tools with all measurements
        source.update();
        List<ToolMeasure> tools = da.loadNextData(1);
        if (tools.isEmpty()) return;
        ToolMeasure tool = tools.get(tools.size() - 1);

        // all measurements from one tool
        List<Measurement> li = tool.getMeasurement();

        series1.getData().clear();
        series2.getData().clear();
        series3.getData().clear();

        for (int i = 1; i < 5; i++) { //use the last 5 measurements, otherwise blending will be a problem during motion

            if (li.size() - i < 0) {
                break;
            }

            double x = li.get(li.size() - i).getPoint().getX();
            double y = li.get(li.size() - i).getPoint().getY();
            double z = li.get(li.size() - i).getPoint().getZ();

            if (i == 1) {
                DecimalFormat df = new DecimalFormat("0.00");
                double qX = li.get(li.size() - i).getRotation().getX();
                double qY = li.get(li.size() - i).getRotation().getY();
                double qZ = li.get(li.size() - i).getRotation().getZ();
                double qR = li.get(li.size() - i).getRotation().getW();
                pos.setText("Position: [" + df.format(x) + ";" + df.format(y) + ";" + df.format(z) + "]");
                rot.setText("Orientation: [" + df.format(qX) + ";" + df.format(qY) + ";" + df.format(qZ) + ";" + df.format(qR) + "]");
            }

            series1.getData().add(new XYChart.Data(x, y));
            series2.getData().add(new XYChart.Data(x, z));
            series3.getData().add(new XYChart.Data(z, y));
        }
    }
}
