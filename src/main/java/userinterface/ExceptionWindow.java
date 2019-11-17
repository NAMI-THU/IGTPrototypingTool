package userinterface;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import inputOutput.*;

public class ExceptionWindow extends JFrame implements ActionListener{


		private JButton ok = new JButton("OK");
		private static String exceptionName = "Something went wrong";
		private JLabel lable = new JLabel(exceptionName);
		
		public ExceptionWindow() {
			
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			init();
			
		}

		private void init() {
			
			this.setLayout(null);
			this.setLocationRelativeTo(null);
			this.setSize(new Dimension(500, 300));
			this.setTitle("Exception!");

			lable.setBounds(165,20,300,100);
			lable.setFont(new Font("Arial", Font.BOLD, 20));
			
			ok.setBounds(200, 100, 100, 100);
			ok.setFont(new Font("Arial", Font.BOLD, 20));
			ok.addActionListener(this);
			
			this.add(lable);
			this.add(ok);
			
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			this.dispose();
			
		}
		
		public static void startExceptionWindow(){
			ExceptionWindow frame;
			frame = new ExceptionWindow();
			frame.validate();
			frame.setVisible(true);
		}
		
		public static void setExceptionText(String name){
			
			exceptionName = name;
			
			startExceptionWindow();
				
		}
}
