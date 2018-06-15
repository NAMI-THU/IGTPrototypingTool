package testInputOutput;

import inputOutput.Networkconnection;
import inputOutput.OpenIGTLinkConnection;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Start_Stop_IGTLink extends JFrame implements ActionListener {

	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	
	private JLabel lable = new JLabel("OPENIGTLINK CONNECTION");

	public Start_Stop_IGTLink() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();

	}

	private void init() {

		this.setLayout(null);
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(800, 300));
		

		lable.setBounds(250, 20, 300, 100);
		lable.setFont(new Font("Arial", Font.BOLD, 20));

		start.setBounds(200, 100, 100, 100);
		start.setFont(new Font("Arial", Font.BOLD, 20));
		start.addActionListener(this);
		
		stop.setBounds(500, 100, 100, 100);
		stop.setFont(new Font("Arial", Font.BOLD, 20));
		stop.addActionListener(this);
		
		
		

		this.add(lable);
		this.add(start);
		this.add(stop);

	}

	@Override
	public void actionPerformed(ActionEvent evt){
		Object src = evt.getSource();
		
		if(src==start){
			 Networkconnection begin = new  Networkconnection();
			 begin.start();
			
			
		}
		
		if(src==stop){
			inputOutput.Networkconnection.setBreak();
			
		}
		
		
	}
	
	public static void startIGTWindow(){
		Start_Stop_IGTLink frame;
		frame = new Start_Stop_IGTLink();
		frame.validate();
		frame.setVisible(true);
	}

	
}
