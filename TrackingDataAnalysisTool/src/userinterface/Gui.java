package userinterface;

import algorithm.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class Gui extends JFrame implements ActionListener{
	
	private JButton b1= new JButton("Starte Messung");
	private JButton b2= new JButton("Beende Messung");
	private JButton b3 = new JButton("Lade Datei");
	private JTextArea xEbene = new JTextArea(6, 50);
	private JTextArea yEbene = new JTextArea(6, 50);
	private JTextArea zEbene = new JTextArea(6, 50);
	
	
	public Gui(){
		//Fenster-Ereignisse zulassen
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	
	private void init(){
		//NorthPanel
		JPanel panelNorth = new JPanel();
		JLabel l0 = new JLabel("Dateiname der CSV-Datei:");
		panelNorth.add(l0); 
		JTextField adresse= new JTextField(15); 
		panelNorth.add(adresse); 
		panelNorth.add(b3);

		//Center
		JPanel panelCenter = new JPanel(); 	
		JLabel l1= new JLabel("x-Ebene"); 
		panelCenter.add(l1);
		panelCenter.add(xEbene);
		JLabel l2= new JLabel("y-Ebene");
		panelCenter.add(l2);
		panelCenter.add(yEbene);
		JLabel l3= new JLabel("z-Ebene");
		panelCenter.add(l3);
		panelCenter.add(zEbene);

		//East
		JPanel panelEast= new JPanel();
		JLabel l5 = new JLabel("Messarten"); 
		panelEast.add(l5); 
		String[] messungen = {"Rauschen", "Genauigkeit", "Jitter"}; 
		JComboBox messarten = new JComboBox(messungen);
		panelEast.add(messarten); 
		panelEast.add(b1);
		panelEast.add(b2);
		List list= new List(3);
		list.add("Mittelwert");
		list.add("Jitter");
		panelEast.add(list);
		JButton b4 = new JButton("Berechne");
		panelEast.add(b4); 
		
		//WestPanel
		JPanel panelWest = new JPanel();
		
		//Verknueft alle buttons,etc. mit dem Frame als ActionListener
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		messarten.addActionListener(this);
		list.addActionListener(this);
	
		this.setVisible(true);
		this.setSize(1000, 800);
		this.setTitle("TrackingDatenAnalysisTool");
		this.setContentPane(panelNorth);
		this.setContentPane(panelCenter);
		this.setContentPane(panelEast);
		this.setContentPane(panelWest);
		this.setLayout(new BorderLayout());
		this.add(panelNorth, BorderLayout.NORTH);
		this.add(panelEast, BorderLayout.EAST);
		this.add(panelCenter, BorderLayout.CENTER);
		this.add(panelWest, BorderLayout.WEST);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//Fenster schliessbar 
	protected void processWindowsEvent(WindowEvent e){ 
		//super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0); //Prgm wird beendet
		}
	}
	public void actionPerformed1(ActionEvent ae) {
				if(ae.getSource() == this.b3){
				//	String[] data = DataService.loadNextData();
					while(true){
					//	String.valueOf(data); 
						xEbene.setText("datei1");
						yEbene.setText("datei2");
						zEbene.setText("datei3");			
					}
				}else if(ae.getSource() == this.b1){
					JOptionPane.showMessageDialog(null, "Bitte das Geraet jetzt ruhig liegen lassen!" , 
									"Hinweis", JOptionPane.WARNING_MESSAGE,null); 
				}
			}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
