package userinterface;

import algorithm.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.TextField;
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

import inputOutput.Networkconnection;
import inputOutput.OpenIGTLinkConnection;

import com.sun.glass.events.KeyEvent;

import javax.swing.filechooser.FileFilter;
import java.net.*;
import inputOutput.*;
import testInputOutput.*;

public class Gui extends JFrame implements ActionListener {
	// Declarations of Buttons for measurements, loading data, compute:
	private JButton start = new JButton("Start Measurement");
	private JButton finish = new JButton("End Measurement");
	private JButton start2 = new JButton("Start 2.Measurement");
	private JButton finish2 = new JButton("End 2.Measurement");
	private JButton loadData = new JButton("Load Data");
	private JButton calculate = new JButton("Calculate");
	private JButton loadTool = new JButton("Tool");
	private JButton restart = new JButton("Load New Data");

	// Textfield for data source to load CSV- data:
	private JTextField adresse = new JTextField(25);
	private JTextField ValueData = new JTextField(15);
	// choose measurement:
	private String[] messungen = { "Rauschen", "Correctness" };
	private JComboBox measurementtyp = new JComboBox(messungen);
	private JCheckBox cBJitterP = new JCheckBox("Jitterposition", false);
	private JCheckBox cBJitterR = new JCheckBox("Jitterrotation", false);
	private JCheckBox cBCorrectnessR = new JCheckBox("Accuracy-Rotation", false);
	private JCheckBox cBCorrectnessP = new JCheckBox("Accuracy-Position", false);
	// Checkbox for Jitter, correctness, accuracy, rotation
	private JLabel LabelDataValue = new JLabel();
	private JLabel openITGL = new JLabel();
	private JLabel lValue = new JLabel();
	private JLabel lCalcJR = new JLabel();
	private JLabel lCalcC = new JLabel();
	private JLabel lCalcJP = new JLabel();
	private static JLabel loaded = new JLabel();
	//Label
	private JMenuBar bar;
	private JMenu menu;
	private JMenuItem openItem;
	private JMenuItem closeItem;
	// Jfile Chooser
	private JTextField toLoadField = new JTextField(5);
	private JLabel toLoad = new JLabel();
	private JTextField distanceF = new JTextField(5);
	private JLabel distance = new JLabel();
	private JTextField rotationAngel = new JTextField();
	private JTextField rotationAngel1 = new JTextField();
	private JTextField rotationAngel2 = new JTextField();
	private JTextField rotationAngel3 = new JTextField();
	
	private JLabel rotationL = new JLabel();
	private JLabel rotationL1 = new JLabel();
	private JLabel rotationL2 = new JLabel();
	private JLabel rotationL3 = new JLabel();
	private JLabel rotationL4 = new JLabel();
	
	private JLabel openIGTl = new JLabel();
	//Textfield, Label
	private JButton exit_connection = new JButton("Exit Connection");
	private boolean value = true;
	private static boolean testapp = false;
	
	TextField positionJitter = new TextField();
	File f;
	String valueP, valueD, valueL;
	double valueR1,valueR2, valueR3, valueR4;  
	// double correctness, accuracy, jitterR, jitterP;
	// list for available tools
	private final java.awt.List toolList = new java.awt.List();
	private final Label label2 = new Label("Available Tools");

	DataService dataS = new DataService();
	DataProcessor dataP = new DataProcessor();
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	int toloadvalue;
	double toR, toD;

	public Gui() {
		// allow window
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		init();
	}

