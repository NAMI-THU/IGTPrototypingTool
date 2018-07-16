package userinterface;

import algorithm.*;

import com.jme3.math.Quaternion;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.TextField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

import inputOutput.CSVFileReader;
import inputOutput.Networkconnection;
import inputOutput.OpenIGTLinkConnection;
import inputOutput.TrackingDataSource;

import com.sun.glass.events.KeyEvent;

import javax.swing.filechooser.FileFilter;

import java.net.*;

import inputOutput.*;
import testInputOutput.*;

public class MeasurementView extends JFrame implements ActionListener {
	private JButton start2 = new JButton("Start Measurement");
	private JButton finish2 = new JButton("End Measurement");
	private JButton loadData = new JButton("Load Data");
	private JButton calculate = new JButton("Calculate");
	private JButton loadTool = new JButton("Add Measurement");
	
	private Map<String,ToolMeasure> storedMeasurements = new LinkedHashMap<String,ToolMeasure>();
	private int measurementCounter = 0;
	
	private List<ToolMeasure> toolMeasures = new ArrayList<>();
	DataProcessor dataProcessor = new DataProcessor();
	
	private Timer timer;

	// Textfield for data source to load CSV- data:
	private JTextField adresse = new JTextField(25);
	// choose measurement:
	private String[] messungen = { "Rauschen", "Correctness" };
	private JComboBox measurementtyp = new JComboBox(messungen);
	private JCheckBox cBJitterP = new JCheckBox("Jitterposition", false);
	private JCheckBox cBJitterR = new JCheckBox("Jitterrotation", false);
	private JCheckBox cBCorrectnessR = new JCheckBox("Accuracy-Rotation", false);
	private JCheckBox cBCorrectnessP = new JCheckBox("Accuracy-Position", false);
	private JLabel lValue = new JLabel();
	private JLabel lCalcJR = new JLabel();
	private JLabel lCalcC = new JLabel();
	private JLabel lCalcJP = new JLabel();
	private static JLabel loaded = new JLabel();
	// Jfile Chooser
	private JTextField toLoadField = new JTextField(5);
	private JLabel toLoad = new JLabel();
	private JTextField distanceF = new JTextField(5);
	private JLabel distance = new JLabel();
	private JTextField rotationAngle = new JTextField();
	private JTextField rotationAngle1 = new JTextField();
	private JTextField rotationAngle2 = new JTextField();
	private JTextField rotationAngle3 = new JTextField();

	private JLabel rotationL = new JLabel();
	private JLabel rotationL1 = new JLabel();
	private JLabel rotationL2 = new JLabel();
	private JLabel rotationL3 = new JLabel();
	private JLabel rotationL4 = new JLabel();
	private boolean value = true;
	private static boolean testapp = false;
	private java.awt.List measurementList;

	TextField positionJitter = new TextField();
	File f;
	String valueP, valueD, valueL, valueR1, valueR2, valueR3, valueR4;
	double toR1, toR2, toR3, toR4;
	// double correctness, accuracy, jitterR, jitterP;
	// list for available tools
	private final java.awt.List toolList = new java.awt.List();
	private final Label label2 = new Label("Available Tools");
	
	TrackingDataSource source;

	DataService dataS = new DataService();
	
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	int toloadvalue;
	double toR, toD;
	private final JButton btnNewButton = new JButton("Search Data");

	public MeasurementView() {
		// allow window
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
	}
	
	public MeasurementView(TrackingDataSource source) {
		this.source = source;
		
		// allow window
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
		
		
		if(source != null)
		{
			dataS.setTrackingDataSource(source); 
			
			for(Tool t : source.update()){
				toolList.add(t.getName());
				System.out.println("Add tool: " + t.getName());
			}
		}
	}
	
	private void updateMeasurementList()
	{
		measurementList.removeAll();
		for (String n : storedMeasurements.keySet())
		{
			measurementList.add(n);
		}
	}

