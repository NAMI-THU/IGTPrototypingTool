package mainMethod;

import javax.swing.JFrame;

import userinterface.*;

public class main {

	public static void main(String[] args) {
		
		Gui myGui = new Gui();
		myGui.setSize(1000, 800);
		myGui.setTitle("TrackingDataAnalysisTool");
		myGui.setLocation(150,150);
		myGui.setVisible(true);
		myGui.validate();
		myGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}