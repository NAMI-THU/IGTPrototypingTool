/*
 * Main response class
 */
package org.medcare.igtl.network;

import java.util.ArrayList;
import java.util.Iterator;

import org.medcare.igtl.util.ErrorManager;
import org.medcare.igtl.util.Header;
import org.medcare.igtl.util.Status;

/**
 * Perform response data corresponding to the message received performResponse
 * methods must be adapted corresponding to the need of each use
 * User must adapt Capability list, to be able to send the right capability list
 * and to have a correct error management for incorrect capability request
 * User must use SetStatus method so it value correspond to the real status of the client.
 * User must use GetStatus method to set 
 * 
 * @author Andre Charles Legendre
 */
public abstract class ResponseHandler {

        String err = "ResponseHandler.performResponse() failed.";
        private byte[] body;
        private Header header;
        private OpenIGTClient openIGTClient;
        private ArrayList<String> capabilityList = new ArrayList<String>();

        /***************************************************************************
         * Default ResponseHandler constructor.
         * 
         * @param header
         *            of the response
         * 
         * @param body
         *            of the response
         * 
         * @param openIGTClient
         *            openIGTClient object
         * 
         **************************************************************************/
        public ResponseHandler(Header header, byte[] body, OpenIGTClient openIGTClient) {
                this.setHeader(header);
                this.setBody(body);
                this.setOpenIGTClient(openIGTClient);
        }

        /**
         * Perform the response job performResponse methods must be adapted
         * corresponding to the need of each use
         * 
         * @return True if response job performed successfully
         * @throws Exception received by perform method
         */
        public boolean performResponse() throws Exception {
                String messageType = this.getHeader().getDataType();
                Iterator<String> it = getCapabilityList().iterator();
                while (it.hasNext()) {
                        if (messageType.equals((String) it.next())) {
                                return perform(messageType);
                        }
                }
                manageError("ResponseHandler Message Type not Found " + messageType, new Exception("ResponseHandler Message Type not Found"), ErrorManager.RESPONSE_NOT_MANAGED);
                return false;
        }

        /**
         * Perform the response job  this method must be adapted for each use
         * 
         * @param messageType
         *            The messageType
         * @return True if response job performed successfully
         * @throws Exception CrcException will be thrown at crc check during message unpacking
         * Other exceptions can be thrown 
         */
        public abstract boolean perform(String messageType) throws Exception ;

        /**
         * manage error this method must be adapted for each use
         * 
         * @param message
         *            The message for error datail
         * @param exception
         *            The exception for error
         * @param errorCode
         *            The error Code
         */
        public abstract void manageError (String message, Exception exception, int errorCode);

        /**
         *** Gets the ArrayList of Types implemented in this Handler
         **/
        public ArrayList<String> getCapability() {
                return getCapabilityList();
        }

        /**
         *** To set client status
         * @param status
         *** 
         */
        public void setStatus(Status status) {
                this.getOpenIGTClient().setStatus(status);
        }

        /**
         *** To set client status
         *** 
         * @return the status status
         */
        public Status getStatus() {
                return this.getOpenIGTClient().getStatus();
        }

		public byte[] getBody() {
			return body;
		}

		public void setBody(byte[] body) {
			this.body = body;
		}

		public Header getHeader() {
			return header;
		}

		public void setHeader(Header header) {
			this.header = header;
		}

		public OpenIGTClient getOpenIGTClient() {
			return openIGTClient;
		}

		public void setOpenIGTClient(OpenIGTClient openIGTClient) {
			this.openIGTClient = openIGTClient;
		}

		public ArrayList<String> getCapabilityList() {
			return capabilityList;
		}

		public void setCapabilityList(ArrayList<String> capabilityList) {
			this.capabilityList = capabilityList;
		}
}

