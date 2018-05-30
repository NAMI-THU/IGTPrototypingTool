package org.medcare.igtl.network;

import org.medcare.igtl.messages.ImageMessage;
import org.medcare.igtl.messages.OpenIGTMessage;
import org.medcare.igtl.messages.PositionMessage;
import org.medcare.igtl.messages.TransformMessage;
import org.medcare.igtl.util.Header;
import org.medcare.igtl.util.IGTImage;

public class GenericClientResponseHandler extends ResponseHandler{

	public OpenIGTMessage openIGTMessage;
	public IOpenIgtPacketListener client;
	public GenericClientResponseHandler(Header header, byte[] body,OpenIGTClient client,IOpenIgtPacketListener openIGTClient) {
		super(header, body, client);
		getCapabilityList().add("GET_CAPABIL");
		getCapabilityList().add("TRANSFORM");
		getCapabilityList().add("POSITION");
		getCapabilityList().add("IMAGE");
		getCapabilityList().add("STATUS");
		getCapabilityList().add("NDARRAY");
		this.client = openIGTClient;
	}

	@Override
	public boolean perform(String messageType) throws Exception {

		//Log.info("Recived IGTLink packet, header="+ getHeader() +" body="+new ByteList(getBody()));

		//http://wiki.ncigt.org/index.php/P41:Prostate:BRP:MRI_New_BRP_OpenIGTLink_Protocol_2012_Mar
		//Should support both TRANSFORM and QTRANSFORM packets

		openIGTMessage = new GenericMessageNodeHandler().perform(messageType, getHeader(),getBody(), client);
		return openIGTMessage != null;
	}



	@Override
	public void manageError(String message, Exception exception, int errorCode) {
		// TODO Auto-generated method stub

	}

}
