/*=========================================================================

  Program:   OpenIGTLink Library
  Module:    $HeadURL: http://osfe.org/OpenIGTLink/Source/org/medcare/igtl/messages/ImageMessage.java $
  Language:  java
  Date:      $Date: 2010-18-14 10:37:44 +0200 (ven., 13 nov. 2009) $
  Version:   $Revision: 0ab$

  Copyright (c) Absynt Technologies Ltd. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

=========================================================================*/

package org.medcare.igtl.messages;

import org.medcare.igtl.util.BytesArray;
import org.medcare.igtl.util.Header;
import org.medcare.igtl.util.Status;

/**
 *** This class create an Status object from bytes received or help to generate
 * bytes to send from it
 * 
 * @author Andre Charles Legendre
 * 
 */
public class StatusMessage extends OpenIGTMessage {

        public static int IGTL_STATUS_HEADER_SIZE = 30;

        public static int IGTL_STATUS_ERROR_NAME_LENGTH = 20;

        long code; // Unsigned short 16bit
        long subCode; // int 64bit
        String errorName; // char[12]
        String statusString; // char[ BodySize - 30 ]
        private byte[] status_data;

        private Status status;

        /**
         *** Constructor to be used to create message to send them with this
         * constructor you must use method SetImageHeader, then CreateBody and then
         * getBytes to send them
         *** 
         * @param deviceName
         *            Device Name
         **/
        public StatusMessage(String deviceName) {
                super(deviceName);
        }
        /**
         *** Constructor to be used to create message to send them with this
         * constructor you must use method SetImageHeader, then CreateBody and then
         * getBytes to send them
         *** 
         * @param deviceName
         *            Device Name
         **/
        public StatusMessage(String deviceName,int code, int subCode, String status) {
                super(deviceName);
                setStatus(new Status(code,subCode, deviceName, status));
                PackBody();
        }
        /**
         *** Constructor to be used to create message to send them with this
         * constructor you must use method SetImageHeader, then CreateBody and then
         * getBytes to send them
         *** 
         * @param deviceName -Device Name
         *            
         **/
        public StatusMessage(String deviceName,int code, int subCode, String errorName, String status) {
                super(deviceName);
                setStatus(new Status(code,subCode, errorName, status));
                PackBody();
        }
        /**
         *** Constructor to be used to create message from received data
         * 
         * @param header
         * @param body
         * @throws Exception 
         */
        public StatusMessage(Header header, byte body[]) throws Exception {
                super(header, body);
        }

        /**
         *** To create body from body array
         * 
         *** 
         * @return true if unpacking is ok
         */
        @Override
        public boolean UnpackBody() throws Exception {
                int bodyLength = getBody().length;
                status_data = new byte[bodyLength];
                System.arraycopy(getBody(), 0, status_data, 0, bodyLength);
                setStatusData(status_data);
                return true;
        }

        /**
         *** To create body from image_header and image_data
         *  SetStatusData must have called first
         * 
         *** 
         * @return the bytes array containing the body
         */
        @Override
        public byte[] PackBody() {
        		setStatusData(status);
                setBody(new byte[status_data.length]);
                System.arraycopy(status_data, 0, getBody(), 0, status_data.length);
                setHeader(new Header(VERSION, "STATUS", deviceName, getBody()));
                return getBytes();
        }

        /**
         *** To create status_data from server or client status and to get the byte array to send
         * @param status
         *** 
         * @return the bytes array created from the value
         */
        public byte[] setStatusData(Status status) {
                this.status = status;
                this.code = status.getCode();
                this.subCode = status.getSubCode();
                this.errorName = status.getErrorName();
                this.statusString = status.getStatusString();
                bytesArray = new BytesArray();
                bytesArray.putULong(code, 2);
                bytesArray.putLong(subCode, 8);
                bytesArray.putString(errorName);
                bytesArray.putString(statusString);
                status_data = bytesArray.getBytes();
                return status_data;
        }

        /**
         *** To extract image characteristics from status_data byte array
         * @param status_data
         */
        public void setStatusData(byte status_data[]) {
                this.status_data = status_data;
                bytesArray = new BytesArray();
                bytesArray.putBytes(status_data);
                this.code = bytesArray.getLong(2); // Unsigned short 16bits
                this.subCode = bytesArray.getLong(8); // int 64
                this.errorName = bytesArray.getString(IGTL_STATUS_ERROR_NAME_LENGTH); // char 20
                //Control status_data.length-IGTL_STATUS_HEADER_SIZE-1 == '\0' ???
                this.statusString = bytesArray.getString(status_data.length-IGTL_STATUS_HEADER_SIZE);
                this.status = new Status((int) code, (int) subCode, errorName, statusString);
        }

        /**
         *** To get status_data byte array
         *** 
         * @return the status_data bytes array
         */
        public byte[] getStatusData() {
                return this.status_data;
        }

        /**
         *** To set client or server code
         * @param code
         *** 
         */
        public void setCode(long code) {
                this.code = code;
        }

        /**
         *** To get client or server code
         *** 
         * @return the status code
         */
        public long getCode() {
                return this.code;
        }

        /**
         *** To set client or server status
         * @param status
         *** 
         */
        public void setStatus(Status status) {
                this.status = status;
        }

        /**
         *** To get client or server status
         *** 
         * @return the status code
         */
        public Status getStatus() {
                return this.status;
        }

        /**
         *** To set Image subCode
         * @param subCode
         *** 
         */
        void setSubCode(long subCode) {
                this.subCode = subCode;
        }

        /**
         *** To get Image subCode
         *** 
         * @return the subCode array
         */
        public long getSubCode() {
                return subCode;
        }

        /**
         *** To set Error Name
         * @param errorName
         *** 
         */
        void setErrorName(String errorName) {
                this.errorName = errorName;
        }

        /**
         *** To get errorName subCode
         *** 
         * @return the errorName
         */
        public String getErrorName() {
                return errorName;
        }

        /**
         *** To set Status String
         * @param statusString
         *** 
         */
        void setStatusString(String statusString) {
                this.statusString = statusString;
        }

        /**
         *** To get statusString subCode
         *** 
         * @return the statusString
         */
        public String getStatusString() {
                return statusString;
        }

        /**
         *** To get status String
         *** 
         * @return the status String
         */
        @Override
        public String toString() {
                String statusString = "STATUS Device Name           : " + getDeviceName();
                statusString = statusString + " Code      : " + getCode() + "\n";
                statusString = statusString + " SubCode   : " + getSubCode() + "\n";
                statusString = statusString + " Error Name: " + getErrorName() + "\n";
                statusString = statusString + " Status    : " + getStatusString() + "\n";
                statusString = statusString + "============================\n";
                return statusString;
        }
}

