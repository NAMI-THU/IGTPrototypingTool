package userinterface;

import java.util.ArrayList;
import java.util.List;

import algorithm.Measurement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.*;
import algorithm.ToolMeasure;
import algorithm.DataProcessor;
import algorithm.DataService;

public class Diagramm extends Application{


		
		Button add, start;
		//@SupressWarnings("unchecked")
		@SuppressWarnings("unchecked")
		@Override public void start(Stage stage) throws InterruptedException {

			String x = "X-Achse";
			String y = "Y-Achse";
			String z = "Z-Achse";
			
			stage.setTitle("x-y-z-Ebene");
		
		final NumberAxis xAxis = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis = new NumberAxis(0, 30, 1);
		
		final NumberAxis xAxis1 = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis1 = new NumberAxis(0, 30, 1);
		
		final NumberAxis xAxis2 = new NumberAxis(0, 30, 1);
		final NumberAxis yAxis2 = new NumberAxis(0, 30, 1);
		
		
		xAxis.setLabel(x);
		yAxis.setLabel(y);
		
		xAxis1.setLabel(x);
		yAxis1.setLabel(z);
		
		xAxis2.setLabel(z);
		yAxis2.setLabel(y);
		
		final ScatterChart <Number, Number> s1 = 
				new ScatterChart<Number, Number>(xAxis, yAxis);
		final ScatterChart <Number, Number> s2 = 
				new ScatterChart<Number, Number>(xAxis1, yAxis1);
		final ScatterChart <Number, Number> s3 = 
				new ScatterChart<Number, Number>(xAxis2, yAxis2);
		
		s1.setTitle("XY-Ebene");
		s2.setTitle("XZ-Ebene");
		s3.setTitle("ZY-Ebene");
		
	
XYChart.Series series1 = new XYChart.Series();
//series1.setName("XY-Diagramm");
XYChart.Series series2 = new XYChart.Series();
//series2.setName("XZ-Diagramm");
XYChart.Series series3 = new XYChart.Series();
//series3.setName("ZY-Diagramm");


s1.setPrefSize(400, 300);
s1.getData().addAll(series1);

s2.setPrefSize(400, 300);
s2.getData().addAll(series2);

s3.setPrefSize(400, 300);
s3.getData().addAll(series3);

Scene scene = new Scene(new Group());
final VBox vbox = new VBox();
final HBox hbox = new HBox();

//RadioButton radioB1 = new RadioButton("xyz");
start = new Button("Start");

	start.setOnAction((event)->{
	series1.getData().clear();
	
		//String choice = "xyz";
		
//		if (radioB1.isSelected()) {
//    		choice = "xyz";
//            series1.setName("XY-Diagramm");
//            series2.setName("XZ-Diagramm");
//		}


		
		// Objekt erstellen
		DataService da = new DataService();
		
		//wie oft 
		int countGoToNext =  34;
		
		// alle Tools mit allen Messungen
		List<ToolMeasure> tools = da.loadNextData(countGoToNext);
		
		//List<Measurement> l = new ArrayList();
		
		//ToolMeasure tx = new ToolMeasure();
		
		for(int i = 0; i<tools.size(); i++){
			ToolMeasure tool = tools.get(i);
			
			//alle Messungen von einem Tool
			List<Measurement> li = tool.getMeasurement();
			
			
		Coordinatesystem.drawAchsen("xy", li , series1, xAxis, yAxis);
		Coordinatesystem.drawAchsen("xz", li, series2, xAxis, yAxis);
		Coordinatesystem.drawAchsen("zy", li, series3, xAxis, yAxis);
	}
 	});



hbox.setSpacing(10);
//hbox.getChildren().addAll(s1, s2, s3);
hbox.getChildren().addAll(vbox);

//vbox.getChildren().addAll(start, hbox);
vbox.getChildren().addAll(start, s1, s2, s3);
hbox.setPadding(new Insets(50, 10, 50, 20));

((Group)scene.getRoot()).getChildren().add(vbox);
stage.setScene(scene);
stage.show();
}


public static void main(String[]args){

launch(args);}}



