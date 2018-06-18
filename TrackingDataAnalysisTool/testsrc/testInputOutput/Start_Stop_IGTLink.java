package testInputOutput;

import inputOutput.Networkconnection;
import inputOutput.OpenIGTLinkConnection;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Start_Stop_IGTLink extends JFrame implements ActionListener {

	private boolean value = true;
	Networkconnection begin;

	private JButton start = new JButton("Start");
	private JButton stop = new JButton("Stop");
	private JButton close = new JButton("Close Application");
	private JButton exit_connection = new JButton("Exit Connection");

	private JLabel lable = new JLabel("OPENIGTLINK CONNECTION");

	public Start_Stop_IGTLink() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		init();

	}

	private void init() {

		this.setLayout(null);
		this.setLocationRelativeTo(null);
		this.setSize(new Dimension(800, 600));

		lable.setBounds(250, 20, 300, 100);
		lable.setFont(new Font("Arial", Font.BOLD, 20));

		start.setBounds(200, 100, 100, 100);
		start.setFont(new Font("Arial", Font.BOLD, 20));
		start.addActionListener(this);

		stop.setBounds(500, 100, 100, 100);
		stop.setFont(new Font("Arial", Font.BOLD, 20));
		stop.addActionListener(this);

		close.setBounds(500, 300, 200, 100);
		close.setFont(new Font("Arial", Font.BOLD, 20));
		close.addActionListener(this);

		exit_connection.setBounds(200, 300, 200, 100);
		exit_connection.setFont(new Font("Arial", Font.BOLD, 20));
		exit_connection.addActionListener(this);

		this.add(exit_connection);
		this.add(close);
		this.add(lable);
		this.add(start);
		this.add(stop);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		algorithm.DataManager data = new algorithm.DataManager();

		if (src == start) {

			if (value == true) {
				value = false;
				begin = new Networkconnection();

				// If group 2 want to start openigtlink then create a Thread for
				// OpenIGTLinkConnection
				// OpenIGTLinkConnection.update();
				begin.start();

			} else if (value == false) {

				begin.setBreak(true);

			}

		}

		if (src == stop) {

			begin.setBreak(false);

			data.setCount();

			testInputOutput.Networkconnection_Test.setCount();

		}

		if (src == close) {
			System.exit(0);
		}

		if (src == exit_connection) {
			value = true;
			begin.setExit(false);

			data.setCount();
			testInputOutput.Networkconnection_Test.setCount();
		}
	}

	public static void startIGTWindow() {
		Start_Stop_IGTLink frame;
		frame = new Start_Stop_IGTLink();
		frame.validate();
		frame.setVisible(true);
	}

}
