/*=========================================================================

  Program:   OpenIGTLink Library
  Module:    $HeadURL: http://osfe.org/OpenIGTLink/Source/org/medcare/igtl/util/BytesArray.java $
  Language:  java
  Date:      $Date: 2010-08-14 10:37:44 +0200 (ven., 13 nov. 2009) $
  Version:   $Revision: 0ab$

  Copyright (c) Absynt Technologies Ltd. All rights reserved.

  This software is distributed WITHOUT ANY WARRANTY; without even
  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
  PURPOSE.  See the above copyright notices for more information.

=========================================================================*/

package org.medcare.igtl.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

//import com.neuronrobotics.sdk.common.ByteList;

/**
 *** For reading/writing binary fields
 * 
 * @author Andre Charles Legendre
 **/

public class BytesArray {
	public static final long MAX_UINT = 4294967295L; // 2^32 - 1
	// ------------------------------------------------------------------------

	private byte bytesArray[] = null;
	private int size = 0;
	private int index = 0;
	Charset charset = Charset.forName("US-ASCII");
	// ------------------------------------------------------------------------

	//private static final long[] CRC_TABLE;
	//private static final long crc64Polynomial = 0xC96C5795D7870F42L;
	private static final long[] CRC_TABLE={
			    0x0000000000000000L,0x42F0E1EBA9EA3693L,
			    0x85E1C3D753D46D26L,0xC711223CFA3E5BB5L,
			    0x493366450E42ECDFL,0x0BC387AEA7A8DA4CL,
			    0xCCD2A5925D9681F9L,0x8E224479F47CB76AL,
			    0x9266CC8A1C85D9BEL,0xD0962D61B56FEF2DL,
			    0x17870F5D4F51B498L,0x5577EEB6E6BB820BL,
			    0xDB55AACF12C73561L,0x99A54B24BB2D03F2L,
			    0x5EB4691841135847L,0x1C4488F3E8F96ED4L,
			    0x663D78FF90E185EFL,0x24CD9914390BB37CL,
			    0xE3DCBB28C335E8C9L,0xA12C5AC36ADFDE5AL,
			    0x2F0E1EBA9EA36930L,0x6DFEFF5137495FA3L,
			    0xAAEFDD6DCD770416L,0xE81F3C86649D3285L,
			    0xF45BB4758C645C51L,0xB6AB559E258E6AC2L,
			    0x71BA77A2DFB03177L,0x334A9649765A07E4L,
			    0xBD68D2308226B08EL,0xFF9833DB2BCC861DL,
			    0x388911E7D1F2DDA8L,0x7A79F00C7818EB3BL,
			    0xCC7AF1FF21C30BDEL,0x8E8A101488293D4DL,
			    0x499B3228721766F8L,0x0B6BD3C3DBFD506BL,
			    0x854997BA2F81E701L,0xC7B97651866BD192L,
			    0x00A8546D7C558A27L,0x4258B586D5BFBCB4L,
			    0x5E1C3D753D46D260L,0x1CECDC9E94ACE4F3L,
			    0xDBFDFEA26E92BF46L,0x990D1F49C77889D5L,
			    0x172F5B3033043EBFL,0x55DFBADB9AEE082CL,
			    0x92CE98E760D05399L,0xD03E790CC93A650AL,
			    0xAA478900B1228E31L,0xE8B768EB18C8B8A2L,
			    0x2FA64AD7E2F6E317L,0x6D56AB3C4B1CD584L,
			    0xE374EF45BF6062EEL,0xA1840EAE168A547DL,
			    0x66952C92ECB40FC8L,0x2465CD79455E395BL,
			    0x3821458AADA7578FL,0x7AD1A461044D611CL,
			    0xBDC0865DFE733AA9L,0xFF3067B657990C3AL,
			    0x711223CFA3E5BB50L,0x33E2C2240A0F8DC3L,
			    0xF4F3E018F031D676L,0xB60301F359DBE0E5L,
			    0xDA050215EA6C212FL,0x98F5E3FE438617BCL,
			    0x5FE4C1C2B9B84C09L,0x1D14202910527A9AL,
			    0x93366450E42ECDF0L,0xD1C685BB4DC4FB63L,
			    0x16D7A787B7FAA0D6L,0x5427466C1E109645L,
			    0x4863CE9FF6E9F891L,0x0A932F745F03CE02L,
			    0xCD820D48A53D95B7L,0x8F72ECA30CD7A324L,
			    0x0150A8DAF8AB144EL,0x43A04931514122DDL,
			    0x84B16B0DAB7F7968L,0xC6418AE602954FFBL,
			    0xBC387AEA7A8DA4C0L,0xFEC89B01D3679253L,
			    0x39D9B93D2959C9E6L,0x7B2958D680B3FF75L,
			    0xF50B1CAF74CF481FL,0xB7FBFD44DD257E8CL,
			    0x70EADF78271B2539L,0x321A3E938EF113AAL,
			    0x2E5EB66066087D7EL,0x6CAE578BCFE24BEDL,
			    0xABBF75B735DC1058L,0xE94F945C9C3626CBL,
			    0x676DD025684A91A1L,0x259D31CEC1A0A732L,
			    0xE28C13F23B9EFC87L,0xA07CF2199274CA14L,
			    0x167FF3EACBAF2AF1L,0x548F120162451C62L,
			    0x939E303D987B47D7L,0xD16ED1D631917144L,
			    0x5F4C95AFC5EDC62EL,0x1DBC74446C07F0BDL,
			    0xDAAD56789639AB08L,0x985DB7933FD39D9BL,
			    0x84193F60D72AF34FL,0xC6E9DE8B7EC0C5DCL,
			    0x01F8FCB784FE9E69L,0x43081D5C2D14A8FAL,
			    0xCD2A5925D9681F90L,0x8FDAB8CE70822903L,
			    0x48CB9AF28ABC72B6L,0x0A3B7B1923564425L,
			    0x70428B155B4EAF1EL,0x32B26AFEF2A4998DL,
			    0xF5A348C2089AC238L,0xB753A929A170F4ABL,
			    0x3971ED50550C43C1L,0x7B810CBBFCE67552L,
			    0xBC902E8706D82EE7L,0xFE60CF6CAF321874L,
			    0xE224479F47CB76A0L,0xA0D4A674EE214033L,
			    0x67C58448141F1B86L,0x253565A3BDF52D15L,
			    0xAB1721DA49899A7FL,0xE9E7C031E063ACECL,
			    0x2EF6E20D1A5DF759L,0x6C0603E6B3B7C1CAL,
			    0xF6FAE5C07D3274CDL,0xB40A042BD4D8425EL,
			    0x731B26172EE619EBL,0x31EBC7FC870C2F78L,
			    0xBFC9838573709812L,0xFD39626EDA9AAE81L,
			    0x3A28405220A4F534L,0x78D8A1B9894EC3A7L,
			    0x649C294A61B7AD73L,0x266CC8A1C85D9BE0L,
			    0xE17DEA9D3263C055L,0xA38D0B769B89F6C6L,
			    0x2DAF4F0F6FF541ACL,0x6F5FAEE4C61F773FL,
			    0xA84E8CD83C212C8AL,0xEABE6D3395CB1A19L,
			    0x90C79D3FEDD3F122L,0xD2377CD44439C7B1L,
			    0x15265EE8BE079C04L,0x57D6BF0317EDAA97L,
			    0xD9F4FB7AE3911DFDL,0x9B041A914A7B2B6EL,
			    0x5C1538ADB04570DBL,0x1EE5D94619AF4648L,
			    0x02A151B5F156289CL,0x4051B05E58BC1E0FL,
			    0x87409262A28245BAL,0xC5B073890B687329L,
			    0x4B9237F0FF14C443L,0x0962D61B56FEF2D0L,
			    0xCE73F427ACC0A965L,0x8C8315CC052A9FF6L,
			    0x3A80143F5CF17F13L,0x7870F5D4F51B4980L,
			    0xBF61D7E80F251235L,0xFD913603A6CF24A6L,
			    0x73B3727A52B393CCL,0x31439391FB59A55FL,
			    0xF652B1AD0167FEEAL,0xB4A25046A88DC879L,
			    0xA8E6D8B54074A6ADL,0xEA16395EE99E903EL,
			    0x2D071B6213A0CB8BL,0x6FF7FA89BA4AFD18L,
			    0xE1D5BEF04E364A72L,0xA3255F1BE7DC7CE1L,
			    0x64347D271DE22754L,0x26C49CCCB40811C7L,
			    0x5CBD6CC0CC10FAFCL,0x1E4D8D2B65FACC6FL,
			    0xD95CAF179FC497DAL,0x9BAC4EFC362EA149L,
			    0x158E0A85C2521623L,0x577EEB6E6BB820B0L,
			    0x906FC95291867B05L,0xD29F28B9386C4D96L,
			    0xCEDBA04AD0952342L,0x8C2B41A1797F15D1L,
			    0x4B3A639D83414E64L,0x09CA82762AAB78F7L,
			    0x87E8C60FDED7CF9DL,0xC51827E4773DF90EL,
			    0x020905D88D03A2BBL,0x40F9E43324E99428L,
			    0x2CFFE7D5975E55E2L,0x6E0F063E3EB46371L,
			    0xA91E2402C48A38C4L,0xEBEEC5E96D600E57L,
			    0x65CC8190991CB93DL,0x273C607B30F68FAEL,
			    0xE02D4247CAC8D41BL,0xA2DDA3AC6322E288L,
			    0xBE992B5F8BDB8C5CL,0xFC69CAB42231BACFL,
			    0x3B78E888D80FE17AL,0x7988096371E5D7E9L,
			    0xF7AA4D1A85996083L,0xB55AACF12C735610L,
			    0x724B8ECDD64D0DA5L,0x30BB6F267FA73B36L,
			    0x4AC29F2A07BFD00DL,0x08327EC1AE55E69EL,
			    0xCF235CFD546BBD2BL,0x8DD3BD16FD818BB8L,
			    0x03F1F96F09FD3CD2L,0x41011884A0170A41L,
			    0x86103AB85A2951F4L,0xC4E0DB53F3C36767L,
			    0xD8A453A01B3A09B3L,0x9A54B24BB2D03F20L,
			    0x5D45907748EE6495L,0x1FB5719CE1045206L,
			    0x919735E51578E56CL,0xD367D40EBC92D3FFL,
			    0x1476F63246AC884AL,0x568617D9EF46BED9L,
			    0xE085162AB69D5E3CL,0xA275F7C11F7768AFL,
			    0x6564D5FDE549331AL,0x279434164CA30589L,
			    0xA9B6706FB8DFB2E3L,0xEB46918411358470L,
			    0x2C57B3B8EB0BDFC5L,0x6EA7525342E1E956L,
			    0x72E3DAA0AA188782L,0x30133B4B03F2B111L,
			    0xF7021977F9CCEAA4L,0xB5F2F89C5026DC37L,
			    0x3BD0BCE5A45A6B5DL,0x79205D0E0DB05DCEL,
			    0xBE317F32F78E067BL,0xFCC19ED95E6430E8L,
			    0x86B86ED5267CDBD3L,0xC4488F3E8F96ED40L,
			    0x0359AD0275A8B6F5L,0x41A94CE9DC428066L,
			    0xCF8B0890283E370CL,0x8D7BE97B81D4019FL,
			    0x4A6ACB477BEA5A2AL,0x089A2AACD2006CB9L,
			    0x14DEA25F3AF9026DL,0x562E43B4931334FEL,
			    0x913F6188692D6F4BL,0xD3CF8063C0C759D8L,
			    0x5DEDC41A34BBEEB2L,0x1F1D25F19D51D821L,
			    0xD80C07CD676F8394L,0x9AFCE626CE85B507L	};
	//static {
	//	CRC_TABLE = new long[0x100];
		/*for (int i = 0; i < 0x100; i++) {
			long v = i;
			for (int j = 0; j < 8; j++) {
				// is current coefficient set?
				if ((v & 1) == 1) {
					// yes, then assume it gets zero'd (by implied x^64
					// coefficient of dividend)
					// and add rest of the divisor
					v = (v >>> 1) ^ crc64Polynomial;
				} else {
					// no? then move to next coefficient
					v = (v >>> 1);
				}
			}
			// Log.debug("0x" + Long.toHexString(v));
			CRC_TABLE[i] = v;
		}*/
		