	private void init() {
		// register for searching OITG or CSV
		JTabbedPane tabbedPane = new JTabbedPane();
		start2.addActionListener(this);
		finish2.addActionListener(this);
		getContentPane().setLayout(new BorderLayout());
		this.getContentPane();
		getContentPane().add(tabbedPane);
		panel1 = new JPanel();
		tabbedPane.addTab("Measurement", panel1);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
				panel1.setLayout(null);
				
						JLabel l0 = new JLabel(" CSV-Datafile:");
						l0.setBounds(30, 107, 120, 20);
						panel1.add(l0);
						adresse.setBounds(200, 107, 318, 20);
						panel1.add(adresse);
						loadData.setBounds(528, 107, 120, 20);
						panel1.add(loadData);
								toLoad.setBounds(30, 40, 180, 25);
						
								toLoad.setText("Number of samples to load:");
								panel1.add(toLoad);
								toLoadField.setText("50");
								toLoadField.setBounds(200, 42, 318, 20);
								panel1.add(toLoadField);
										distance.setBounds(661, 347, 130, 20);
								
										distance.setText("Expected Distance");
										panel1.add(distance);
										distanceF.setBounds(797, 347, 80, 20);
										panel1.add(distanceF);
										distanceF.setEnabled(false);
												rotationL.setBounds(661, 378, 101, 20);
										
												rotationL.setText("Quaternion");
												panel1.add(rotationL);
														rotationL1.setBounds(777, 378, 15, 20);
												
														rotationL1.setText("x:");
														panel1.add(rotationL1);
														rotationAngle.setBounds(797, 378, 80, 20);
														panel1.add(rotationAngle);
																rotationL2.setBounds(777, 398, 15, 20);
														
																rotationL2.setText("y:");
																panel1.add(rotationL2);
																rotationAngle1.setBounds(797, 398, 80, 20);
																panel1.add(rotationAngle1);
																		rotationL3.setBounds(777, 418, 15, 20);
																
																		rotationL3.setText("z:");
																		panel1.add(rotationL3);
																		rotationAngle2.setBounds(797, 418, 80, 20);
																		panel1.add(rotationAngle2);
																				rotationL4.setBounds(777, 438, 15, 20);
																		
																				rotationL4.setText("r:");
																				panel1.add(rotationL4);
																				rotationAngle3.setBounds(797, 438, 80, 20);
																				panel1.add(rotationAngle3);
																				rotationAngle.setEnabled(false);
																				rotationAngle1.setEnabled(false);
																				rotationAngle2.setEnabled(false);
																				rotationAngle3.setEnabled(false);
																				
																						JLabel measuredTyp = new JLabel("Type of Measurement");
																						measuredTyp.setBounds(651, 316, 150, 20);
																						panel1.add(measuredTyp);
																						measurementtyp.setBounds(797, 314, 120, 25);
																						panel1.add(measurementtyp);
																								cBJitterR.setBounds(651, 540, 150, 30);
																								panel1.add(cBJitterR);
																								cBJitterP.setBounds(651, 560, 150, 30);
																								panel1.add(cBJitterP);
																								cBCorrectnessR.setBounds(651, 580, 150, 30);
																								panel1.add(cBCorrectnessR);
																								cBCorrectnessP.setBounds(651, 600, 150, 30);
																								panel1.add(cBCorrectnessP);
																								calculate.setBounds(807, 570, 130, 60);
																								panel1.add(calculate);
																										label2.setBounds(710, 40, 101, 20);
																										panel1.add(label2);
																										toolList.setBounds(817, 40, 120, 87);
																										panel1.add(toolList);
																										loadTool.setBounds(710, 137, 227, 23);
																										panel1.add(loadTool);
																														loaded.setText("<no data loaded>");
																														loaded.setBounds(200, 138, 318, 22);
																														panel1.add(loaded);
																														loadData.addActionListener(this);
																														calculate.addActionListener(this);
																														measurementtyp.addActionListener(this);
																														cBJitterR.addActionListener(this);
																														cBJitterP.addActionListener(this);
																														cBCorrectnessR.addActionListener(this);
																														cBCorrectnessP.addActionListener(this);
																														toLoadField.addActionListener(this);
																														distanceF.addActionListener(this);
																														rotationAngle.addActionListener(this);
																														rotationAngle1.addActionListener(this);
																														rotationAngle2.addActionListener(this);
																														rotationAngle3.addActionListener(this);
																														toolList.addActionListener(this);
																														loadTool.addActionListener(this);
																																
																																JLabel lblLoadDataFrom = new JLabel("Load data from file");
																																lblLoadDataFrom.setBounds(10, 11, 195, 14);
																																panel1.add(lblLoadDataFrom);
																																
																																JSeparator separator = new JSeparator();
																																separator.setBounds(20, 183, 1003, 2);
																																panel1.add(separator);
																																
																																JLabel lblCaptureContinousData = new JLabel("Capture continous data");
																																lblCaptureContinousData.setBounds(15, 196, 195, 14);
																																panel1.add(lblCaptureContinousData);
																																
																														
																																start2.setForeground(Color.GREEN);
																																start2.setBounds(210, 221, 150, 30);
																																panel1.add(start2);
																																
																															
																																finish2.setForeground(Color.RED);
																																finish2.setBounds(378, 221, 150, 30);
																																panel1.add(finish2);
																																
																																JSeparator separator_1 = new JSeparator();
																																separator_1.setBounds(20, 281, 1003, 2);
																																panel1.add(separator_1);
																																
																																JSeparator separator_2 = new JSeparator();
																																separator_2.setBounds(369, 511, 5, -115);
																																panel1.add(separator_2);
																																btnNewButton.addActionListener(new ActionListener() {
																																	public void actionPerformed(ActionEvent arg0) {
																																	}
																																});
																																btnNewButton.setBounds(528, 138, 120, 20);
																																
																																panel1.add(btnNewButton);
																																
																																measurementList = new java.awt.List();
																																measurementList.setBounds(30, 331, 554, 250);
																																panel1.add(measurementList);
																																
																																Label label = new Label("Captured Measurements");
																																label.setBounds(10, 305, 150, 20);
																																panel1.add(label);
	}

