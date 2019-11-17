package inputOutput;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Exception_Window extends JFrame implements ActionListener{

    private JButton ok = new JButton("OK");
    private static String exceptionName = "Something went wrong";
    private JLabel lable = new JLabel(exceptionName);
    
    public Exception_Window() {
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        init();
        
    }

    private void init() {
        
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setSize(new Dimension(500, 300));
        this.setTitle("Exception!");

        lable.setBounds(165,20,200,100);
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
        Exception_Window frame;
        frame = new Exception_Window();
        frame.validate();
        frame.setVisible(true);
    }
    
    public static void setExceptionText(String name){
        
        exceptionName = name;
        
        startExceptionWindow();
        
        
        
    }
    
    

}
