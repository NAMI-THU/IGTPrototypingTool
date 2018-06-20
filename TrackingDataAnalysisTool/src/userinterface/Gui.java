package userinterface;

import algorithm.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;//import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import java.net.*;
import inputOutput.*;  


public class Gui extends JFrame implements ActionListener{
	// Declarations of Buttons for measurements, loading data, compute:
	private JButton start = new JButton("Starte Messung");
	private JButton finish = new JButton("Beende Messung");
	private JButton start2 = new JButton("Starte 2.Messung");
	private JButton finish2 = new JButton("Ende 2.Messung"); 
	private JButton loadData = new JButton("Lade Datei");
	private JButton calculate = new JButton("Berechne");
	
	// Textfield for data source to load CSV- data:
	private JTextField adresse= new JTextField(25); 
	
	// choose measurement:
	private String[] messungen = {"Rauschen", "Korrektheit"}; 
	private JComboBox messarten = new JComboBox(messungen);
	
	// Checkbox for Jitter, correctness, accuracy, rotation
	private JCheckBox cBJitterP = new JCheckBox("Jitterposition", false);
	private JCheckBox cBJitterR = new JCheckBox("Jitterrotation", false);
	private JCheckBox cBCorrectness = new JCheckBox("Korrektheit", false);
	private JCheckBox cBAccuracy = new JCheckBox("Genauigkeit", false);
	private JCheckBox cBRotation = new JCheckBox("Rotation", false);
	
	//Label
	JLabel distanz = new JLabel(); JPanel pCenter = new JPanel();
	JLabel xAxis = new JLabel(); JLabel zAxis = new JLabel();JLabel yAxis = new JLabel();
	JLabel LabelDataValue = new JLabel(); private JTextField ValueData = new JTextField(15);
	
	//Jfile Chooser
	JMenuBar bar; JMenu menu ; 
	JMenuItem openItem; JMenuItem closeItem;
	
	//Label
	JLabel lValue = new JLabel(); JLabel lCalcJ = new JLabel();  
	JLabel lCalcC = new JLabel(); JLabel lCalcA = new JLabel();  
	File f; String valueL, valueD;  
	double correctness, accuracy, jitterR, jitterP;
	
	
	//Textfield, Label
	JTextField toLoadField = new JTextField(5); JLabel toLoad = new JLabel();
	JTextField distanceF = new JTextField(5); JLabel distance = new JLabel();
	JTextField rotationAngel = new JTextField(5); JLabel rotationL = new JLabel();
	JTextField openIGTf = new JTextField(25); JLabel openIGTl = new JLabel();
	
	// Button for connection to openIGTB
	JButton openIGTB = new JButton("Connect");
	
	// Tool Measurement first and second
	ToolMeasure firstMeasurement; ToolMeasure secondMeasurement;
	ToolMeasure firstAverageMeasurement; ToolMeasure secondAverageMeasurement;
	
	// Data Service, Data Processor Object
	DataService dataS = new DataService();
	DataProcessor dataP = new DataProcessor();
	
	
	
