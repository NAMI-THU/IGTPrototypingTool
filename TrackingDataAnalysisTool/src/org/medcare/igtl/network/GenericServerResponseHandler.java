package org.medcare.igtl.network;

import org.medcare.igtl.messages.OpenIGTMessage;
import org.medcare.igtl.util.Header;


public class GenericServerResponseHandler extends MessageHandler {
	private IOpenIgtPacketListener server;
	public OpenIGTMessage openIGTMessage;
	public GenericServerResponseHandler(Header header, byte[] body,ServerThread serverThread,IOpenIgtPacketListener server) {
		super(header, body, serverThread);
		capabilityList.add("TRANSFORM");
		capabilityList.add("POSITION");
		capabilityList.add("IMAGE");
		capabilityList.add("MOVE_TO");
		capabilityList.add("ARRAY");
		capabilityList.add("CALIBRATION");
		capabilityList.add("TARGET");
		capabilityList.add("PLANNING");
		capabilityList.add("MOVE_TO_TARGET");
		capabilityList.add("LOCK");
		capabilityList.add("STRING");
		capabilityList.add("STATUS");
		capabilityList.add("GET_STATUS");
		capabilityList.add("GET_TRANS");
		capabilityList.add("NDARRAY");
		
		this.server=server;
	}

	@Override
	public boolean perform(String messageType) throws Exception {
        //Log.debug("perform messageType : " + messageType);
		//Log.info("Recived IGTLink packet, header="+getHeader()+" body="+new ByteList(getBody()));
        
		//TODO - GSF: Need to add complete set of new IGTLInk commands for BRP robot
		//http://wiki.ncigt.org/index.php/P41:Prostate:BRP:MRI_New_BRP_OpenIGTLink_Protocol_2012_Mar
		//Should support both TRANSFORM and QTRANSFORM packets
		
		openIGTMessage = new GenericMessageNodeHandler().perform(messageType, getHeader(),getBody(), server);
		return openIGTMessage != null;
	}

	@Override
	public void manageError(String message, Exception exception, int errorCode) {
		serverThread.errorManager.error(message, exception, errorCode);
	}

}
