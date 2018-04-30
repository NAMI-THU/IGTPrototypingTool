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
	
	/*private JComboBox cBox1= new JComboBox();
	private JComboBox cBox2 = new JComboBox();
	private JComboBox cBox3= new JComboBox();
	private JComboBox cBox4= new JComboBox();*/
	
	public Gui(){
		//Fenster-Ereignisse zulassen
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	private void init(){
		
		JPanel mainPanel= new JPanel();
		mainPanel.setLayout(null);
		checkBox.setBounds(200,200,20,10);
		b1.setBounds(800,600,40,60);
		b2.setBounds(800,550, 40, 60);
		
		JPanel panelEast = new JPanel();
		panelEast.setLayout(new FlowLayout());
		panelEast.add(checkBox);
		panelEast.add(checkBox2);
		panelEast.add(checkBox3);
		
		List list= new List(3);
		list.add("Jitter");
		list.add("Fehlerwert");
		list.add("Genauigkeit");
		panelEast.add(list);
	
		//Verknüpft alle buttons,etc. mit dem Frame als ActionListener
		b1.addActionListener(this);
		b2.addActionListener(this);
		checkBox.addActionListener(this);
		checkBox2.addActionListener(this);
		checkBox3.addActionListener(this);
		list.addActionListener(this);
		
		/*add with panel
		mainPanel.add(b1);
		mainPanel.add(b2);
		mainPanel.add(checkBox);
		mainPanel.add(checkBox2);
		mainPanel.add(checkBox3);
		mainPanel.add(cBox1);*/	
		
		this.setContentPane(mainPanel);
		this.setLayout(new BorderLayout());
		this.setSize(1000,500);
		this.setTitle("TrackingDaten");
		this.add(panelEast, BorderLayout.EAST);
		this.add(mainPanel, BorderLayout.EAST);
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