	public Gui(){
		//allow window
		enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
		init();	
	}
	
	
	//
	private void init(){
		//NorthPanel
		JPanel panelNorth = new JPanel();
		//Searching for data file
		bar = new JMenuBar();
		menu = new JMenu("Dateisuche ");
		openItem = new JMenuItem("Oeffnen");
	    closeItem = new JMenuItem("Schliessen"); 
	    menu.add(openItem);
	    menu.add(closeItem);
	    bar.add(menu);
	    panelNorth.add(bar);
	    
	 	JLabel l0 = new JLabel(" CSV-Dateipfad:");//  Label for CSV file path
		panelNorth.add(l0); 
		panelNorth.add(adresse);
		panelNorth.add(loadData);
		openIGTf.setBounds(120, 120, 120, 90);
		add(openIGTf);
		openIGTl.setBounds(90, 120, 120, 90);
		add(openIGTl);
		openIGTB.setBounds(250, 120, 120, 90);
		add(openIGTB); 
		
		toLoad.setText("Anzahl der zu ladende Dateien"); //Number of files to load
		panelNorth.add(toLoad);
		valueL = toLoadField.getText(); 
		panelNorth.add("n", toLoadField);
		distance.setText("Distanzangabe");// distance indication
		distance.setBounds(1000, 170, 120, 20);
		rotationAngel.setText("Winkel");//corner
		rotationAngel.setBounds(1000, 450, 120, 20);
		LabelDataValue.setBounds(650,300, 120, 80);
		add(ValueData);
		
		JLabel measuredTyp = new JLabel("Messarten"); //measuredtyp
		measuredTyp.setBounds(650, 80, 120, 60);
		add(measuredTyp);
		messarten.setEditable(true);
		messarten.setBounds(800, 100, 120, 20);// setBounds for position measured typ
		add(messarten); 
		start.setBounds(650, 170, 130, 60);
		add(start);
		start.setForeground(Color.GREEN); // set button "start" green
		finish.setBounds(800, 170, 130, 60);
		add(finish);
		finish.setForeground(Color.RED); // set button "finish" red
		
		// setbounds for cBJitter,cBJitterP, cBCorrectness, cBAccuracy
		cBRotation.setBounds(800, 80, 120, 60);
		add(cBRotation);
		cBJitterR.setBounds(650, 380, 150, 30);
		add(cBJitterR);
		cBJitterP.setBounds(650, 400, 150, 30);
		cBCorrectness.setBounds(650, 420, 150, 30);
		cBAccuracy.setBounds(650, 440, 150, 30);
		
		// adding cBJitter, cBCorrectness, cBAccuracy
		add(cBJitterP);
		add(cBCorrectness);
		add(cBAccuracy);
		setLayout(null); //null-layout
		calculate.setBounds(800, 400, 130, 60);// setBounds for calculate
		add(calculate);

		//Connection with ActionListener
		start.addActionListener(this);
		finish.addActionListener(this);
		loadData.addActionListener(this);
		calculate.addActionListener(this);
		messarten.addActionListener(this);
		start2.addActionListener(this);
		finish2.addActionListener(this);
		cBJitterR.addActionListener(this);
		cBJitterP.addActionListener(this);
		cBCorrectness.addActionListener(this);
		cBAccuracy.addActionListener(this);
		toLoadField.addActionListener(this);
		distanceF.addActionListener(this);
		rotationAngel.addActionListener(this);
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);// for closing
		this.getContentPane();
		this.add(panelNorth, BorderLayout.NORTH);
	
