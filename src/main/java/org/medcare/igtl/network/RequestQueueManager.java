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

import org.medcare.igtl.util.ErrorManager;

/**
 * RequestQueueManager create its own OpenIGTClient connected to one
 * OpenIGTServer. Application add Messages request to RequestQueueManager queue.
 * OpenIGTClient will send Messages to its OpenIGTServer respecting the order.
 * 
 * @author Andre Charles Legendre
 */
public class RequestQueueManager extends Thread {
        private static String VERSION = "0.1a";

        private long sleep;
        public ConcurrentLinkedQueue<byte[]> openIGT_Queue = new ConcurrentLinkedQueue<byte[]>();
        private boolean alive = true;
        private OpenIGTClient openIGTClient = null;

                private ErrorManager errorManager;

        /***************************************************************************
         * Default RequestQueueManager constructor.
         * 
         * @param openIGTClient
         *            managing this connection
         * 
         **************************************************************************/
        public RequestQueueManager(OpenIGTClient openIGTClient) {
                super("RequestQueueManager");
                this.openIGTClient = openIGTClient;
                this.errorManager = openIGTClient.errorManager;
        }

        /**
         * Starts the thread and process requests in queue
         */
        public void run() {
                boolean res = true;
                openIGTClient.start();
                do {
                        try {
                                Thread.sleep(sleep); // Wait 100 milli before alive again
                                if (!openIGT_Queue.isEmpty()) {
                                        // On prefere perdre des impressions que rester coince
                                        byte[] bytes = (byte[]) openIGT_Queue.poll();
                                        if (bytes != null) {
                                                try {
                                                        res = openIGTClient.sendBytes(bytes);
                                                        if (!res)
                                                                errorManager.error("PB openIGTClient.sendBytes  ", new Exception("sendBytes error"), ErrorManager.REQUEST_RESULT_ERROR);
                                                } catch (Exception e) {
                                                        errorManager.error("RequestQueueManager openIGTClient.sendBytes ", e, ErrorManager.REQUEST_EXCEPTION);
                                                } finally {
                                                        Log.debug("RequestQueueManager SendBytes OK");
                                                }
                                        } else {
                                                       errorManager.error("PB openIGTClient openIGTRequest null", new Exception("sendBytes error"), ErrorManager.REQUEST_ERROR);
                                        }
                                }
                        } catch (InterruptedException e) {
                                errorManager.error("RequestQueueManager PB dans Thread", e, ErrorManager.REQUEST_INTERRUPT_EXCEPTION);
                        }
                } while (alive && res);
        }

        /**
         * add a new request to the request queue
         * 
         * @param request
         * 
         */
        public void addRequest(byte[] request) {
                if (request != null) {
                        openIGT_Queue.add(request);
                }
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
}