		//Log.debug("\n CRC table");
		//Log.debug(CRC_TABLE + "\n");
	//}

	// ------------------------------------------------------------------------

	/**
	 *** Destination Constructor
	 *** 
	 * 
	 **/
	public BytesArray() {
		this.bytesArray = new byte[0];
		this.size = 0; // no 'size' yet
		this.index = 0; // start at index '0' for writing
	}

	// ------------------------------------------------------------------------

	/**
	 *** Write an array of bytes to the bytesArray
	 *** 
	 * @param n
	 *            The bytes to write to the bytesArray è¦�å†™å…¥çš„å­—ç¬¦
	 *** @param nOfs
	 *            The offset into <code>n</code> to start reading from å†™å¼€å§‹çš„ä½�ç½®
	 *** @param nLen
	 *            The number of bytes to write from <code>n</code> å†™å…¥çš„é•¿åº¦
	 *** @return The number of bytes written
	 **/
	// ä¸‰ç§�putBytes å½¢å¼�ï¼Œç¬¬ä¸€ç§�æ˜¯æœ€å…¨çš„
	public int putBytes(byte n[], int nOfs, int nLen) {
		/* check for nothing to write */
		if (nLen > 0 && nOfs >= 0 && n != null && (n.length - nOfs >= nLen)) {
			enlarge(nLen);
			System.arraycopy(n, nOfs, bytesArray, this.size, nLen);
			this.size = bytesArray.length;
			return nLen;
		}
		// nothing to write
		return 0;
	}

