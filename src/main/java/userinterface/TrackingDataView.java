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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/* TODO 
 * make layout dynamic & better to use
 */
public class TrackingDataView extends Application {

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
    Button start, choose, network, measurement, thrombectomyView;
    File f;
    Stage stage;
    FileChooser fp;
    File file;
    TrackingDataSource source;
    DataService da;
    MeasurementView myMeasurementView;
    Timeline timeline;
    
    /*
     * maps tool name to series of points displayed in charts
     */
    HashMap<String,XYChart.Series<Double,Double>[]> toolSeriesMap;

    HashMap<String, Label> position;
    HashMap<String, Label> rotation;
    
    VBox posBox;
    VBox rotBox;
    
    /*
     * Create a method which contains the stage. Stage is the main window and
     * all elements of the window are entered there
     */
    @Override
    public void start(Stage stage) throws InterruptedException {

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

        /* Set title and size for the scatter-charts. */
        s1.setTitle("XY-plane");
        s2.setTitle("XZ-plane");
        // probs ZY-plane instead
        s3.setTitle("YZ-plane");
        s1.setPrefSize(400, 300);
	    s2.setPrefSize(400, 300);
	    s3.setPrefSize(400, 300);
    	
        /* create map to store tool name and respective data series and List for displaying current measurements */
        toolSeriesMap = new HashMap<String, XYChart.Series<Double, Double>[]>();
        position = new HashMap<String, Label>();
        rotation = new HashMap<String, Label>();

        /* #############  create buttons  ############# */
        choose = new Button("Load Data From MITK CSV File");
        choose.setMinWidth(280);
        network = new Button("Connect via OpenIGTLink (localhost)");
        network.setMinWidth(280);
        start = new Button("Start Tracking Data Visualization");
        start.setMinWidth(280);
        measurement = new Button("Open Measurement View");
        measurement.setMinWidth(280);
        thrombectomyView = new Button("Open Thrombectomy View");
        thrombectomyView.setMinWidth(280);
        
        // handle button events
        choose.setOnAction(loadCSVFile());
        network.setOnAction(connect());
        start.setOnAction(visualizeTracking());
        measurement.setOnAction(openMeasurementView());
        thrombectomyView.setOnAction(openThrombectomyView());

        /* tab pane to display position and rotation of tools */
        HBox hbox2 = new HBox();
        hbox2.setPadding(new Insets(0,10,20,10));
        posBox = new VBox();
        posBox.setMinWidth(250);
        posBox.getChildren().add(new Label("Position:"));
    	rotBox  = new VBox();
    	rotBox.getChildren().add(new Label("Rotation:"));
    	hbox2.getChildren().addAll(posBox, rotBox);
    	hbox2.setMaxWidth(350);
        
        /* create layouts */
        VBox vbox = new VBox();
        VBox v2box = new VBox();
        VBox v3box = new VBox();
        HBox hbox = new HBox();

        /* Add buttons and charts to the layout */
        vbox.setSpacing(10);
        vbox.getChildren().addAll(choose, network, start, measurement, thrombectomyView);
        v2box.getChildren().addAll(s1, s2);
        v3box.getChildren().addAll(s3, hbox2);
        hbox.getChildren().addAll(v2box, v3box, vbox);

        /* display stage */
        stage.setTitle("Tracking Data Analysis Tool");
        Scene scene = new Scene(new Group());
        ((Group) scene.getRoot()).getChildren().add(hbox);
        stage.setScene(scene);
        stage.show();
    }

    
    /*
     * load CSV file with tracking data created by MITK workbench
     */
    private EventHandler<ActionEvent> loadCSVFile(){
    	return event -> {
            CSVFileReader newSource = null;
                fp = new FileChooser();
                fp.setTitle("Load Data");
                fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

                file = fp.showOpenDialog(stage);
                if (file != null) {
                    try {
                        newSource = new CSVFileReader(file.getAbsolutePath());
                        for (String name : newSource.getToolNames()) {
                        	System.out.println(name);
                        }
                    } catch (IOException e) {
                    	new ExceptionWindow(); 
                    }
                    // csv fängt von vorn an anstatt bei der letzten reihe zu bleiben
//                    newSource.setRepeatMode(true);
                    source = newSource;
                    if (myMeasurementView != null) {
                        myMeasurementView.setTrackingDeviceSource(source);
                    }
                }
    	};
    }
    
