package userinterface;


import java.awt.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.*;
import java.awt.event.*;




public class Coordinatesystem extends JFrame {

	
	public Coordinatesystem () {
		init();
		}
		
	private void init(){
	  
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
      setDefaultCloseOperation(EXIT_ON_CLOSE); 
      this.setSize(new Dimension(400, 300));
      this.setTitle("x-y-z-Ebene"); }
	
	protected void processWindowEvenet(WindowEvent e){
		super.processWindowEvent(e);
		if(e.getID() == WindowEvent.WINDOW_CLOSING){
			System.exit(0);
		}
          }
	
public void drawAchsen(Graphics g){
	float x, y;
	int xnorm, ynorm, i;
	Graphics gra = this.getGraphics();

	
	try
    { Thread.sleep(100);
    }
    catch(Exception ex){

    }
gra.setColor(Color.black);
gra.drawLine(90,70,90,100);
gra.drawLine(50, 0, 50, 650);


//Skala

for(i=0; i<=650; i = i+10)
	gra.drawLine(45, i, 55, i);

for(i=0; i <= 100; i = i+10)
	gra.drawLine(i, 645, i, 655);

gra.setColor(Color.white);

for ( x = -50; x <=50; x = x + 1){
	y= x*x +2 * x + 1;
	
	
	xnorm = (int) (x + 50);
	ynorm = (int) (650 - y);
	gra.drawLine (xnorm, ynorm, xnorm, ynorm);
}
	}
	
}
