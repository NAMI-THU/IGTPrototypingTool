package inputOutput;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;

/**
 * This class is a Thread which starts after the start button is pressed. 
 * It takes the frames continuously from the device or a file through an abstract class and then set them on the TestPanel
 * @author team3
 */

public class TestFrameThread extends Thread {

	Mat mat;
	AbstractImageSource imgSrc;
	BufferedImage bufImg;
	TestPanel panel;
	boolean saveVideoOn;
	ImageWriter save;
	boolean threadSleep = false;

	public TestFrameThread(TestPanel tp, AbstractImageSource src) {
		imgSrc = src;
		panel = tp;
		save = new ImageWriter();
		start();
	}

	public void run() {
		if (imgSrc.openConnection()) {
			while (imgSrc.isConnected) {
				mat = imgSrc.getNextMat();
	
				if(threadSleep == true) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}	
				bufImg = (BufferedImage) HighGui.toBufferedImage(mat);
				panel.setFace(bufImg);
				panel.fps = imgSrc.fps;
				
				if(saveVideoOn == true) {
					save.writeMat(mat);
				}

				panel.repaint();
			}
		}
	}
	
	public void saveVideoStart(String path) {
		save.saveVideo(path, imgSrc.fps, mat.width(), mat.height());
		saveVideoOn = true;
	}
	
	public void stopSave() {
		saveVideoOn = false;
	}
	
	public void saveImageStart(String path) {
		save.saveImage(bufImg, path);
	}

}
