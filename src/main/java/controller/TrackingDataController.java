package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import algorithm.DataService;
import algorithm.Measurement;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import inputOutput.OpenIGTLinkConnection;
import inputOutput.TrackingDataSource;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import javafx.stage.Stage;
import userinterface.ExceptionWindow;

public class TrackingDataController implements Controller {

    @FXML ScatterChart<Number, Number> s1;
    @FXML ScatterChart<Number, Number> s2;
    @FXML ScatterChart<Number, Number> s3;
    @FXML VBox posBox;
    @FXML VBox rotBox;
    public DataService ds;
    public Timeline timeline;
    TrackingDataSource source;
    HashMap<String, Label> position;
    HashMap<String, Label> rotation;
    HashMap<String, XYChart.Series<Double, Double>[]> toolSeriesMap;
    Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toolSeriesMap = new HashMap<String, XYChart.Series<Double, Double>[]>();
        position = new HashMap<String, Label>();
        rotation = new HashMap<String, Label>();
        //pos& rot evtl an fxml zeug anpassen
    }

    public TrackingDataSource getSource() {
        return source;
    }

    public void setStatusLabel(Label statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * load CSV file with tracking data created by MITK workbench
     */
    @FXML
    private void loadCSVFile() {
        CSVFileReader newSource = null;
        FileChooser fp = new FileChooser();
        fp.setTitle("Load Data");
        fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

        File file = fp.showOpenDialog(new Stage());
        if (file != null) {
            try {
                newSource = new CSVFileReader(file.getAbsolutePath());
                for (String name : newSource.getToolNames()) {
                    System.out.println(name);
                }
            } catch (IOException e) {
                new ExceptionWindow();
            }
            // csv faengt von vorn an anstatt bei der letzten reihe zu bleiben
            newSource.setRepeatMode(true);
            source = newSource;
            statusLabel.setText("CSV file loaded");
        }
    }

    /**
     * Connect via OpenIGTLink.
     */
    @FXML
    private void connect() {
        OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
        source = newSource;
    }

    /**
     * add action on the button "start" if the button is clicked, there will
     * be shown the values x, y and z on the axes of the scatter-charts
     */
    @FXML
    public void visualizeTracking() {

        if (timeline == null && source != null) {
            // if tool/dataseries mapping is not empty, delete dataseries entries
            toolSeriesMap.forEach((tool,seriesarray) -> {
                for (XYChart.Series<Double, Double> dataseries : seriesarray) {
                    dataseries.getData().clear();
                }
            });

            // create an object from the class "DataService" in package
            // algorithm which is later used to load tool measurements to this class
            ds = new DataService(source);

            // start timeline and add new keyframe update every 100 ms
            timeline = new Timeline();
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis(100),
                            event2 -> updateDiagrams())
            );
            timeline.play();
            updateDiagrams();
        }
        if (timeline != null) {
            timeline.play();
        }
    }

    public void updateDiagrams() {
        /* all Tools with all measurements
         * update csv: create new arraylist (size of toolname list),
         * add measurements of new record to this arraylist everytime
         * update igtlink: create networkconnection if null;
         * get synchronized tooldatalist from networkconnection & set values
         * in openigtlinkconnection */
        source.update();

        /* gets one new measurement from source (via dataservice->datamanager-> source.getLastToolList)
        * lastToolList in TrackingDataSource is set w each update
        * -> tools = list of measurements of different tools at the same time () */
        List<ToolMeasure> tools = ds.loadNextData(1);
        if (tools.isEmpty()) return;
        for (ToolMeasure tool : tools) {
            // all measurements from one tool
            List<Measurement> li = tool.getMeasurement();

            if (!toolSeriesMap.containsKey(tool.getName())) {
                createSeriesForTool(tool.getName());
            }

            XYChart.Series<Double, Double>[] toolSeries = toolSeriesMap.get(tool.getName());
            for (XYChart.Series<Double, Double> s : toolSeries) {
                s.getData().clear();
            }

            //use the last 5 measurements, otherwise blending will be a problem during motion
            for (int i = 1; i < 5; i++) {
                if (li.size() - i < 0) {
                    break;
                }
                double x = li.get(li.size() - i).getPoint().getX();
                double y = li.get(li.size() - i).getPoint().getY();
                double z = li.get(li.size() - i).getPoint().getZ();

                // display position and rotation of tool
                if (i == 1) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    double qX = li.get(li.size() - i).getRotation().getX();
                    double qY = li.get(li.size() - i).getRotation().getY();
                    double qZ = li.get(li.size() - i).getRotation().getZ();
                    double qR = li.get(li.size() - i).getRotation().getW();

                    position.get(tool.getName()).setText(tool.getName() + ": ["
                            + df.format(x) + ";"
                            + df.format(y) + ";"
                            + df.format(z) + "]");
                    rotation.get(tool.getName()).setText(tool.getName()+": ["
                            + df.format(qY) + ";"
                            + df.format(qZ) + ";"
                            + df.format(qR) + "]");
                }

                toolSeries[0].getData().add(new XYChart.Data<Double, Double>(x, y));
                toolSeries[1].getData().add(new XYChart.Data<Double, Double>(x, z));
                toolSeries[2].getData().add(new XYChart.Data<Double, Double>(z, y));
            }
        }
    }

    /**
     * This method is called when a tool is returned by DataService
     * loadNextData that is not yet in toolSeriesMap.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void createSeriesForTool(String toolname) {

        /* create arrays for each tool and each chart */
        XYChart.Series[] seriesArray = new XYChart.Series[3];
        // Series needs to have a dataset so name and symbol are set correctly
        for (int i = 0; i < 3; i++) {
            seriesArray[i] = new XYChart.Series();
            seriesArray[i].getData().add(new XYChart.Data(0,0));
            seriesArray[i].setName(toolname);
        }
        s1.getData().addAll(seriesArray[0]);
        s2.getData().addAll(seriesArray[1]);
        s3.getData().addAll(seriesArray[2]);
        toolSeriesMap.put(toolname, seriesArray);

        /* create labels for tool position and rotation */
        position.put(toolname, new Label(toolname + ": [-]"));
        posBox.getChildren().add(position.get(toolname));
        rotation.put(toolname, new Label(toolname + ": [-]"));
        rotBox.getChildren().add(rotation.get(toolname));
    }

    @FXML
    public void freezeVisualization() {
        if (timeline != null) {
            switch (timeline.getStatus()) {
            case RUNNING:
                timeline.pause();
                statusLabel.setText("Animation paused");
                break;
            case PAUSED:
                timeline.play();
                statusLabel.setText("Animation running");
                break;
            default: break;
            }
        }
    }

    public void close() {
    }
}
