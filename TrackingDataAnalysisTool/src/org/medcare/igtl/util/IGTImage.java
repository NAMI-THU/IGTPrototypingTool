package org.medcare.igtl.util;

import java.awt.image.BufferedImage;

import org.medcare.igtl.messages.ImageMessage;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;


public class IGTImage {
	ImageMessage message;
	BufferedImage image = null;
	TransformNR transform = new TransformNR(); 
	public IGTImage(ImageMessage m){
		message=m;
		//TODO Satya, populate all fields.
		image = null;
		transform = new TransformNR(); 
	}
	
	public TransformNR getTransform(){
		
		return transform;
	}
	
	public BufferedImage getBufferedImage(){
		return image;
	}
}
