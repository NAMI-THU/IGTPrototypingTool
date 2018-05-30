/*=========================================================================

  Program:   OpenIGTLink Library
  Module:    $HeadURL: http://osfe.org/OpenIGTLink/Source/org/medcare/igtl/network/SocketClientFactory.java $
  Language:  java
  Date:      $Date: 2010-08-14 10:37:44 +0200 (ven., 13 nov. 2009) $
  Version:   $Revision: 0ab$

  Copyright (c) Absynt Technologies Ltd. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

=========================================================================*/

package org.medcare.igtl.network;
import com.neuronrobotics.sdk.common.Log;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.medcare.igtl.util.CrcException;
import org.medcare.igtl.util.ErrorManager;

/**
 * One MessageQueueManager is created by each ServerThread to add MessageHandler
 * to MessageQueueManager queue as soon as they receive messages.
 * MessageQueueManager will perform MessageHandler respecting the order. Using
 * eventually ServerThread to send responses to its ServerThread client.
 * 
 * @author Andre Charles Legendre
 */
public class MessageQueueManager extends Thread {
        private static String VERSION = "0.1a";

        private long sleep;
        private ConcurrentLinkedQueue<MessageHandler> openIGT_Queue = new ConcurrentLinkedQueue<MessageHandler>();
        private boolean alive = true;
        ServerThread serverThread;
        private ErrorManager errorManager;

        /***************************************************************************
         * Default MessageQueueManager constructor.
         * 
         * @param serverThread
         *            to use to send responses to OpenIGTclient
         * 
         **************************************************************************/
        public MessageQueueManager(ServerThread serverThread) {
                super("MessageQueueManager");
                this.serverThread = serverThread;
                this.errorManager = serverThread.errorManager;
        }

        /**
         * Starts the thread and process requests in queue
         */
        public void run() {
                boolean res = true;
                do {
                        try {
                                Thread.sleep(sleep); // Wait 100 milli before alive again
                                if (!getOpenIGT_Queue().isEmpty()) {
                                        // On prefere perdre des impressions que rester coince
                                        MessageHandler messageHandler = (MessageHandler) getOpenIGT_Queue().poll();
                                        if (messageHandler != null) {
                                                try {
                                                        res = messageHandler.performRequest();
                                                        if (!res)
                                                                errorManager.error("MessageQueueManager messageHandler.performRequest result " + res, new Exception("messageHandler.performReques Pb Result"), ErrorManager.MESSAGE_PB_RESULT);
                                                } catch (CrcException c) {
                                                        errorManager.error("PB messageHandler ", c, ErrorManager.MESSAGE_CRC_EXCEPTION);
                                                } catch (Exception e) {
                                                	e.printStackTrace();
                                                    errorManager.error("PB messageHandler ", e, ErrorManager.MESSAGE_EXCEPTION);
                                                } finally {
                                                        Log.debug("MessageQueueManager messageHandler.performRequest OK");
                                                }

                                        } else {
                                                       errorManager.error("MessageQueueManager messageHandler null", new Exception("performRequest error"), ErrorManager.MESSAGE_ERROR);
                                        }
                                }
                        } catch (InterruptedException e) {
                                errorManager.error("PB messageHandler ", e, ErrorManager.MESSAGE_INTERRUPTED_EXCEPTION);
                        }
                } while (alive && res);
        }

        /**
         * add a new request to the request queue
         * 
         * @param messageHandler
         * 
         */
        public void addMessage(MessageHandler messageHandler) {
                getOpenIGT_Queue().add(messageHandler);
        }

        /**
         * stop the thread
         * 
         */
        public void destroy() {
                alive = false;
        }

        /**
         *** Gets the current sleep time value return@ The sleep time value
         **/
        public long getSleepTime() {
                return sleep;
        }

        /**
         * Sets the time the listener thread will wait between actions
         * 
         * @param sleep
         */
        public void setSleepTime(long sleep) {
                this.sleep = sleep;
        }

        /**
         *** Gets the current version return@ The version value
         **/
        public String getVersion() {
                return VERSION;
        }

		public void setOpenIGT_Queue(ConcurrentLinkedQueue<MessageHandler> openIGT_Queue) {
			this.openIGT_Queue = openIGT_Queue;
		}

		public ConcurrentLinkedQueue<MessageHandler> getOpenIGT_Queue() {
			return openIGT_Queue;
		}
}
