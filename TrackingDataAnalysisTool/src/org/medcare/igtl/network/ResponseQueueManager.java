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
 * Each OpenIGTClient create its own ResponseQueueManager to queue response they
 * get from server. ResponseQueueManager will perform ResponseHandler added in
 * them queue respecting the order.
 * 
 * User must adapt Capability enum, to be able to send the right capability list
 * and to have a correct error management for incorrect capability request
 * 
 * @author Andre Charles Legendre
 */
public class ResponseQueueManager extends Thread {
        private static String VERSION = "0.1a";

        private long sleep;
        public ConcurrentLinkedQueue<ResponseHandler> openIGT_Queue = new ConcurrentLinkedQueue<ResponseHandler>();
        private boolean alive = true;

                private ErrorManager errorManager;

        /***************************************************************************
         * Default ResponseQueueManager constructor.
         * @param errorManager 
         **************************************************************************/
        public ResponseQueueManager(ErrorManager errorManager) {
                super("ResponseQueueManager");
                this.errorManager = errorManager;
        }

        /**
         * Starts the thread reading responses and adding them to the responseQueue
         */
        public void run() {
                boolean res = true;
                do {
                        try {
                                Thread.sleep(sleep); // Wait 100 milli before alive again
                                if (!openIGT_Queue.isEmpty()) {
                                        // On prefere perdre des impressions que rester coince
                                        ResponseHandler responseHandler = (ResponseHandler) openIGT_Queue
                                                        .poll();
                                        if (responseHandler != null) {
                                                try {
                                                        res = responseHandler.performResponse();
                                                        if (!res)
                                                                errorManager.error("ResponseQueueManager ResponseHandler.performResponse result " + res, new Exception("perform response pb "), ErrorManager.RESPONSE_PB_RESULT);
                                                } catch (CrcException c) {
                                                            errorManager.error("PB responseHandler ", c, ErrorManager.RESPONSE_CRC_EXCEPTION);
                                                } catch (Exception e) {
                                                        errorManager.error("PB responseHandler ", e, ErrorManager.RESPONSE_EXCEPTION);
                                                } finally {
                                                        Log.debug("ResponseQueueManager ResponseHandler.performResponse OK");
                                                }
                                        } else {
                                                       errorManager.error("ResponseQueueManager responseHandler null", new Exception("performResponse error"), ErrorManager.RESPONSE_ERROR);
                                        }
                                }
                        } catch (InterruptedException e) {
                                errorManager.error("PB responseHandler ", e, ErrorManager.RESPONSE_INTERRUPTED_EXCEPTION);
                        }
                } while (alive && res);
        }

        /**
         * add a new response to the response queue
         * 
         * @param responseHandler created by OpenIGTClient when receiving a message
         * 
         */
        public void addResponse(ResponseHandler responseHandler) {
                openIGT_Queue.add(responseHandler);
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
