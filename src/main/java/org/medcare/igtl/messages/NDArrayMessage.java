package org.medcare.igtl.messages;

/*=========================================================================

Program:   OpenIGTLink Library
Language:  java
Date:      $Date: 2014-17-06 17:31 PM EST
Version:   $Revision: 0$

Copyright (c) AIMLab, Worcester Polytechnic Institute

This software is distributed WITHOUT ANY WARRANTY; without even
the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the above copyright notices for more information.

Author: Nirav Patel: napatel@wpi.edu
=========================================================================*/

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.medcare.igtl.util.BytesArray;
import org.medcare.igtl.util.Header;
//import com.neuronrobotics.sdk.common.ByteList;

import com.neuronrobotics.sdk.common.Log;

/**
*** This class create an NDArray object from bytes received or help to generate
* bytes to send from it
* 
* @author Nirav Patel
* 
*/
public class NDArrayMessage extends OpenIGTMessage {

    private byte type;
    private byte dim;
    private short size[]; 
    private byte byteData[];
    private ArrayList data;
    
    private static byte TYPE_INT8 = 2 , 
    		TYPE_UINT8=3,
    	    TYPE_INT16=4,
    	    TYPE_UINT16=5,
    	    TYPE_INT32=6,
    	    TYPE_UINT32=7,
    	    TYPE_FLOAT32=10,
    	    TYPE_FLOAT64=11,
    	    TYPE_COMPLEX=13 ;
	
      /**
       *** Constructor to be used to create message to send them with this
       * constructor you must use method SetImageHeader, then CreateBody and then
       * getBytes to send them
       *** 
       * @param deviceName
       *            Device Name
       **/
      public NDArrayMessage(String deviceName) {
              super(deviceName);
      }

      /**
       *** Constructor to be used to create message from received data
       * 
       * @param header
       * @param body
       * @throws Exception 
       */
      public NDArrayMessage(Header header, byte body[]) throws Exception {
            super(header, body);
      		//varify CRC here to make sure this is correct message
      		long calculated_crc = BytesArray.crc64(body, body.length, 0L);
      		long recvd_crc = header.getCrc();
      		
      		//System.out.println("Transform: Calculated CRC=" + calculated_crc + "REceived CRC=" + recvd_crc );
      }

      public NDArrayMessage(String deviceName,  float[] data) {
    	  super(deviceName);
    	  //This implements 1D Float array
    	  
    	  set1D_FloatData( data );

    	  //set the data from the array
      		PackBody();
      		setHeader(new Header(VERSION, "NDARRAY", deviceName, getBody()));
      }

      public NDArrayMessage(String deviceName, byte type, byte dim, short[] size, float[] data) {
      	super(deviceName);
      	//THIS IS FOR NDimensional Array Implementation and its not implemented yet
      }

      public void set1D_FloatData( float[] data){
    	  this.type = TYPE_FLOAT32;  
    	  this.dim = 1;
    	  this.size = new short[dim]; 
    	  this.size[0] = (short)data.length;
    	  
    	  this.data = new ArrayList<Float>();
    	  
    	  BytesArray bytesArray = new BytesArray();
          for (int i = 0; i < data.length; i++){
        	  bytesArray.putDouble(data[i], 4);
        	  this.data.add(data[i]);
          }
          this.byteData = bytesArray.getBytes();
          
      }

      public void set1D_FloatData(){
    	  this.data = new ArrayList<Float>();
    	  
    	  BytesArray bytesArray = new BytesArray();
    	  bytesArray.putBytes(byteData);
          for (int i = 0; i < byteData.length/4; i++){
        	  float val = (float)bytesArray.getDouble(4);
        	  this.data.add(val);
          }
      }
      public float[] get1DFloatData(){
    	  float floatData[] = new float[data.size()];
    	  for(int i=0;i<data.size();i++){
    		  floatData[i] = ((Float)data.get(i)).floatValue();
    	  }
    	  return floatData;
      }
/**
       *** To create body from body array
       * 
       *** 
       * @return true if unpacking is ok
       */
      @Override
      public boolean UnpackBody() throws Exception {
    	  ByteBuffer body = ByteBuffer.wrap(getBody());
    	  
  		type  = body.get();
  		dim = body.get();
  		size = new short[dim];
  		for( int i=0;i<dim;i++){
  			size[i] = body.getShort();
  		}
  		int dataLen = getBody().length-(2+2*dim);
  		byteData = new byte[dataLen];
  		body.get(byteData, 0 , dataLen-1);
  		if( type == TYPE_FLOAT32 ){
  			set1D_FloatData();
  		}
  		return true;
      }

      @Override
      public byte[] PackBody() {
  		BytesArray body = new BytesArray();
  		body.putByte(this.type);
  		body.putByte(this.dim);
  		for(int i=0;i<this.size.length;i++){
  			body.putShort(this.size[i]);
  		}
  		body.putBytes(this.byteData);
  		setBody(body.getBytes());
  		return getBody();
      }


      @Override
      public String toString() {
              String transformString = "NDArray Device Name           : " + getDeviceName();
              transformString = transformString + "NDArray Type           : " + type + " ";
              transformString = transformString + "NDArray Dim           : " + dim + " ";
              for(int i=0;i<dim;i++){
                  transformString = transformString + "Dim[" + i + "]=" + size[i] + " ";
              }
              for(int i=0;i<data.size();i++){
                  transformString = transformString + "Data[" + i + "]=" + data.get(i) + " ";
              }
              return transformString;
      }
      
}

