package org.medcare.igtl.network;
import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.util.Status;

import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR;

import Jama.Matrix;

public interface IOpenIgtPacketListener {
	/**
	 * This method will be called by the IGT server when a transform is received. Supports:
	 * TRANSFORM
	 * QTRANS
	 * QTRANSFORM
	 * POSITION
	 * @param name The string in the 'NAME' field of the IGT packet
	 * @param t
	 */
	void onRxTransform(String name,TransformNR t);
	/**
	 * Request for a transform for transmition to IGT
	 * @param name A string of what type of transform to get
	 * @return the requested transform 
	 */
	TransformNR getTxTransform(String name);
	/**
	 * Request for status from IGT/Slicer
	 * @param name A string of what type of transform to get
	 * @return the requested status 
	 */
	 Status onGetStatus(String name);
	/**
	 * This is the handler for a String packet
	 * @param name A string of what type of data to get
	 * @param body A string of the content
	 */
	void  onRxString(String name,String body);
	
	
	/**
	 * This is the request handler for a String packet
	 * @param name A string of what type of transform to get
	 */
	String onTxString(String name);
	
	/**
	 * This is the handler for an array of raw data in an array
	 * @param name A string of what type of data to get
	 * @param data An array of data
	 */
	void onRxDataArray(String name, Matrix data);
	
	/**
	 * THis is a request for an array of data
	 * @param name  A string of what type of data to get
	 * @return an array of data
	 */
	double [] onTxDataArray(String name);
	
	/**
	 * This is a handler for an Image sent from IGT packet
	 * @param name A string of what type of data to get
	 * @param image the image
	 * @param t A transform of where the image is
	 */
	void onRxImage(String name,ImageMessage image);
	

	/**
	 * THis is a request for an array of data
	 * @param name  A string of what type of data to get
	 * @return an array of data
	 */
	void onTxNDArray(String name);

	/**
	 * THis is a request for an array of data
	 * @param name  A string of what type of data to get
	 * @return an array of data
	 */
	void onRxNDArray(String name, float[] data);
}
