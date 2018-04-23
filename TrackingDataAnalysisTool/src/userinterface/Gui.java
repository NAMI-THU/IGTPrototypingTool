package userinterface;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Gui {//extends JFrame implements ActionListener {
	/*
	JFrame guiFrame = new JFrame();
	
	guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
	guiFrame.setTitle("Tracking-System-Daten");
	guiFrame.setSize(300,250);
	
	private JPanel panel = new JPanel (); 
	
	private JButton button1 = new JButton("Starte Messung");
	private JButton button2 = new JButton("Beende Messung");
	
	String[] messungOptions = {"Rauschen", "Genauigkeitsmessung", "Kalibrierung","Fehler"};

	
	private ComboBox messungen = new  ComboBox(messungOptions); 
	
	private CheckboxGroup Messungsart = new CheckboxGroup(); 
	private Checkbox box1 = new Checkbox("Rauschen",Messungsart, false); 
	private Checkbox box2 = new Checkbox("Genauigkeit", Messungsart, false); 
	private Checkbox box3 = new Checkbox("Rauschen",Messungsart, false); 
	private Checkbox box4 = new Checkbox("Fehler", Messungsart, false); 
	
	public Gui{
		
		// Fenster-Ereignisse zulassen:
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
}

	


	//Initialisierung der Komponenten 
	 private void init() {
		 
		 this.setSize(400, 300);  
		 this.setTitle("Tracking-System-Daten");
		 
		 JPanel panel= new JPanel(); 
		 panel.setLayout(new FlowLayout()); 
		 
		 button1.addActionListener(this);
		 button2.addActionListener(this);
		 
		 panel.add(button1);
		 panel.add(button2);
		 
		 
		 this.setContentPane(panel);
		 
		 Choice choice = new Choice();
		 choice.add("Rauschen"); 
		 choice.add("Genauigkeit"); 
		 choice.add("Kalibrierung "); 
		 this.add(choice); 
		 
		 
		 List list= new List(4); 
		 list.add("Jitter");
		 list.add("Fehlerwert");
		 list.add("Genauigkeit"); 
		 this.add(list);
		 
	
			
		
    }
	 //wird überschrieben, um das Fenster schliessen zu können
	 protected void processWindowEvent(WindowEvent e) {
		 if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			 	System.exit(0);
	 }

}
}*/

}