    /*
     * Connect via OpenIGTLink.
     */
    private EventHandler<ActionEvent> connect(){
    	return event -> {
            OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
            source = newSource;
            if (myMeasurementView != null)
                myMeasurementView.setTrackingDeviceSource(source);
        };
    }
    
    /*
     * add action on the button "start" if the button is clicked, there will
     * be shown the values x, y and z on the axes of the scatter-charts
     */
    public EventHandler<ActionEvent> visualizeTracking(){
    	return event -> {
    		// tracking timeline has not started yet and source is loaded (csv or openigt)
            if (timeline == null && source != null) {
            	// if tool/dataseries mapping is not empty, delete dataseries entries
            	toolSeriesMap.forEach((tool,seriesarray) -> {
            		for(XYChart.Series<Double, Double> dataseries : seriesarray) {
            			dataseries.getData().clear();
            		}
            	});

                // create an object from the class "DataService" in package
                // algorithm which is later used to load tool measurements to this class
                da = new DataService(source);

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
        };
    }
    
    private EventHandler<ActionEvent> openMeasurementView(){
    	return event -> {
            myMeasurementView = new MeasurementView(source);
            myMeasurementView.setSize(1000, 1000);
            myMeasurementView.setTitle("TrackingDataAnalysisTool");
            myMeasurementView.setLocation(600, 30);
            myMeasurementView.setVisible(true);
            myMeasurementView.validate();
        };
    }
    
    private EventHandler<ActionEvent> openThrombectomyView(){
    	return event -> {
			new ThrombectomyView(this);
    	};
    }
    
    public void updateDiagrams() {
    	
        // all Tools with all measurements
    	// update csv: create new arraylist (size of toolname list), add measurements of new record to this arraylist everytime
    	// update igtlink: create networkconnection if null; get synchronized tooldatalist from networkconnection & set values 
    	// 					in openigtlinkconnection
        source.update();
        
        // gets one new measurement from source (via dataservice->datamanager-> source.getLastToolList)
        // lastToolList in TrackingDataSource is set w each update 
        // -> tools = list of measurements of different tools at the same time ()
        List<ToolMeasure> tools = da.loadNextData(1);
        if (tools.isEmpty()) return;
        for(ToolMeasure tool : tools) {
	
	        // all measurements from one tool
	        List<Measurement> li = tool.getMeasurement();
	        
	        if(!toolSeriesMap.containsKey(tool.getName())) {
	        	createSeriesForTool(tool.getName());
	        }
	        
	        XYChart.Series<Double, Double>[] toolSeries = toolSeriesMap.get(tool.getName());
	        for(XYChart.Series<Double, Double> s : toolSeries) {
	        	s.getData().clear();
	        }
	        
	        for (int i = 1; i < 5; i++) { //use the last 5 measurements, otherwise blending will be a problem during motion
	
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
	                
	                position.get(tool.getName()).setText(tool.getName()+": [" 
	                		+ df.format(x) + ";" + df.format(y) + ";" + df.format(z) + "]");
	                rotation.get(tool.getName()).setText(tool.getName()+": [" 
	                		+ df.format(qX) + ";" + df.format(qY) + ";" + df.format(qZ) + ";" + df.format(qR) + "]");
	            }
	            
	            toolSeries[0].getData().add(new XYChart.Data<Double, Double>(x,y));
	            toolSeries[1].getData().add(new XYChart.Data<Double, Double>(x,z));
	            toolSeries[2].getData().add(new XYChart.Data<Double, Double>(z,y));
	        }
        }
    }
    
    /**
     * This method is called when a tool is returned by DataService loadNextData
     * that is not yet in toolSeriesMap.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void createSeriesForTool(String toolname) {
    	
    	XYChart.Series[] seriesArray = new XYChart.Series[3];
    	
    	// Series needs to have a dataset so name and symbol are set correctly
    	for(int i=0; i<3; i++) {
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

}
