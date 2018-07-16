package userinterface;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import algorithm.Measurement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.*;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import algorithm.ToolMeasure;
import inputOutput.CSVFileReader;
import algorithm.DataManager;
import algorithm.DataProcessor;
import algorithm.DataService;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;


import java.net.*;
import inputOutput.*;
import testInputOutput.*;

public class Diagramm extends Application {

	/*
	 * Create a button named "add" create a button named "start".
	 */
	Button start, choose, network;
	File f;
	Stage stage;
	FileChooser fp;
	File file;
	
	TrackingDataSource source;
	DataService da;
	
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
	
	/*
	 * Create new series for the coordinate system and set name.
	 */
	XYChart.Series series1 = new XYChart.Series();
	// series1.setName("XY-Diagramm");
	XYChart.Series series2 = new XYChart.Series();
	// series2.setName("XZ-Diagramm");
	XYChart.Series series3 = new XYChart.Series();
	// series3.setName("YZ-Diagramm");
	
	/* Create new scatter-charts and put the created axes on them. */
	final ScatterChart<Number, Number> s1 = new ScatterChart<Number, Number>(xAxis, yAxis);
	final ScatterChart<Number, Number> s2 = new ScatterChart<Number, Number>(xAxis1, yAxis1);
	final ScatterChart<Number, Number> s3 = new ScatterChart<Number, Number>(xAxis2, yAxis2);
	
	@SuppressWarnings("unchecked")

	/*
	 * Create a method which contains the stage. Stage is the main window and
	 * all elements of the window are entered there
	 */

	@Override
	public void start(Stage stage) throws InterruptedException {

		/* Create variables x, y and z from type string to store each axis */

		String x = "X-axis";
		String y = "Y-axis";
		String z = "Z-axis";

		/* The stage is named "x-y-z-Ebene" */
		stage.setTitle("x-y-z-plane");

		

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

		/* create vbox and hbox for the scatter-charts */
		final VBox vbox = new VBox();
		final HBox hbox = new HBox();

		/* create new button "start" with the name "Start" */

		start = new Button("Start");

		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		

		GridPane grid2 = new GridPane();
		grid2.setVgap(4);
		grid2.setHgap(10);
		grid2.setPadding(new Insets(5, 5, 5, 5));
		
		
		choose = new Button("Load Data");

		// handle button event
		choose.setOnAction((event) -> {
			fp = new FileChooser();
			fp.setTitle("Load Data");
			fp.getExtensionFilters().addAll(new ExtensionFilter("Text Datei", "*.csv"));

			file = fp.showOpenDialog(stage);
			CSVFileReader newSource = new CSVFileReader();
			if (file != null)
			{
				newSource.setPath(file.getAbsolutePath());
				newSource.setRepeatMode(true);
				source = newSource;
			}

		});
		
		network = new Button("Connect via OpenIGTLink (localhost)");
		
		// handle button event
		network.setOnAction((event) -> {
					
					OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
					source = newSource;

				});

		/*
		 * add action on the button "start" if the button is clicked, there will
		 * be shown the values x, y and z on the axes of the scatter-charts
		 */
		start.setOnAction((event) -> {
			
			//inputOutput.CSVFileReader.setLine_counter();
			series1.getData().clear();
			series2.getData().clear();
			series3.getData().clear();

			// create an object from the class "DataService" in package
			// algorithm
			da = new DataService(source);
			
			// number of passes
			Gui myGui = new Gui();
			int countGoToNext = myGui.toloadvalue;
			
			Timeline timeline = new Timeline();
	        timeline.setCycleCount(Animation.INDEFINITE);
	        timeline.getKeyFrames().add(
	                new KeyFrame(Duration.millis(100),
	                event2 -> updateDiagrams())
	        );
	        timeline.play();
			updateDiagrams();
			
		});

		hbox.setSpacing(10);

		/*
		 * vbox added to the hbox
		 */
		hbox.getChildren().addAll(vbox);

		/*
		 * the button "start" and the scatter-charts added to the vbox
		 */
		vbox.getChildren().addAll(grid, grid2, start, choose, network, s1, s2, s3);

		// vbox.getChildren().add(adresse);
		hbox.setPadding(new Insets(50, 10, 50, 20));

		stage.setX(120);
		stage.setY(0);
		/*
		 * vbox added to the scene and the scene added to the main stage
		 */
		((Group) scene.getRoot()).getChildren().add(vbox);
		stage.setScene(scene);
		stage.show();

	}

	public static void main(String[] args) {

		launch(args);
	}
	
	public void updateDiagrams()
	{
		// all Tools with all measurements
		List<ToolMeasure> tools = da.loadNextData(1);
		if(tools.isEmpty()) return;
		ToolMeasure tool = tools.get(tools.size()-1);
			
		// all measurements from one tool
		List<Measurement> li = tool.getMeasurement();
		
		series1.getData().clear();
		series2.getData().clear();
		series3.getData().clear();
		
		for (int i=1; i<5; i++) //use the last 5 measurements, otherwise blending will be a problem during motion
		{
			if(li.size()-i < 0) break;
			
			double x = li.get(li.size()-i).getPoint().getX();
			double y = li.get(li.size()-i).getPoint().getY();
			double z = li.get(li.size()-i).getPoint().getZ();
			
			series1.getData().add(new XYChart.Data(x, y));
			series2.getData().add(new XYChart.Data(x, z));
			series3.getData().add(new XYChart.Data(z, y));
		}
	}
}