	// Label for recognizing, if data is loaded
	public static void setTexttoloaded() {
		loaded.setText("Data loaded");
		loaded.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
				
		try {
			// button loaddata pressed
			if (src == loadData) {

				// get path and handover to group 3
				f = new File(adresse.getText());
				String path = f.getAbsolutePath();
				if (f.exists() == true && path.endsWith(".csv")) {
					CSVFileReader newSource = new CSVFileReader();
					newSource.setPath(path);
					source = newSource;
					
				} else {
					JOptionPane.showMessageDialog(null, "Wrong data type!",
							"Warnung", JOptionPane.WARNING_MESSAGE);
				}
				

				// button start pressed
			} else if (src == start2) {

				JOptionPane.showMessageDialog(null,
						"Please hold tracking tool in fixed position.", "Attention!",
						JOptionPane.WARNING_MESSAGE);

				dataS = new DataService(source);
				dataS.getDataManager().setSource(source);
				if (timer==null)timer = new Timer(50, this);
		        timer.setInitialDelay(0);
		        timer.start();

				// button finish pressed
			} else if (src == finish2) {
					
				timer.stop();
				System.out.println("Stop!");
				storedMeasurements.put("Measurement " + measurementCounter + "("
								       + dataS.getDataManager().getToolMeasures().get(0).getName() 
								       + ")",dataS.getDataManager().getToolMeasures().get(0));
				this.updateMeasurementList();
				measurementCounter++;
				
				
				
			} 

			// if mesurementtyp pressed
			else if (src == measurementtyp) {
				String selected = (String) measurementtyp.getSelectedItem();
				if ("Correctness".equals(selected)) {
					distanceF.setEnabled(true);
					rotationAngle.setEnabled(true);
					rotationAngle1.setEnabled(true);
					rotationAngle2.setEnabled(true);
					rotationAngle3.setEnabled(true);
				}
				// button sough pressed
				if ("Rauschen".equals(selected)) {
					
					distanceF.setEnabled(false);
					rotationAngle.setEnabled(false);
					rotationAngle1.setEnabled(false);
					rotationAngle2.setEnabled(false);
					rotationAngle3.setEnabled(false);

				}

				
			} 
			
			else if (src == calculate) // button calculate pressed
			{

				System.out.println("Computing results");
				ToolMeasure tool = (ToolMeasure) storedMeasurements.values().toArray()[0]; 
				System.out.println("name: "+tool.getName());
				System.out.println("size: "+tool.getMeasurement().size());
				AverageMeasurement avgMes = tool.getAverageMeasurement();
				/*
				for (Measurement m : tool.getMeasurement())
				{
					System.out.println("Captured value: " + m.getPoint());
				}
				System.out.println("jitter: "+avgMes.getJitter());
				*/

				lValue.setText("Calculated Value");
				lValue.setBounds(650, 510, 130, 30);
				panel1.add(lValue);
				lValue.setForeground(Color.BLUE);

				lCalcJR.setBounds(650, 540, 380, 30);
				panel1.add(lCalcJR);
				lCalcC.setBounds(650, 580, 280, 30);
				panel1.add(lCalcC);
				lCalcJP.setBounds(650, 620, 280, 40);
				panel1.add(lCalcJP);

				// JCheckBox cBJitterR pressed
				if (cBJitterR.isSelected()) {
					lCalcJR.setText("0,00");
					lCalcJR.setText(String.valueOf(avgMes.getRotationError()));

				}
				// JCheckBox cBJitterP pressed
				if (cBJitterP.isSelected()) {
					lCalcJP.setText("0,00");
					lCalcJP.setText(String.valueOf(avgMes.getJitter()));

				}
				// JChekBox cBCorrectnessR pressed
				if (cBCorrectnessR.isSelected()) {

					valueR1 = rotationAngle.getText();
					toR1 = Double.parseDouble(valueR1);

					valueR2 = rotationAngle1.getText();
					toR2 = Double.parseDouble(valueR2);
					;

					valueR3 = rotationAngle2.getText();
					toR3 = Double.parseDouble(valueR3);

					valueR4 = rotationAngle3.getText();
					toR4 = Double.parseDouble(valueR4);

					Quaternion expectedrotation = new Quaternion().set(
							(float) toR1, (float) toR2, (float) toR3,
							(float) toR4);
					
					ToolMeasure firstTool = (ToolMeasure)storedMeasurements.values().toArray()[0];
					ToolMeasure secondTool = (ToolMeasure)storedMeasurements.values().toArray()[1];
					
					lCalcJR.setText(String.valueOf(dataS.getAccuracyRotation(
							expectedrotation, firstTool.getMeasurement().get(0),secondTool.getMeasurement().get(0))));

				}
				// JChekBox cBCorrectnessP pressed
				if (cBCorrectnessP.isSelected()) {
					valueD = distanceF.getText();
					toD = Double.parseDouble(valueD);
					lCalcC.setText("0,00");
					
					ToolMeasure firstTool = null;
					ToolMeasure secondTool = null;
					AverageMeasurement avgMes1 = new AverageMeasurement();
					AverageMeasurement avgMes2 = new AverageMeasurement();
					for (ToolMeasure m : storedMeasurements.values())
						{
						/*
						if (firstTool == null) 
							{
							firstTool = m;
							System.out.println("Tool1:" + firstTool.getName());
							avgMes1 = dataProcessor.getAverageMeasurement(firstTool.getMeasurement());
							}
						else if (secondTool==null) 
							{
							secondTool = m;
							System.out.println("Tool2:" + secondTool.getName());
							avgMes2 = dataProcessor.getAverageMeasurement(secondTool.getMeasurement());
							}*/
						System.out.println("Tool:"+m.getName());
						
						}
					
								
					System.out.println("Avgmes1:" + avgMes1.getPoint());
					System.out.println("Avgmes2:" + avgMes2.getPoint());
					
					lCalcJP.setText(String.valueOf(dataS.getAccuracy(toD,
							avgMes1,
							avgMes2)));

				}

			} 
			
			else if (src == loadTool) // loadData is pressed
			{

				valueL = toLoadField.getText();
				toloadvalue = Integer.parseInt(valueL);
				
				toolMeasures = dataS.loadNextData(toloadvalue);
				
				
				for (ToolMeasure tm : toolMeasures) {
					toolList.add(tm.getName());
				}
				
			} 
			
			else if (src == timer) //timer event
			{
				dataS.getDataManager().getNextData(1);
				System.out.println("Capturing data...");
				
			}
			// Exception-Window

		} catch (

		Exception ep) {
			if (f!=null && (f.exists() == false)) {
				JOptionPane.showMessageDialog(null,
						"Dateipfad existiert nicht", "Fenstertitel",
						JOptionPane.ERROR_MESSAGE);
			} else {

				JOptionPane.showMessageDialog(null, "Fehlermeldung",
						"Fenstertitel", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// close window
	protected void processWindowsEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			System.exit(0); // Prgm wird beendet
		}
	}
}