		//Open with ActionsListener
		openItem.addActionListener(new java.awt.event.ActionListener() {
	        //opens actionPerformed by clicking openItem
	        public void actionPerformed(java.awt.event.ActionEvent e) {
	      
	            FileFilter filter = new FileNameExtensionFilter("Testreihe", "csv");
				JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				fc.addChoosableFileFilter(filter);
				int returnValue = fc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION){
				    File selctedFile = fc.getSelectedFile();
				    String path2 = selctedFile.getAbsolutePath();
				    CSVFileReader.setPath(path2);     
				}
		        }});}

		public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				FileFilter filter; 
				String path, valueD, valueR ;  
				AverageMeasurement aM2; 
				Diagramm Diag = new Diagramm();
				DataProcessor dP = new DataProcessor(); 
				
				try{ 
					//button loaddata pressed 
					if(src == loadData){ 
						f = new File(adresse.getText());
						path = f.getAbsolutePath();
						if( f.exists()== true && path.endsWith(".csv")){
							CSVFileReader.setPath(path);
						
						}else{
							JOptionPane.showMessageDialog(null, "Ungueltiger Dateityp", 
									"Warnung", JOptionPane.WARNING_MESSAGE);
						}
						//button start pressed 	
					}else if(src == start || src == start2 ){
						JOptionPane.showMessageDialog(null, "Jetzt das Gerät ruhig liegen lassen", "Warnung", JOptionPane.WARNING_MESSAGE);	
						
					//button finish pressed 
					}else if(src == finish || src == finish2 ){
					//	Diag.start(stage);
						
					//button measured species pressed 		
					}else if(src == messarten){
						String selected = (String) messarten.getSelectedItem();
						//button correctness pressed
						if("Korrektheit".equals(selected)){
							distanz.setText("Zu erwartende Distanz");
							distanz.setBounds(800, 120, 100, 20);
							add(distanz);
							valueD = distanz.getText(); 
							start2.setBounds(650, 280, 130, 60);
							add(start2);
							start2.setForeground(Color.GREEN);
							start2.setEnabled(true);
							finish2.setBounds(800, 280, 130, 60);
							add(finish2);
							finish2.setForeground(Color.RED);
							finish2.setEnabled(true);
						//button sough pressed	
						}if("Rauschen".equals(selected)){							
							start2.setEnabled(false);
							finish2.setEnabled(false);
						}
					//button calculate pressed
					}else if(src == calculate){
						/*
						for(int i =0 ; i< toolMeas.size(); i++){
							ToolMeasure tool = toolMeas.get(i); 
								tool.getMeasurement(); 
								AverageMeasurement aM = tool.getAverageMeasurement(); 
							jitter = aM.getError();
						    boxplot = aM.getBoxPlot(); 
						    firstMeas = aM.g;
						}
						/*
						List<Measurement> mesL = toolMeasure.getMeasurement();
						AverageMeasurement avgMes = dataProcessor.getAverageMeasurement(mesL);
						dataS.getAccuracyRotation(valueR, firstMeasurement.getMeasurement(), secondMeasurement.getMeasurement()); 
						dataS.getAccuracy(valueD, firstAverageMeasurement.getAverageMeasurement(), secondAverageMeasurement.getAverageMeasurement()) ;
						*/
						
						lValue.setText("Errechneter Wert");
						lValue.setBounds(650, 510, 130, 30);
						add(lValue);
						lValue.setForeground(Color.BLUE);
						//validate();
						lCalcJ.setBounds(800, 510, 200, 30);
						add(lCalcJ);
						lCalcC.setBounds(800, 540, 200, 30);
						add(lCalcC);
						lCalcA.setBounds(800, 570, 200, 40);
						add(lCalcA);
						//JCheckBox cBJitterR pressed
						if(cBJitterR.isSelected()){
							lCalcJ.setText(" Jitter = " +jitterR );
						//JCheckBox cBJitterP pressed
						}if(cBJitterP.isSelected()){
							lCalcJ.setText(" Jitter = " +jitterP ); 
						//JCheckBox cBCorrectness pressed
						}if(cBCorrectness.isSelected()){
							lCalcC.setText(" Korrektheit  = ");  
						//	JCheckBox cBRotation pressed
						}if(cBRotation.isSelected()){
							valueR = rotationAngel.getText();  
						//button openIGTB pressed
						}if(openIGTB.isSelected()){
							new testInputOutput.Start_Stop_IGTLink.startIGTWindow();		
						}	
						
					} 	
				}catch(Exception ep){
					if(f.exists()==false){
						JOptionPane.showMessageDialog(null,"Dateipfad existiert nicht",
								"Fenstertitel",JOptionPane.ERROR_MESSAGE);
					}else{
						//ExceptionReporting.registerExceptionReporter();
						JOptionPane.showMessageDialog(null,"Fehlermeldung","Fenstertitel",JOptionPane.ERROR_MESSAGE);
					} 

				}					
			};	
			//close window
				protected void processWindowsEvent(WindowEvent e){
				super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						System.exit(0); //Prgm wird beendet
					}
				}

		
}
