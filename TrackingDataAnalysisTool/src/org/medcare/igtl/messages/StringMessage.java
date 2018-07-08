/*=========================================================================

  Program:   OpenIGTLink Library
  Language:  java
  Date:      $Date: 19-06-2013$
  Version:   $Revision: 1$

  Copyright (c) AIMLab, Worcester Polytechic Institute. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

  Author: Nirav Patel
  
  //TODO this class is yet not fully implemented and should not be used
=========================================================================*/
package org.medcare.igtl.messages;

import java.nio.ByteBuffer;

import org.medcare.igtl.util.BytesArray;
import org.medcare.igtl.util.Header;

public class StringMessage extends OpenIGTMessage{
	/**
	 * This is a stub class for how the String message is to be implemented
	 * @param head
	 * @param body
	 * @throws Exception
	 */
	short encoding=3; //this is default encoding which is "US-ASCII"
	short length;
	String message;
	
	public StringMessage(String deviceName){
		super(deviceName);
	}
	
	public StringMessage(String deviceName, String msg){
		super(deviceName);
		setMessage(msg);
		PackBody();
		setHeader(new Header(VERSION, "STRING", deviceName, getBody()));
	}

	public StringMessage(Header head, byte[] body) throws Exception {
		super(head, body);
		//varify CRC here to make sure this is correct message
		long calculated_crc = BytesArray.crc64(body, body.length, 0L);
		long recvd_crc = head.getCrc();
		
		System.out.println("String: Calculated CRC=" + calculated_crc + "REceived CRC=" + recvd_crc );
	}
	
	void setMessage(String msg){
		this.message = msg;
		this.length = (short)msg.length();
	}
	@Override
	public boolean UnpackBody() throws Exception {
		// TODO Auto-generated method stub
		encoding  = ByteBuffer.wrap(getBody(), 0,2).getShort();
		length = ByteBuffer.wrap(getBody(), 2,2).getShort();
		//TODO always US-ASCII may be need to update if required
		message = new String(getBody() , 4, getBody().length-4, "US-ASCII");
		message = message.trim();
		return true;
	}

	@Override
	public byte[] PackBody() {
		// TODO Auto-generated method stub
		BytesArray body = new BytesArray();
		body.putShort(encoding);
		body.putShort(length);
		body.putString(message);
		setBody(body.getBytes());
		return getBody();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
        String statusString = "STRING Device Name           : " + getDeviceName();
        statusString = statusString + " Encoding      : " + getEncoding() + "\n";
        statusString = statusString + " Length   : " + getLength() + "\n";
        statusString = statusString + " Message: " + getMessage() + "\n";
        statusString = statusString + "============================\n";
        return statusString;
	}
	public short getEncoding(){
		return encoding;
	}
	public short getLength(){
		return length;
	}
	public String getMessage(){
		return message;
	}
}