	private void init() {
		// register for searching OITG or CSV
		JTabbedPane tabbedPane = new JTabbedPane();
		panel1 = new JPanel();
		panel2 = new JPanel();
		tabbedPane.addTab("CSV", panel1);
		tabbedPane.addTab("OpenIGTLink", panel2);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		// Searching for data file
		bar = new JMenuBar();
		menu = new JMenu("Search for Data ");
		openItem = new JMenuItem("Open");
		closeItem = new JMenuItem("Close");
		menu.add(openItem);
		menu.add(closeItem);
		bar.add(menu);
		bar.setBounds(5, 20, 120, 20);
		panel1.add(bar);
		
		JLabel lblOpenIgtlink = new JLabel("Open IGTLINK"); //Connection for OPENIGTLink
		lblOpenIgtlink.setForeground(Color.RED);
		lblOpenIgtlink.setBounds(350, 10, 120, 40);
		panel2.add(lblOpenIgtlink);
		
		JLabel l0 = new JLabel(" CSV-Datafile:"); // Label for CSV file path
		l0.setBounds(20, 40, 120, 20);
		panel1.add(l0);
		adresse.setBounds(210, 40, 250, 20);
		panel1.add(adresse);
		loadData.setBounds(460, 40, 120, 20);
		panel1.add(loadData);

		toLoad.setText("Number of files to load"); // number of files to load
		toLoad.setBounds(20, 140, 180, 25);
		panel1.add(toLoad);
		toLoadField.setBounds(210, 140, 180, 20);
		panel1.add("n", toLoadField);

		distance.setText("Distant indication");// distance indication
		distance.setBounds(20, 240, 130, 20);
		panel1.add(distance);
		distanceF.setBounds(210, 240, 180, 20);
		panel1.add(distanceF);

		rotationL.setText("Quaternion-Angabe"); // corner
		rotationL.setBounds(20, 340, 170, 20);
		panel1.add(rotationL);
		
		rotationL1.setText("x:"); 
		rotationL1.setBounds(200, 340, 15, 20);
		panel1.add(rotationL1);
		rotationAngel.setBounds(220, 340, 80, 20);
		panel1.add(rotationAngel);
		
		rotationL2.setText("y:"); 
		rotationL2.setBounds(200, 360, 15, 20);
		panel1.add(rotationL2);
		rotationAngel1.setBounds(220, 360, 80, 20);
		panel1.add(rotationAngel1);
	
		rotationL3.setText("z:"); 
		rotationL3.setBounds(200, 380, 15, 20);
		panel1.add(rotationL3);
		rotationAngel2.setBounds(220, 380, 80, 20);
		panel1.add(rotationAngel2);
		
		rotationL4.setText("r:"); 
		rotationL4.setBounds(200, 400, 15, 20);
		panel1.add(rotationL4);
		rotationAngel3.setBounds(220, 400, 80, 20);
		panel1.add(rotationAngel3);
		
		JLabel measuredTyp = new JLabel("Measurementtyp"); //measuredtyp
		measuredTyp.setBounds(700, 72, 150, 20);
		panel2.add(measuredTyp);
		measurementtyp.setBounds(700, 120, 120, 25);
		panel2.add(measurementtyp);
		start.setBounds(200, 72, 150, 30); // setbounds for position
		panel2.add(start);
		start.setForeground(Color.GREEN); // set button "start" green
		finish.setBounds(400, 72, 150, 30);
		panel2.add(finish);
		finish.setForeground(Color.RED); // set button "finish" red
		
		cBJitterR.setBounds(650, 400, 150, 30);
		panel1.add(cBJitterR);
		cBJitterP.setBounds(650, 420, 150, 30);
		panel1.add(cBJitterP);
		cBCorrectnessR.setBounds(650, 440, 150, 30);
		panel1.add(cBCorrectnessR);
		cBCorrectnessP.setBounds(650, 460, 150, 30);
		panel1.add(cBCorrectnessP);
		calculate.setBounds(800, 400, 130, 60); // set bounds for calculate
		panel1.add(calculate);

		toolList.setBounds(20, 500, 120, 80);
		panel1.add(toolList);
		loadTool.setBounds(210, 500, 150, 25);
		panel1.add(loadTool);

		exit_connection.setBounds(350, 200, 160, 20);
		panel2.add(exit_connection);
		
		restart.setBounds(460,600, 150,30);
		panel1.add(restart);
		
		loaded.setBounds(600, 40, 120, 25);
		panel1.add(loaded);
		
		// Connection with ActionListener
		start.addActionListener(this);
		finish.addActionListener(this);
		loadData.addActionListener(this);
		calculate.addActionListener(this);
		measurementtyp.addActionListener(this);
		start2.addActionListener(this);
		finish2.addActionListener(this);
		cBJitterR.addActionListener(this);
		cBJitterP.addActionListener(this);
		cBCorrectnessR.addActionListener(this);
		cBCorrectnessP.addActionListener(this);
		toLoadField.addActionListener(this);
		distanceF.addActionListener(this);
		rotationAngel.addActionListener(this);
		rotationAngel1.addActionListener(this);
		rotationAngel2.addActionListener(this);
		rotationAngel3.addActionListener(this);
		toolList.addActionListener(this);
		loadTool.addActionListener(this);
		exit_connection.addActionListener(this);
		restart.addActionListener(this);

		panel1.setLayout(null);
		panel2.setLayout(null);
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); // for closing
		this.getContentPane();
		this.add(tabbedPane);

