/*******************************************************************************
 * Copyright or � or Copr. CNES
 *
 * This software is a computer program whose purpose is to provide a 
 * framework for the CCSDS Mission Operations services.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package org.ccsds.moims.mo.malspp.test.datatype;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.test.datatype.DataTypeScenario;
import org.ccsds.moims.mo.mal.test.datatype.TestData;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacket;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacketHeader;
import org.ccsds.moims.mo.malspp.test.sppinterceptor.SPPInterceptor;
import org.ccsds.moims.mo.malspp.test.util.BufferReader;
import org.ccsds.moims.mo.malspp.test.util.SecondaryHeader;
import org.ccsds.moims.mo.malspp.test.util.TestHelper;
import org.ccsds.moims.mo.testbed.util.LoggingBase;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class MalSppDataTypeTest extends DataTypeScenario {
	
	public final static Logger logger = fr.dyade.aaa.common.Debug
		  .getLogger(MalSppDataTypeTest.class.getName());
	
	private SpacePacketHeader primaryHeader;

	private SecondaryHeader secondaryHeader;
	
	private BufferReader bufferReader;
	
	public String explicitDurationTypeWorks() throws MALInteractionException, MALException
  {
	  if (logger.isLoggable(BasicLevel.DEBUG)) {
      logger.log(BasicLevel.DEBUG, "explicitDurationTypeWorks()");
    }
	  try {
	    return super.explicitDurationTypeWorks();
	  } catch (Throwable error) {
	    if (logger.isLoggable(BasicLevel.DEBUG)) {
	      logger.log(BasicLevel.DEBUG, "", error);
	    }
	    error.printStackTrace();
	    return null;
	  }
  }
	
	public boolean selectReceivedPacketAt(int index) {
	  if (logger.isLoggable(BasicLevel.DEBUG)) {
      logger.log(BasicLevel.DEBUG, "selectReceivedPacketAt(" + index + ')');
    }
		LoggingBase.logMessage("selectReceivedPacketAt(" + index + ')');
		LoggingBase.logMessage("ReceivedPacketCount=" + SPPInterceptor.instance().getReceivedPacketCount());
		SpacePacket packet = SPPInterceptor.instance().getReceivedPacket(index);
		return selectPacket(packet);
	}
	
	public boolean selectSentPacketAt(int index) {
	  if (logger.isLoggable(BasicLevel.DEBUG)) {
      logger.log(BasicLevel.DEBUG, "selectSentPacketAt(" + index + ')');
    }
		LoggingBase.logMessage("selectSentPacketAt(" + index + ')');
		LoggingBase.logMessage("SentPacketCount=" + SPPInterceptor.instance().getSentPacketCount());
		SpacePacket packet = SPPInterceptor.instance().getSentPacket(index);
		return selectPacket(packet);
	}
	
	private boolean selectPacket(SpacePacket packet) {
	  if (logger.isLoggable(BasicLevel.DEBUG)) {
      logger.log(BasicLevel.DEBUG, "selectSentPacketAt(" + packet + ')');
    }
		byte[] packetBody = packet.getBody();
		LoggingBase.logMessage("packetBody.length=" + packetBody.length);
		primaryHeader = packet.getHeader();
		LoggingBase.logMessage("primaryHeader=" + primaryHeader);
		secondaryHeader = new SecondaryHeader();
		try {
			bufferReader = new BufferReader(packetBody, 0, true);
	    TestHelper.decodeSecondaryHeader(secondaryHeader, bufferReader, 
	    		packet.getHeader().getSequenceFlags(), BufferReader.TimeFormat.CUC);
	    LoggingBase.logMessage("secondaryHeader=" + secondaryHeader);
    } catch (Exception e) {
    	if (logger.isLoggable(BasicLevel.WARN)) {
        logger.log(BasicLevel.WARN, "", e);
    	}
    	LoggingBase.logMessage(e.toString());
    	return false;
    }
		return true;
	}
	
	public int presenceFlagIs() {
		return bufferReader.read();
	}
	
	public long areaNumberIs() {
		return bufferReader.read16();
	}
	
	public long serviceNumberIs() {
		return bufferReader.read16();
	}
	
	public int versionIs() {
		return bufferReader.read();
	}
	
	public int typeNumberIs() {
		return bufferReader.read24();
	}
	
	public int listSizeIs() throws Exception {
		return bufferReader.readUnsignedVarInt();
	}
	
	public String stringFieldIs() throws Exception {
		String s = bufferReader.readString();
		LoggingBase.logMessage("String=" + s);
		return s;
	}
	
	public int integerFieldIs() throws Exception {
		return bufferReader.readInteger();
	}
	
	public boolean booleanFieldIs() throws Exception {
		return bufferReader.readBoolean();
	}
	
	public int enumeratedIs() {
		return bufferReader.read();
	}
	
	public int attributeTagIs() throws Exception {
		return bufferReader.readUnsignedVarInt();
	}

	public boolean checkDuration() {
		try {
		  return checkData(bufferReader.readDuration(), TestData.testDuration);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkIdentifier() {
		try {
		  return checkData(bufferReader.readIdentifier(), TestData.testIdentifier);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkUri() {
		try {
		  return checkData(bufferReader.readUri(), TestData.testURI);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkBlob() {
		try {
		  return checkData(bufferReader.readBlob(), TestData.testBlob);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkBoolean() {
		try {
		  return checkData(bufferReader.readBoolean(), TestData.testBoolean);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean booleanIs() {
    try {
      return bufferReader.readBoolean();
    } catch (Exception e) {
      return logError(e);
    }
  }
	
	public boolean checkOctet() {
		try {
		  return checkData(bufferReader.readOctet(), TestData.testOctet);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkDouble() {
		try {
		  return checkData(bufferReader.readDouble(), TestData.testDouble);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkFloat() {
		try {
		  return checkData(bufferReader.readFloat(), TestData.testFloat);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkInteger() {
		try {
		  return checkData(bufferReader.readInteger(), TestData.testInteger);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkLong() {
		try {
		  return checkData(bufferReader.readLong(), TestData.testLong);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkShort() {
		try {
		  return checkData(bufferReader.readShort(), TestData.testShort);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkString() {
		try {
		  return checkData(bufferReader.readString(), TestData.testString);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkUoctet() {
		try {
		  return checkData(bufferReader.readUOctet(), TestData.testUOctet);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkUinteger() {
		try {
		  return checkData(bufferReader.readUInteger(), TestData.testUInteger);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkUlong() {
		try {
		  return checkData(bufferReader.readULong(), TestData.testULong);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkTime() {
		try {
		  return checkData(bufferReader.readTime(BufferReader.TimeFormat.CUC), TestData.testTime);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkFineTime() {
		try {
		  return checkData(bufferReader.readFineTime(BufferReader.TimeFormat.CUC), TestData.testFineTime);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	public boolean checkUshort() {
		try {
		  return checkData(bufferReader.readUShort(), TestData.testUShort);
		} catch (Exception e) {
    	return logError(e);
    }
	}
	
	private boolean checkData(Object readData, Object expectedData) {
		boolean res = expectedData.equals(readData);
		if (! res) {
			LoggingBase.logMessage(expectedData + " != " + readData);
		}
		return res;
	}
	
	private boolean logError(Exception e) {
		if (logger.isLoggable(BasicLevel.WARN)) {
      logger.log(BasicLevel.WARN, "", e);
  	}
  	LoggingBase.logMessage(e.toString());
  	return false;
	}
	
	public boolean resetSppInterceptor() {
	  if (logger.isLoggable(BasicLevel.DEBUG)) {
      logger.log(BasicLevel.DEBUG, "resetSppInterceptor()");
    }
		LoggingBase.logMessage("SentPacketCount=" + SPPInterceptor.instance().getSentPacketCount());
		LoggingBase.logMessage("ReceivedPacketCount=" + SPPInterceptor.instance().getReceivedPacketCount());
		SPPInterceptor.instance().reset();
		// Need to reset the tables
		//TransportInterceptor.instance().resetReceiveCount(ip);
		return true;
	}
	
}