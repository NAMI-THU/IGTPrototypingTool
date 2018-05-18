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
import java.io.File;

import javax.swing.*;

public class Gui extends JFrame implements ActionListener{
	
	private JButton b1= new JButton("Starte Messung");
	private JButton b2= new JButton("Beende Messung");
	private JButton b3 = new JButton("Lade Datei");
	private JButton b4 = new JButton("Berechne");
	private JTextArea xEbene = new JTextArea(6, 50);
	private JTextArea yEbene = new JTextArea(6, 50);
	private JTextArea zEbene = new JTextArea(6, 50);
	JTextField adresse= new JTextField(25); 
	String[] messungen = {"Rauschen", "Korrektheit"}; 
	JComboBox messarten = new JComboBox(messungen);
	JPanel pCenter = new JPanel(); 	
	
	//private DataService dataS = new DataService();
	
	public Gui(){
		//Fenster-Ereignisse zulassen
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	
	private void init(){
		//NorthPanel
		JPanel panelNorth = new JPanel();
		JLabel l0 = new JLabel("CSV-Dateipfad:");
		panelNorth.add(l0); 
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
		panelEast.add(messarten); 
		panelEast.add(b1);
		b1.setForeground(Color.GREEN);
		panelEast.add(b2);
		b2.setForeground(Color.RED);
		List list= new List(3);
		list.add("Mittelwert");
		list.add("Jitter");
		list.add("Korrektheit");
		panelEast.add(list);
		panelEast.add(b4); 
		
		//Connection with ActionListener
		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);
		messarten.addActionListener(this);
		list.addActionListener(this);
	
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.getContentPane();
		this.add(panelNorth, BorderLayout.NORTH);
		this.add(panelEast, BorderLayout.EAST);
		this.add(panelCenter, BorderLayout.CENTER);		
	}
	
		//close window
		protected void processWindowsEvent(WindowEvent e){
		super.processWindowEvent(e);
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				System.exit(0); //Prgm wird beendet
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
				
				Object src = e.getSource();
				File f;
				String path;
				//statt TestMethod dann loadNextData();
				String data = Test.testMethod(); 
				int z1 ;
				int z2 ;
				int z3 ;
			
				try{ 
					if(src == b3){ 
						f= new File(adresse.getText());
						path = f.getAbsolutePath();
						System.out.println(path);
						while (data !=null){  
							return;
						} 
					}else if(src ==b1){
							JOptionPane.showMessageDialog(null, "Jetzt das Gerät ruhig liegen lassen", "Warnung", JOptionPane.WARNING_MESSAGE);
							//Koordinatensystem einzeichnen

					}else if(src == b2){
						//in Feld reinschreiben 
						 z1 = Integer.parseInt(data);
						 z2 = Integer.parseInt(data);
						 z3 = Integer.parseInt(data);

						String resX = ""+z1;
						xEbene.setText(resX);
						String resY = ""+z2;
						yEbene.setText(resY);
						String resZ = ""+z3;
						zEbene.setText(resZ);
						
					}else if(src == messarten){
						if( src == "Korrektheit"){
						JButton starte2 = new JButton("Starte 2.Messung");
						pCenter.add(starte2);
						JButton ende2 = new JButton("Ende 2.Messung"); 
						pCenter.add(ende2);
						} 
					}else if(src == b4){
						JLabel berechne = new JLabel("Errechneter Wert");
						pCenter.add(berechne);
						JTextField tFeld = new JTextField(30); 
						pCenter.add(tFeld);	
					}
					
				}catch(Exception ep){
					System.out.println("Fehler");
				}					
			}	
}