		openItem.addActionListener(new java.awt.event.ActionListener() {
			// opens actionPerformed by clicking openItem
			public void actionPerformed(java.awt.event.ActionEvent e) {

				FileFilter filter = new FileNameExtensionFilter("Testreihe", "csv");
				JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				fc.addChoosableFileFilter(filter);
				int returnValue = fc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selctedFile = fc.getSelectedFile();
					String path2 = selctedFile.getAbsolutePath();
					CSVFileReader.setPath(path2);
					
				}
			}
		});
	}
	
	public static void setTexttoloaded(){
		loaded.setText("Data loaded");
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		String path;
		algorithm.DataManager data = new algorithm.DataManager();
		Networkconnection begin = new Networkconnection();

		List<ToolMeasure> toolMeasures = dataS.loadNextData(toloadvalue);
		List<ToolMeasure> firstMeasurement = toolMeasures;
		List<ToolMeasure> secondMeasurement = toolMeasures;

		try {
			if (src == loadData) {

				f = new File(adresse.getText());
				path = f.getAbsolutePath();
				if (f.exists() == true && path.endsWith(".csv")) {
					CSVFileReader.setPath(path);

				} else {
					JOptionPane.showMessageDialog(null, "Ungueltiger Dateityp", "Warnung", JOptionPane.WARNING_MESSAGE);
				}
			} else if (src == start || src == start2) {

				JOptionPane.showMessageDialog(null, "Jetzt das Geraet ruhig liegen lassen", "Warnung",
						JOptionPane.WARNING_MESSAGE);

				if (value == true) {
					value = false;
					begin.start();

				} else if (value == false) {
					begin.setBreak(true);
				}

			} else if (src == finish || src == finish2) {
				begin.setBreak(false);
				data.setCount();
			} else if (src == exit_connection) {
				value = true;
				begin.setExit(false);
				data.setCount();
				testInputOutput.Networkconnection_test_app.setCount();
			}

			else if (src == measurementtyp) {
				String selected = (String) measurementtyp.getSelectedItem();
				if ("Correctness".equals(selected)) {
					distanceF.setEnabled(true);
					rotationAngel.setEnabled(true);
					start2.setBounds(200, 110, 150, 30);
					panel2.add(start2);
					start2.setForeground(Color.GREEN);
					start2.setEnabled(true);
					finish2.setBounds(400, 110, 150, 30);
					panel2.add(finish2);
					finish2.setForeground(Color.RED);
					finish2.setEnabled(true);

				}
				if ("Rauschen".equals(selected)) {
					start2.setEnabled(false);
					finish2.setEnabled(false);
					distanceF.setEnabled(false);
					rotationAngel.setEnabled(false);
				}

			} else if (src == calculate) {

				ToolMeasure tool = dataS.getToolByName(toolList.getSelectedItem());
				AverageMeasurement avgMes = tool.getAverageMeasurement();

				lValue.setText("Calculatet Value");
				lValue.setBounds(650, 510, 130, 30);
				panel1.add(lValue);
				lValue.setForeground(Color.BLUE);

				lCalcJR.setBounds(650, 540, 200, 30);
				panel1.add(lCalcJR);
				lCalcC.setBounds(650, 580, 200, 30);
				panel1.add(lCalcC);
				lCalcJP.setBounds(650, 620, 200, 40);
				panel1.add(lCalcJP);

				if (cBJitterR.isSelected()) {
					lCalcJR.setText("0,00");
					lCalcJR.setText(String.valueOf(avgMes.getRotationError()));

				}
				if (cBJitterP.isSelected()) {
					lCalcJP.setText("0,00");
					lCalcJP.setText(String.valueOf(avgMes.getError()));

				}
				if (cBCorrectnessR.isSelected()) {
					
					String str = String.valueOf(valueR1);
					str = rotationAngel.getText();
					
					String str2 = String.valueOf(valueR2);
					str2 = rotationAngel1.getText();
					
					String str3 = String.valueOf(valueR3);
					str3 = rotationAngel.getText();
					
					String str4 = String.valueOf(valueR4);
					str4 = rotationAngel.getText();
					
					lCalcC.setText("0,00");
					lCalcC.setText(String
							.valueOf(dataS.getAccuracyRotation(str, str2, str3, str4, firstMeasurement.get(0).getMeasurement().get(0),
									secondMeasurement.get(0).getMeasurement().get(0))));

				}
				if (cBCorrectnessP.isSelected()) {
					valueD = distance.getText();
					toD = Double.parseDouble(valueD);
					lCalcC.setText("0,00");
					lCalcC.setText(
							String.valueOf(dataS.getAccuracy(toD, firstMeasurement.get(0).getAverageMeasurement(),
									secondMeasurement.get(0).getAverageMeasurement())));

				}
			} else if (src == loadTool) {
				valueL = toLoadField.getText();
				toloadvalue = Integer.parseInt(valueL);

				for (ToolMeasure tm : toolMeasures) {
					toolList.add(tm.getName());
					firstMeasurement = toolMeasures;
					if (toolMeasures.contains(firstMeasurement)) {
						toolMeasures.clear();

						for (ToolMeasure tm2 : toolMeasures) {
							toolList.add(tm2.getName());
							secondMeasurement = toolMeasures;
						}
					}

				}
			}
			
		} catch (Exception ep) {
			if (f.exists() == false) {
				JOptionPane.showMessageDialog(null, "Dateipfad existiert nicht", "Fenstertitel",
						JOptionPane.ERROR_MESSAGE);
			} else {

				JOptionPane.showMessageDialog(null, "Fehlermeldung", "Fenstertitel", JOptionPane.ERROR_MESSAGE);
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
