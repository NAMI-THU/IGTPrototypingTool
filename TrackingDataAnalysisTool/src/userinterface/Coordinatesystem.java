package userinterface;

import java.util.List;

import java.util.Arrays;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.*;


//@SuppressWarnings("unused")	
public class Coordinatesystem extends Application{

	
	/*public Coordinatesystem () {
		init();
		}
		
	private void init(){
	  
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
      setDefaultCloseOperation(EXIT_ON_CLOSE); 
      this.setSize(new Dimension(400, 300));
      this.setTitle("x-y-z-Ebene"); }
	
	protected void processWindowEvenet(WindowEvent e){
		super.processWindowEvent(e);
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			System.exit(0);
		}
          }*/

//@SupressWarnings({"unchecked", "rawtypes"})

public static void drawAchsen(String choice, List<String>l,
		XYChart.Series s, NumberAxis xAxis, NumberAxis yAxis) {
			int x, y, z;
			
			
			
xAxis.setLabel("X-Achse");
yAxis.setLabel("Y-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = Integer.parseInt(l.get(i));
			//System.out.println(x);
		
			for(int j=1; j<l.size(); j=j+3) {
				y = Integer.parseInt(l.get(j));
				System.out.println(y);
				s.getData().add(new XYChart.Data(x, y));
			}
		}

		xAxis.setLabel("X-Achse");                
        yAxis.setLabel("Z-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = Integer.parseInt(l.get(i));
			System.out.println(x);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = Integer.parseInt(l.get(j));
				s.getData().add(new XYChart.Data(x, z));
			}
		}

		xAxis.setLabel("Z-Achse");                
        yAxis.setLabel("Y-Achse");
		for (int i=1; i<l.size(); i=i+3) {
			y = Integer.parseInt(l.get(i));
			System.out.println(y);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = Integer.parseInt(l.get(j));
				s.getData().add(new XYChart.Data(z, y));
			}
		}
}

		

		public void xyz(){
				
				Button add, start;
				//@SupressWarnings("unchecked")

				
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



}


@Override
public void start(Stage primaryStage) throws Exception {
	// TODO Auto-generated method stub


	
}

public static void main(String[]args){
	
	launch(args);}}



	