	/**
	 *** Write an array of bytes to the bytesArray
	 *** 
	 * @param n
	 *            The bytes to write to the bytesArray
	 *** @param nLen
	 *            The number of bytes to write from <code>n</code>
	 *** @return The number of bytes written
	 **/
	public int putBytes(byte n[], int nLen) {
		return this.putBytes(n, this.index, nLen);
	}

	/**
	 *** Write an array of bytes to the bytesArray
	 *** 
	 * @param n
	 *            The bytes to write to the bytesArray
	 *** @param nLen
	 *            The number of bytes to write from <code>n</code>
	 *** @return The number of bytes written
	 **/
	public int putByte(byte n) {
		byte []data = new byte[1];
		data[0] = n;
		return this.putBytes(data, this.index, 1);
	}
	/**
	 *** Write an array of bytes to the bytesArray
	 *** 
	 * @param n
	 *            The bytes to write to the bytesArray
	 *** @return The number of bytes writen
	 **/
	public int putBytes(byte n[]) {
		return this.putBytes(n, this.index, n.length);
	}

	// ------------------------------------------------------------------------

	/**
	 *** For an output/write bytesArray, returns the number of bytes written. For
	 * an input/read bytesArray, return the total number of bytes contained in
	 * this bytesArray.
	 *** 
	 * @return The current size of the bytesArray
	 **/
	public int getSize() {
		return this.size;
	}

