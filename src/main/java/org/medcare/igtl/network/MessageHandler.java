/*
 * Main requesting class
 */
package org.medcare.igtl.network;

import java.util.ArrayList;
import java.util.Iterator;

import org.medcare.igtl.util.ErrorManager;
import org.medcare.igtl.util.Header;
import org.medcare.igtl.util.Status;

/**
 * Perform request corresponding to the message received performRequest methods
 * must be adapted corresponding to the need of each use
 * User must adapt Capability list, to be able to send the right capability list
 * and to have a correct error management for incorrect capability request
 * 
 * @author Andre Charles Legendre
 */
public abstract class MessageHandler {

        String err = "MessageHandler.performRequest() failed.";
        private byte[] body;
        private Header header;
        public ServerThread serverThread;
        public ArrayList<String> capabilityList = new ArrayList<String>();

        /***************************************************************************
         * Default MessageQueueManager constructor.
         * 
         * @param header
         *            of the message
         * 
         * @param body
         *            of the message
         * @param serverThread
         *            serverThread object
         * 
         **************************************************************************/
        public MessageHandler(Header header, byte[] body, ServerThread serverThread) {
                this.setHeader(header);
                this.setBody(body);
                this.serverThread = serverThread;
        }

        /**
         * Perform the request job on the message performRequest methods must be
         * corresponding to the need of each use
         * 
         * @return True if response job performed successfully
         * @throws Exception received by perform method
         */
        public boolean performRequest() throws Exception {
        	// extract message type from header
                String messageType = this.getHeader().getDataType();
                System.out.println("Receiv ed message with capability value=" + messageType);
                Iterator<String> it = capabilityList.iterator();
                while (it.hasNext()) {
                        String capab = (String) it.next();
                        // perform corresponding message based on message type
                        if (messageType.equals(capab)) {
                                return perform(messageType);
                        }
                }
                manageError("MessageHandler Message Type not Found " + messageType, new Exception("MessageHandler Message Type not Found"), ErrorManager.MESSAGE_NOT_MANAGED);
                return false;
        }

        /**
         * Perform the request job  this method must be adapted for each use
         * 
         * @param messageType
         *            The messageType
         * @return True if response job performed successfully
         * @throws Exception CrcException will be thrown at crc check during message unpacking
         * Other exceptions can be thrown 
         */
        public abstract boolean perform(String messageType) throws Exception;

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
                return capabilityList;
        }

 
		public void setHeader(Header header) {
			this.header = header;
		}

		public Header getHeader() {
			return header;
		}

		public void setBody(byte[] body) {
			this.body = body;
		}

		public byte[] getBody() {
			return body;
		}
}

