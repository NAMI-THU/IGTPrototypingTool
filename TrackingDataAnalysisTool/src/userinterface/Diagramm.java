package userinterface;

import java.util.List;
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

public class Diagramm extends Application{

		
		Button add, start;
		//@SupressWarnings("unchecked")
		@Override public void start(Stage stage) throws InterruptedException {
		
		final NumberAxis xAxis = new NumberAxis(0, 200, 10);
		final NumberAxis yAxis = new NumberAxis(0, 200, 10);
		final NumberAxis zAxis = new NumberAxis(0, 200, 10);
		
		final ScatterChart <Number, Number> s1 = 
				new ScatterChart<Number, Number>(xAxis, yAxis);
		final ScatterChart <Number, Number> s2 = 
				new ScatterChart<Number, Number>(xAxis, zAxis);
		final ScatterChart <Number, Number> s3 = 
				new ScatterChart<Number, Number>(yAxis, zAxis);
		
	
XYChart.Series series1 = new XYChart.Series();

s1.setPrefSize(100, 50);

final VBox vbox = new VBox();
final HBox hbox = new HBox();

RadioButton radioB1 = new RadioButton("xyz");
start = new Button("Start");

try {
	series1.getData().clear();
		String choice = "xyz";
		
		List <String> line = null;
		Coordinatesystem.drawAchsen(choice, line, series1, xAxis, yAxis);
}
catch(InterruptedException e){
	e.printStackTrace();

}

	

		hbox.setSpacing(10);
hbox.getChildren().addAll(start, s1, s2, s3);

vbox.getChildren().addAll(hbox);
hbox.setPadding(new Insets(10, 10, 10, 50));
}



public static void main(String[]args){

launch(args);}}



