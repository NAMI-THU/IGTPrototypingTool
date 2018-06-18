package mainMethod;

import javax.swing.JFrame;

import algorithm.*;
import inputOutput.*;
import userinterface.*;

public class main {

	public static void main(String[] args) {
		Gui myGui = new Gui();
		myGui.setSize(1200, 1000);
		myGui.setTitle("TrackingDataAnalysisTool");
		myGui.setLocation(250,30);
		myGui.setVisible(true);
		myGui.validate();
		myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
        Diagramm app = new Diagramm();
        app.main(null);
        
       
        
	}

}