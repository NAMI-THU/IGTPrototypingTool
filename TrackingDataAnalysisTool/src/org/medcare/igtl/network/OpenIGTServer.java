/*=========================================================================

  Program:   OpenIGTLink Library
  Module:    $HeadURL: http://osfe.org/OpenIGTLink/Source/org/medcare/igtl/network/SocketServer.java $
  Language:  java
  Date:      $Date: 2010-08-14 10:37:44 +0200 (ven., 13 nov. 2009) $
  Version:   $Revision: 0ab$

  Copyright (c) Absynt Technologies Ltd. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

  Edited By Nirav Patel(napatel@wpi.edu) on Aug 10 2013
  ---Making server run and listen for multiple devices and maintain server status information
  
=========================================================================*/

package org.medcare.igtl.network;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import org.medcare.igtl.messages.OpenIGTMessage;
import org.medcare.igtl.util.ErrorManager;
import org.medcare.igtl.util.Status;
import org.medcare.igtl.util.Header;

import com.neuronrobotics.sdk.common.Log;
import com.neuronrobotics.sdk.util.ThreadUtil;


/**
 * <p>
 * The class can be used to create a server listening a port Messages received
 * will be queued and proceed
 * <p>
 * 
 * @author <a href="mailto:andleg@osfe.org"> 
 * Andre Charles Legendre </a>
 * @version 0.1a (09/06/2010)
 * 
 */

public abstract class OpenIGTServer {
        public ErrorManager errorManager;
        ServerSocket socket = null;
        private ServerThread thread;
        private boolean keepAlive = true;
        private int port;
        
        public static enum ServerStatus {STOPPED, LISTENING, CONNECTED, DISCONNECTED }; //possible server states
        ServerStatus currentStatus = ServerStatus.STOPPED; //start as stopped status
        /***************************************************************************
         * Default MessageQueueManager constructor.
         * 
         * @param port
         *            port on which this server will be bind
         * @param errorManager
         *            main class running this server
         * @throws Exception 
         * 
         **************************************************************************/
        public OpenIGTServer(int port, ErrorManager errorManager) throws Exception {
            this.errorManager = errorManager;
            this.port = port;
            currentStatus = ServerStatus.STOPPED;
            
            server s = new server();
            s.start();
        }
        
        public void startServer(int port) throws IOException{
        	Log.debug("Starting IGTLink Server");
        	stopServer();
            try {
            	ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
                socket = serverSocketFactory.createServerSocket(this.port);
                Log.debug("Server Socket created");
 
            } catch (IOException e) {
                    errorManager.error("OpenIGTServer Could not listen on port: " + this.port, e, ErrorManager.OPENIGTSERVER_IO_EXCEPTION);
                    throw e;
            }
        }
        public void startListening(int port){
        	this.port = port;
        	try {
				startServer(this.port);
		        server s = new server();
		        s.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
  
        	
        }
		public void stopServer(){
			Log.debug("Stopping the IGTLink Server");
			if(getServerThread()!=null)
				getServerThread().interrupt();
			if(socket!= null){
				try {
					socket.close();
					currentStatus = ServerStatus.STOPPED;
					socket = null;
					Log.debug("IGTLink Server stopped");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket = null;
			currentStatus = ServerStatus.STOPPED;
		}
       
        private class server extends Thread{
        	public void run(){
        		{
        			while(getKeepAlive()){
	            		try {
	                  		while( socket==null || socket.isClosed() )
	                  		{
	            	        	Log.debug("IGTLink Server Died, restarting");
	            	        	try {
	            					startServer(port);
	            				} catch (IOException e) {
	            					// TODO Auto-generated catch block
	            					e.printStackTrace();
	            					currentStatus = ServerStatus.STOPPED;
	            				}
	                		}

	    					startIGT();
	    					 //ThreadUtil.wait(500);
		               		 Log.debug("Before waiting for another client, waiting for currnt client to get disconnected");
		            		 while(getServerThread().getAlive() != false){
		            			 //wait here until client gets disconnected
		            			 ThreadUtil.wait(500);
		            		 }
		            		 Log.debug("IGTLink client Disconnected");
		            		 //currentStatus = ServerStatus.DISCONNECTED;
	    				} catch (Exception e1) {
	    					// TODO Auto-generated catch block
	    					e1.printStackTrace();
	    				}
        			}
        		}
        	}
        }
        /**
         * This method waits until a client connects to the server port
         * @throws IOException
         * @throws Exception
         */
        private void startIGT() throws IOException, Exception{
     		 
      		try{
            	 currentStatus = ServerStatus.LISTENING;
               	 Log.debug("IGTLink Server Waiting for connection");
            	 setServerThread(new ServerThread(socket.accept(), this));
        		 getServerThread().start();
        		 currentStatus = ServerStatus.CONNECTED;
        		 Log.debug("IGTLink client connected");
      		}catch(Exception e){
      			Log.error("Server died while waiting for client");
      			e.printStackTrace();
      		}
        }

         /**
         * Sends a message up the link
         * @param messageHandler
         * @throws Exception 
         */
        public void sendMessage(OpenIGTMessage message) throws Exception{
        	if(getServerThread()!=null){
        		
        		try {
    				//TODO before sending message it should be packed, should this be done here or in Message itself? 
					getServerThread().sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					stopServer();
					throw e;
				}
        		
        		//Log.info("Pushing upstream IGTLink packet "+message);
        	}else{
        		Log.debug("No clients connected");
        	}
        }

        /**
         *** To get message Handler
         * @param header header of the message received
         * 
         * @param bodyBuf byte array of the body of the message received
         * 
         * @param serverThread serverThread managing connection of client where does come from the message
         *** 
         * @return the message Handler
         */
        public abstract MessageHandler getMessageHandler(Header header, byte[] bodyBuf, ServerThread serverThread);

		public void setServerThread(ServerThread thread) {
			this.thread = thread;
		}

		public ServerThread getServerThread() {
			return thread;
		}
		/**
		 * @return the killAlive
		 */
		public boolean getKeepAlive() {
			return keepAlive;
		}

		/**
		 * @param killAlive the killAlive to set
		 */
		public void setKeepAlive(boolean keepAlive) {
			this.keepAlive = keepAlive;
		}

		/**
		 * @return the currentStatus
		 */
		public ServerStatus getCurrentStatus() {
			return currentStatus;
		}

		/**
		 * @param currentStatus the currentStatus to set
		 */
		public void setCurrentStatus(ServerStatus currentStatus) {
			this.currentStatus = currentStatus;
		}
		
		public boolean isConnected(){
			if( currentStatus == ServerStatus.CONNECTED ){
				return true;
			}
			else{
				return false;
			}
		}
}