	// ------------------------------------------------------------------------

	/**
	 *** Gets the current read/write index return@ The index
	 **/
	public int getIndex() {
		return this.index;
	}

	/**
	 *** Resets the read/write index to the specified value
	 *** 
	 * @param ndx
	 *            The value to set the index
	 **/
	public void setIndex(int ndx) {
		this.index = (ndx <= 0) ? 0 : ndx;
	}

	// ------------------------------------------------------------------------

	/**
	 *** Return a byte array representing the data
	 * 
	 *** 
	 * @return A copy of the current bytesArray (as-is)
	 **/
	public byte[] getBytes() {
		// return the full copy bytesArray (regardless of the state of
		// 'this.index')
		byte n[] = new byte[this.size];
		// æº�æ–‡ä»¶ï¼Œèµ·å§‹ä½�ç½®ï¼Œç›®æ ‡æ–‡ä»¶ï¼Œèµ·å§‹ä½�ç½®ï¼Œ å¤�åˆ¶çš„å­—èŠ‚é•¿åº¦
		System.arraycopy(bytesArray, 0, n, 0, this.size);
		return n;
	}

	// ------------------------------------------------------------------------

	/**
	 *** Read <code>length</code< of bytes from the bytesArray
	 *** 
	 * @param length
	 *            The number fo bytes to read from the bytesArray
	 *** 
	 * @return A copy of the current bytesArray or byte[0]
	 **/
	public byte[] getBytes(int length) {
		// This will read 'length' bytes, or the remaining bytes, whichever is
		// less
		int maxLen = ((length >= 0) && ((this.index + length) <= this.size)) ? length : (this.size - this.index);
		if (maxLen <= 0) {
			// no room left
			return new byte[0];
		} else {
			byte n[] = new byte[maxLen];
			System.arraycopy(this.bytesArray, this.index, n, 0, maxLen);
			this.index += maxLen;
			return n;
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Read a <code>long</code> value from bytesArray
	 *** 
	 * @param length
	 *            The number of bytes to decode the value from
	 *** @return The decoded long value, or 0L
	 **/
	public long getLong(int length) {
		int maxLen = ((this.index + length) <= this.size) ? length
				: (this.size - this.index);
		if (maxLen <= 0) {
			// nothing to read
			return 0L;
		} else {
			byte n[] = getBytes(maxLen);
			long val = decodeLong(n, 0, true);
			return val;
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Read an unsigned <code>long</code> value from bytesArray
	 *** 
	 * @param length
	 *            The number of bytes to decode the value from
	 *** @return The decoded long value, or 0L
	 **/
	public long getULong(int length) {
		int maxLen = ((this.index + length) <= this.size) ? length : (this.size - this.index);
		if (maxLen <= 0) {
			// nothing to read
			return 0L;
		} else {
			byte n[] = getBytes(maxLen);
			long val = decodeLong(n, 0, false);
			return val;
		}
	}

	/**
	 *** Read a <code>double</code> value from bytesArray, using IEEE 754 format
	 *** 
	 * @param length
	 *            The number of bytes from which the value is decoded
	 *** @return The decoded double value, or 0L
	 **/
	
	public double getDouble(int length) {
		// 'length' must be at least 4
		int maxLen = ((this.index + length) <= this.size) ? length
				: (this.size - this.index);
		if (maxLen <= 0) {
			// nothing to read
			return 0.0;
		} else {
			byte n[] = getBytes(maxLen);
			double val = decodeDouble(n, 0);
			return val;
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Read a string from the bytesArray. The string is read until (whichever 
	 * comes first):
	 *** <ol>
	 * <li><code>length</code> bytes have been read</li>
	 *** <li>a null (0x00) byte is found (if <code>varLength==true</code>)</li>
	 *** <li>end of data is reached</li>
	 * </ol>
	 *** 
	 * @param length
	 *            The maximum length to read
	 *** @param varLength
	 *            Boolean telling if the string can be variable in length (stop
	 *            on a null)
	 *** @return The read String
	 **/
	// 2ç§�getStringç‰ˆæœ¬
	public String getString(int length, boolean varLength) {
		// Read until (whichever comes first):
		// 1) length bytes have been read
		// 2) a null (0x00) byte is found (if 'varLength==true')
		// 3) end of data is reached
		int m;
		if (length > this.size)
			length = this.size;
		if (varLength) {
			// look for the end-of-data, or a terminating null (0x00)
			for (m = 0; m < length && (this.bytesArray[this.index + m] != 0); m++)
				;
		} else {
			// look for end of data only
			m = length;
		}
		return getString(m);
	}

	/**
	 *** Converts the byte array to a String based on the US-ASCII character set.
	 *å­—ç¬¦æ•°ç»„è½¬æ�¢æˆ�å­—ç¬¦ä¸²
	 *** 
	 *** @param len
	 *            The number of bytes to convert to a String
	 *** @return The String representation of the specified byte array
	 **/
	public String getString(int len) {
		if (bytesArray == null) {
			return null; // what goes around ...
		} else if (len <= 0) {
			return ""; // empty length
		} else {
			int ofs = this.index;
			this.index += len;
			try {
				// Convert bytes to a string.
				byte[] newData = new byte[len];
				System.arraycopy(bytesArray, ofs, newData, 0, len);
				int i;
				for (i = 0; i < len; i++) {
					if (newData[i] == 0)
						break;
				}
				ByteBuffer bb = Charset.forName("US-ASCII").encode(
						CharBuffer
								.wrap(new String(bytesArray, ofs, i, charset)));
				return Charset.defaultCharset().decode(bb).toString();
			} catch (Throwable t) {
				// This should NEVER occur (at least not because of the charset)
				return "";
			}
		}
	}

	/**
	 *** Add a <code>long</code> value to the bytesArray
	 *** 
	 * @param val
	 *            The value to write
	 *** @param wrtLen
	 *            The number of bytes to write the value into
	 *** @return The number of bytes written
	 **/
	public int putLong(long val, int wrtLen) {
		/* check for nothing to write */
		if (wrtLen <= 0)
			return 0;
		/* write long */
		byte n[] = encodeLong(wrtLen, val, true);
		return putBytes(n);
	}

	/**
	 *** Add a unsigned <code>long</code> value to the bytesArray. Same as
	 * writeLong()
	 *** 
	 * @param val
	 *            The value to write
	 *** @param wrtLen
	 *            The number of bytes to write the value into
	 *** @return The number of bytes written
	 **/
	public int putULong(long val, int wrtLen) {
		/* check for nothing to write */
		if (wrtLen <= 0) {
			System.err.println("Not adding, nothing to add");
			return 0;
		}
		/* write long */
		byte n[] = encodeLong(wrtLen, val, !(val >= 0));
		return putBytes(n);
	}

	/**
	 *** Calculate Add a <code>timestamp</code> value to the bytesArray
	 *** 
	 *** @return The long value of timeStamp
	 **/
	public long putTimeStamp() {
		byte n[] = encodeTimeStamp(System.currentTimeMillis());
		// byte n[] = encodeTimeStamp(0);
		putBytes(n);
		return decodeTimeStamp(n);
	}

	/**
	 *** Add a <code>timestamp</code> value to the bytesArray
	 *** 
	 *** @param t
	 *            float timestamp <code>n</code>
	 *** @return The number of bytes written
	 **/
	public int putTimeStamp(float t) {
		long seconds = (long) t;
		long fraction = (long) (t * (float) 0x100000000L);
		byte n[] = encodeTimeStamp(seconds, fraction);
		return putBytes(n);
	}

	/**
	 *** Calculate and Add a <code>crc</code> value to the bytesArray
	 *** 
	 * @param buf
	 *            The bytes to callculate the crc from
	 *** @param len
	 *            The number of bytes to calaculate from
	 *** @param crc
	 *            actual crc value <code>n</code>
	 *** @return The long value of crc
	 **/
	public long putCrc(byte[] buf, int len, long crc) {
		crc = crc64(buf, len, crc);
		putULong(crc, 8);
		return crc;
	}

	/**
	 *** Add a <code>double</code> value to the bytesArray
	 *** 
	 * @param val
	 *            The value to write
	 *** @param wrtLen
	 *            The number of bytes to write the value into
	 *** @return The number of bytes written
	 **/
	public int putDouble(double val, int wrtLen) {
		// 'wrtLen' should be either 4 or 8

		/* check for nothing to write */
		if (wrtLen <= 0) {
			// nothing to write
			return 0;
		}
		/* write float/double */
		if (wrtLen < 4) {
			// not enough bytes to encode float/double
			return 0;
		}
		/* write float/double */
		if (wrtLen < 8) {
			// 4 <= wrtLen < 8 [float]
			byte n[] = encodeDouble(4, val);
			return putBytes(n);
		} else {
			// 8 <= wrtLen [double]
			byte n[] = encodeDouble(8, val);
			return putBytes(n);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Add a <code>short</code> value to the bytesArray
	 *** 
	 * @param val
	 *            The value to write
	 *** @param wrtLen
	 *            The number of bytes to write the value into
	 *** @return The number of bytes written
	 **/
	public int putShort(short val) {
		/* write int */
			byte n[] = ByteBuffer.allocate(2).putShort(val).array();
			return putBytes(n);
	}

	// ------------------------------------------------------------------------
	
	/**
	 *** Add a string to the bytesArray. Writes until either <code>wrtLen</code>
	 *** bytes are written or the string terminates
	 *** 
	 * @param s
	 *            The string to write
	 *** @return The number of bytes written
	 **/
	public int putString(String s) {
		/* empty string ('maxLen' is at least 1) */
		if ((s == null) || s.equals("")) {
			byte n[] = new byte[1];
			n[0] = (byte) 0; // string terminator
			return putBytes(n);
		}
		/* write string bytes, and adjust pointers */
		return putBytes(s.getBytes(charset));
	}

	// ------------------------------------------------------------------------

	/**
	 *** Encodes a <code>long</code> value into bytes
	 *** 
	 *** @param len
	 *            The number of bytes to encode the value to
	 *** @param val
	 *            The value to encode
	 *** @param signed
	 *            true if signed false if unsigned
	 *** 
	 * @return the bytes array created from the value
	 **/
	public byte[] encodeLong(int len, long val, boolean signed) {
		double signMax = Math.pow(2, (len * 8) - 1);
		if (len < 0) {
			throw new RuntimeException("len < 0");
		}
		if (signed && val > signMax) {
			throw new RuntimeException("signed && val>signMax " + signMax);
		}
		if (signed && val < -signMax) {
			throw new RuntimeException("signed && val<-signMax -" + signMax);
		}
		if (!signed && val < 0) {
			throw new RuntimeException("!signed && val<0");

		}
		if (!signed && val > 2 * signMax) {
			throw new RuntimeException("!signed && val>2*signMax " + 2
					* signMax);
		}

		byte data[] = new byte[len];
		long n = val;
		for (int i = (len - 1); i >= 0; i--) {
			data[i] = (byte) (n & 0xFF);
			n >>>= 8;
		}
		return data;
	}

	// ------------------------------------------------------------------------

	/**
	 *** Decodes a <code>long</code> value from bytes
	 *** 
	 * @param data
	 *            The byte array to decode the value from
	 *** @param ofs
	 *            The offset into <code>data</code> to start decoding from
	 *** @param signed
	 *            If the encoded bytes represent a signed value
	 *** @return The decoded value, or 0L
	 **/
	public long decodeLong(byte data[], int ofs, boolean signed) {
		if (data != null) {
			int len = data.length - ofs;
			long n = (signed && ((data[ofs] & 0x80) != 0)) ? -1L : 0L;
			for (int i = ofs; i < ofs + len; i++) {
				n = (n << 8) | ((long) data[i] & 0xFF);
			}
			return n;
		} else {
			return 0L;
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Encodes a <code>double</code> value into bytes
	 *** 
	 *** @param len
	 *            The number of bytes to encode the value to
	 *** @param val
	 *            The value to encode
	 *** 
	 * @return the bytes array created from the value
	 **/
	public byte[] encodeDouble(int len, double val) {
		// 'len' must be at least 4
		if (len >= 4) {
			byte data[] = new byte[len];
			int flen = (len >= 8) ? 8 : 4;
			long n = (flen == 8) ? Double.doubleToRawLongBits(val)
					: (long) Float.floatToRawIntBits((float) val);
			// Big-Endian order
			for (int i = (flen - 1); i >= 0; i--) {
				data[i] = (byte) (n & 0xFF);
				n >>>= 8;
			}
			return data;
		} else {
			System.err.println("Wrong legnth byte array");
			return new byte[len];
		}
	}

	// ------------------------------------------------------------------------

	/**
	 *** Decodes a <code>double</code> value from bytes, using IEEE 754 format
	 *** 
	 * @param data
	 *            The byte array from which to decode the <code>double</code>
	 *            value
	 *** @param ofs
	 *            The offset into <code>data</code> to start decoding
	 *** @return The decoded value, or 0L
	 **/
	public double decodeDouble(byte data[], int ofs) {
		// 'len' must be at lest 4
		if ((data != null) && (data.length >= 4)) {
			int len = data.length - ofs;
			int flen = (len >= 8) ? 8 : 4;
			long n = 0L;
			// Big-Endian order
			// { 0x01, 0x02, 0x03, 0x04 } -> 0x01020304
			for (int i = ofs; i < ofs + flen; i++) {
				n = (n << 8) | ((long) data[i] & 0xFF);
			}
			if (flen == 8) {
				return Double.longBitsToDouble(n);
			} else {
				return (double) Float.intBitsToFloat((int) n);
			}
		} else {
			return 0.0;
		}
	}

	/**
	 * Enlarge this byte vector so that it can receive n more bytes.
	 * 
	 * @param size
	 *            number of additional bytes that this byte vector should be
	 *            able to receive.
	 */
	private void enlarge(final int size) {
		byte[] newData = new byte[bytesArray.length + size];
		System.arraycopy(bytesArray, 0, newData, 0, bytesArray.length);
		bytesArray = newData;
	}

	// Inspire from www.pps.jussieu.fr/~balat/Timestamp.java spec from rfc2030
	// but starting
	// date 00:00:00 January 1, 1970, UTC (instead of 1900)
	/**
	 * Encodes a time in millisec in a 8 bytes array
	 *** 
	 * @param t
	 *            the time in millis from 00:00:00 January 1, 1970, UTC value
	 *** @return The byte array containing the timestamp
	 */
	public byte[] encodeTimeStamp(long t) {
		long s, f; // s = seconds part, f = fraction part
		s = (t / 1000L);
		byte array[] = new byte[8];
		array[0] = (byte) (s >> 24);
		array[1] = (byte) ((s >> 16) & 0xFF);
		array[2] = (byte) ((s >> 8) & 0xFF);
		array[3] = (byte) (s & 0xFF);
		f = (t % 1000L) * 1000L * 4295; // 4295 = approximation de 10^-6*2^32
		array[4] = (byte) (f >> 24);
		array[5] = (byte) ((f >> 16) & 0xFF);
		array[6] = (byte) ((f >> 8) & 0xFF);
		array[7] = (byte) (f & 0xFF);
		return array;
	}

	// Inspire from www.pps.jussieu.fr/~balat/Timestamp.java spec from rfc2030
	// but starting
	// date 00:00:00 January 1, 1970, UTC (instead of 1900)
	/**
	 * Encodes a time in millisec in a 8 bytes array
	 *** 
	 * @param s
	 *            the seconds from time in millis from 00:00:00 January 1, 1970,
	 *            UTC value
	 * @param f
	 *            the fraction of time in millis from 00:00:00 January 1, 1970,
	 *            UTC value
	 *** @return The byte array containing the timestamp
	 */
	public byte[] encodeTimeStamp(long s, long f) {
		// s = seconds part, f = fraction part
		byte array[] = new byte[8];
		array[0] = (byte) (s >> 24);
		array[1] = (byte) ((s >> 16) & 0xFF);
		array[2] = (byte) ((s >> 8) & 0xFF);
		array[3] = (byte) (s & 0xFF);
		array[4] = (byte) (f >> 24);
		array[5] = (byte) ((f >> 16) & 0xFF);
		array[6] = (byte) ((f >> 8) & 0xFF);
		array[7] = (byte) (f & 0xFF);
		return array;
	}

	/**
	 * Will read a 8 bytes array and return it as a milli sec time stamp.
	 *** 
	 * @param array
	 *            the array to decode TimeStamp from
	 * 
	 *** @return The long time stamp value
	 */
	public long decodeTimeStamp(byte[] array) {
		long seconds = decodeLong(array, 0, false);
		long fraction = decodeLong(array, 4, false);
		long ms = (long) ((double) fraction / (double) 1000L / (double) 4295 + 0.5);
		return seconds * 1000L + ms;
	}

	// ------------------------------------------------------------------------

	/**
	 * Converts an unsigned byte to a short. By default, Java assumes that a
	 * byte is signed.
	 * 
	 * @param b
	 *            the byte to get short from
	 * 
	 *** @return The short value
	 */
	public short unsignedByteToShort(byte b) {
		if ((b & 0x80) == 0x80)
			return (short) (128 + (b & 0x7f));
		else
			return (short) b;
	}

	/**
	 * Calculates CRC from a byte buffer based on the polynom specified in
	 * ECMA-182.
	 * 
	 *** Calculate a <code>crc</code> value Adds from a partial byte array to the
	 * data checksum.
	 *** 
	 * @param buffer
	 *            The bytes to callculate the crc from
	 *** @param len
	 *            The number of bytes to calculate from
	 *** @param crc
	 *            actual crc value <code>n</code>
	 *** @return The long crc64 value
	 **/
	public static long crc64(byte[] buffer, int len, long crc) {
			int i=0;
			int tableIndex=0;
			while (i<len){
				char unsignedCharCrc56 = (char)((crc>>56) & 0xFF);//Java does not support unsigned data types so this is workaround to convert byte to unsigned int-char
				char unsignedCharBuf = (char)buffer[i];
				unsignedCharBuf = (char)(unsignedCharBuf & 0xFF); //Java does not support unsigned data types so this is workaround to convert byte to unsigned int-char
				
				tableIndex = Math.abs( unsignedCharBuf ^ unsignedCharCrc56 );
				
		        crc = CRC_TABLE[tableIndex] ^ (crc << 8);
				//System.out.println( "data=" + shrtBfr + " , unsigned crc>>56=" + (int)shrtcrc56 +  ", Index=" + tableIndex + ", crc=" + crc) ;
		        i++;
		    }
		    return crc;

		
		/*int i=0;
			while (i<len){
				ByteBuffer b = ByteBuffer.allocate(Long.SIZE);
				b.putLong(crc >> 56);
				byte lastByte = b.get(16); //get Last byte in Long CRC
				
				int tableIndex = Math.abs(buffer[i] ^ Math.abs(lastByte));
		        crc = CRC_TABLE[tableIndex] ^ (crc << 8);
		        i++;
		    }
		    return crc;*/
		// Log.debug("crc len " + len);
	/*	for (int i = 0; i < len; i++) {
			crc = next_crc(crc, buffer[i]);
			// Log.debug("crc 0x" + Long.toHexString(crc));
		}*/
		//return crc;
	}
/*	public long crc64(byte[] buffer, int len, long crc) {
		int index=1;
	//	ByteList b = new ByteList(buffer);
		while (len > 0)
	    {
			crc = CRC_TABLE[buffer[index-1]^(byte)(crc >> 56)]^(crc << 8);
	        index++;
	        len--;
	     }
		return crc;
	}*/
	
	/**
	 *** Calculate a <code>crc</code> value Adds from a complete byte array to the
	 * data checksum.
	 *** 
	 * @param buf
	 *            The bytes to callculate the crc from
	 *** @param crc
	 *            actual crc value <code>n</code>
	 *** @return The long crc64 value
	 **/
	public long crc64(byte[] buf, long crc) {
		return crc64(buf, buf.length, crc);
	}

	/**
	 * Calculates the next crc value.
	 *** 
	 *** @param crc
	 *            actual crc value <code>n</code>
	 *** @param ch
	 *            The next byte
	 *** @return The long crc64 value
	 **/
	public long next_crc(long crc, byte ch) {
		return (crc >>> 8) ^ CRC_TABLE[((int) crc ^ ch) & 0xff];
	}
	
	@Override 
	public String toString(){
		String s="[";
		for(int i=0;i<this.size;i++){
			s+=" "+bytesArray[i];
		}
		return s+"]";
	}
	
	public static void printBytes(byte b[]){
		for(int i=0;i<b.length;i++){
			System.out.print( b[i] + " " );
		}
		System.out.println("");
	}

}
