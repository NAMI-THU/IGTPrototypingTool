package userinterface;

import java.util.List;


import java.util.Arrays;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import algorithm.Measurement;
import algorithm.ToolMeasure;


@SuppressWarnings("unused")	
public class Coordinatesystem{

	

//@SupressWarnings({"unchecked", "rawtypes"})

public static void drawAchsen(String choice, List<Measurement> l,
		XYChart.Series s, NumberAxis xAxis, NumberAxis yAxis) {
			double x;
			double y;
			double z;
			
			switch (choice){
			
			case "xy":
			
			
			xAxis.setLabel("X-Achse");                
		    yAxis.setLabel("Y-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = l.get(i).getPoint().getX();
			//System.out.println(x);
		
			for(int j=1; j<l.size(); j=j+3) {
				y = l.get(i).getPoint().getY();
				System.out.println(y);
				s.getData().add(new XYChart.Data(x, y));
			}
		}
		break;
		
		
			case "xz":

		xAxis.setLabel("X-Achse");                
        yAxis.setLabel("Z-Achse");
		for (int i=0; i<l.size(); i=i+3) {
			x = l.get(i).getPoint().getX();;
			System.out.println(x);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = l.get(i).getPoint().getZ();;
				s.getData().add(new XYChart.Data(x, z));
			}
		}
		break;

		
			case "zy":
		
		xAxis.setLabel("Z-Achse");                
        yAxis.setLabel("Y-Achse");
		for (int i=1; i<l.size(); i=i+3) {
			y = l.get(i).getPoint().getY();;
			System.out.println(y);
		
			for(int j=2; j<l.size(); j=j+3) {
				z = l.get(i).getPoint().getZ();;
				s.getData().add(new XYChart.Data(z, y));
			}
		}
		
		break;
}}
}
		
