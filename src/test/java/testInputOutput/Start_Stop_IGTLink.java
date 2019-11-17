package testInputOutput;

import inputOutput.CSVFileReader;

import javax.swing.Timer;

import inputOutput.Networkconnection;
import inputOutput.OpenIGTLinkConnection;
import inputOutput.Tool;
import inputOutput.TrackingDataSource;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class Start_Stop_IGTLink extends JFrame implements ActionListener {

	private boolean value = true;
	private static boolean testapp = false;
	Networkconnection begin;

	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JButton close = new JButton("Close Application");
	private JButton exit_connection = new JButton("Exit Connection");

	private JLabel lable = new JLabel("Simple Test Application");
	private JTextField ipAdress;
	private JTextField filename;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	JRadioButton rdbtnCsvFileReader;
	JRadioButton rdbtnOpenIgtLink;
	
	private Timer timer;

	public Start_Stop_IGTLink() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();

	}

	private void init() {

		getContentPane().setLayout(null);
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(800, 600));

		lable.setBounds(250, 20, 300, 100);
		lable.setFont(new Font("Arial", Font.BOLD, 20));

		start.setBounds(121, 221, 200, 100);
		start.setFont(new Font("Arial", Font.BOLD, 20));
		start.addActionListener(this);

		stop.setBounds(488, 221, 200, 100);
		stop.setFont(new Font("Arial", Font.BOLD, 20));
		stop.addActionListener(this);

		close.setBounds(488, 369, 200, 100);
		close.setFont(new Font("Arial", Font.BOLD, 20));
		close.addActionListener(this);

		exit_connection.setBounds(121, 369, 200, 100);
		exit_connection.setFont(new Font("Arial", Font.BOLD, 20));
		exit_connection.addActionListener(this);

		getContentPane().add(exit_connection);
		getContentPane().add(close);
		getContentPane().add(lable);
		getContentPane().add(start);
		getContentPane().add(stop);
		
		ipAdress = new JTextField();
		ipAdress.setText("127.0.0.1");
		ipAdress.setBounds(410, 151, 284, 20);
		getContentPane().add(ipAdress);
		ipAdress.setColumns(10);
		
		filename = new JTextField();
		filename.setBounds(410, 111, 284, 20);
		getContentPane().add(filename);
		filename.setColumns(10);
		
		rdbtnCsvFileReader = new JRadioButton("CSV File Reader");
		buttonGroup.add(rdbtnCsvFileReader);
		rdbtnCsvFileReader.setSelected(true);
		rdbtnCsvFileReader.setBounds(90, 110, 109, 23);
		getContentPane().add(rdbtnCsvFileReader);
		
		rdbtnOpenIgtLink = new JRadioButton("Open IGT Link Connection");
		buttonGroup.add(rdbtnOpenIgtLink);
		rdbtnOpenIgtLink.setBounds(90, 150, 174, 23);
		getContentPane().add(rdbtnOpenIgtLink);
		
		JLabel lblFilename = new JLabel("Filename:");
		lblFilename.setBounds(341, 114, 59, 14);
		getContentPane().add(lblFilename);
		
		JLabel lblIpAdress = new JLabel("IP Adress:");
		lblIpAdress.setBounds(341, 154, 59, 14);
		getContentPane().add(lblIpAdress);

	}

	private TrackingDataSource source;
	
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		algorithm.DataManager data = new algorithm.DataManager();

		if (src == start) {

			if(rdbtnCsvFileReader.isSelected())
			{
			CSVFileReader newSource = new CSVFileReader();
			newSource.setPath(filename.getText());
			newSource.setRepeatMode(true);
			source = newSource;
			}
			else
			{
			OpenIGTLinkConnection newSource = new OpenIGTLinkConnection();
			newSource.setIpAddress(this.ipAdress.getText());
			newSource.update();
			source = newSource;
			}
			
			//Set up timer to drive animation events.
	        timer = new Timer(50, this);
	        timer.setInitialDelay(0);
	        timer.start();
	        
	      
		}

		else if (src == stop) {

			timer.stop();
			
		}

		else if (src == close) {
			System.exit(0);
		}

		else if (src == exit_connection) {
			value = true;
			if (this.rdbtnOpenIgtLink.isSelected()) ((OpenIGTLinkConnection)source).closeConnection();
		}
		
		else
		{
			ArrayList<Tool> tools = source.update();
			System.out.print("Data: ");
			for (Tool t : tools) System.out.print("{"+t.getName()+":"
													 +t.getCoordinat().getX()+";"
													 +t.getCoordinat().getY()+";"
													 +t.getCoordinat().getZ()+"}");
			System.out.println();
		}
	}

	public static void setTestappValue() {
		testapp = true;
	}

	public static void startIGTWindow() {
		Start_Stop_IGTLink frame;
		frame = new Start_Stop_IGTLink();
		frame.validate();
		frame.setVisible(true);
	}
}
