package userinterface;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class Gui extends JFrame implements ActionListener{
	
	private JButton b1= new JButton("Starte Messung");
	private JButton b2= new JButton("Beende Messung");
	private JCheckBox checkBox = new JCheckBox("Rauschen");
	private JCheckBox checkBox2 = new JCheckBox("Genauigkeit");
	private JCheckBox checkBox3 = new JCheckBox("Kalibrierung");
	private JTextArea xEbene = new JTextArea(6, 20);
	private JTextArea yEbene = new JTextArea(6, 20);
	private JTextArea zEbene = new JTextArea(6, 20);
		
	public Gui(){
		//Fenster-Ereignisse zulassen
		super("My Window");
		setSize(500,500);
		setVisible(true);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	
	private void init(){
		
		JPanel mainPanel= new JPanel();
		mainPanel.setLayout(null);
		checkBox.setBounds(200,200,20,10);
		b1.setBounds(800,600,40,60);
		b2.setBounds(800,550, 40, 60);
		xEbene.setBounds(50,50,100,400); 
		yEbene.setBounds(50,100,100,400);
		zEbene.setBounds(50,150,100,400);
		
		JPanel panelNorth = new JPanel();
		JLabel l0 = new JLabel("Dateiname der CSV_Datei:");
		panelNorth.add(l0); 
		JTextField adresse= new JTextField(15); 
		panelNorth.add(adresse);
		JButton b3 = new JButton("Lade Datei");
		panelNorth.add(b3);
		
		JPanel panelEast = new JPanel();
		panelEast.setLayout(new FlowLayout());
		panelEast.add(checkBox);
		panelEast.add(checkBox2);
		panelEast.add(checkBox3);
		
		JPanel panelWest = new JPanel();
		panelWest.setLayout(new FlowLayout());
		JLabel l1= new JLabel("x-Ebene"); 
		panelWest.add(l1);
		panelWest.add(xEbene);
		JLabel l2= new JLabel("y-Ebene");
		panelWest.add(l2);
		panelWest.add(yEbene);
		JLabel l3= new JLabel("z-Ebene");
		panelWest.add(l3);
		panelWest.add(zEbene);
	
		List list= new List(3);
		list.add("Jitter");
		list.add("Fehlerwert");
		list.add("Genauigkeit");
		panelEast.add(list);
	
		//Verknueft alle buttons,etc. mit dem Frame als ActionListener
		b1.addActionListener(this);
		b2.addActionListener(this);
		checkBox.addActionListener(this);
		checkBox2.addActionListener(this);
		checkBox3.addActionListener(this);
		list.addActionListener(this);
	
		this.setContentPane(mainPanel);
		this.setLayout(new BorderLayout());
		this.setSize(1000,500);
		this.setTitle("TrackingDaten");
		this.add(panelEast, BorderLayout.EAST);
		//this.add(mainPanel, BorderLayout.EAST);
	}

	//Implementierung des Event Listeners durch die Methode actionPerformed
	public void actionPerformed(ActionEvent e) {
		//Object src = evt.getSource();			
	}
	//Fenster schließbar 
	protected void processWindowsEvent(WindowEvent e){ 
		//super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0); //Prgm wird beendet
		}
	}


}
