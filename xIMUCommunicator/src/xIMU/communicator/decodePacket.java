package xIMU.communicator;

import xIMU.communicator.dataTypes.Qvals;

public class decodePacket {

	/**
	 * Decodes a packet with consecutive left shifts, so that the MSB of each encoded byte is removed.
	 */
	public byte[] decode(final byte[] encodedPacket) {

		byte[] decodedPacket = new byte[(int) (Math.floor(((float) encodedPacket.length - 0.125f) / 1.125f))];
		byte[] shiftRegister = new byte[encodedPacket.length];
		for (int i = shiftRegister.length - 1; i >= 0; i--) {
			shiftRegister[i] = encodedPacket[i];
			shiftRegister = leftShiftByteArray(shiftRegister);
		}
		System.arraycopy(shiftRegister, 0, decodedPacket, 0, decodedPacket.length);
		//System.out.println(Arrays.toString(decodedPacket));

		return decodedPacket;
	}

	/** Left shifts a byte array by 1 bit. The MSB of byte x becomes the LSB of byte x-1. */
	// Package visibility for test.
	private byte[] leftShiftByteArray(final byte[] byteArray) {
		byteArray[0] <<= 1;
		for (int i = 1; i < byteArray.length; i++) {
			if ((byteArray[i] & 0x80) == 0x80) {
				byteArray[i - 1] |= 0x01;
			}
			byteArray[i] <<= 1;
		}
		return byteArray;
	}

	/** Processes incoming raw packets and returns the interpreted packet. 
	 * @return */
	public void convertPacket(final byte[] decodedPacket) {

		byte checksum = 0;
		for (int i = 0; i < (decodedPacket.length - 1); i++) checksum += decodedPacket[i];
		if (checksum != decodedPacket[decodedPacket.length - 1])
		{
			System.out.println("Invalid checksum.");
		}

		if (decodedPacket.length <= 1) {
			System.out.println("Packet too small");
		}

		dataTypes.Type type = dataTypes.Type.typeForOrdinal(decodedPacket[0]);
		if (type == null) {
			System.out.println("Packet not of type");
		}
		
	    float[] gyroscope = getFloatsFromIndex(decodedPacket, 1, dataTypes.Qvals.CalibratedGyro);
	    float[] accelerometer = getFloatsFromIndex(decodedPacket, 7, dataTypes.Qvals.CalibratedAccel);
	    float[] magnetometer = getFloatsFromIndex(decodedPacket, 13, dataTypes.Qvals.CalibratedMag); 
	    
//	    System.out.println("x : " + gyroscope[0] + " y : " + gyroscope[1] + " z : " + gyroscope[2]);
	    System.out.println("x : " + accelerometer[0] + " y : " + accelerometer[1] + " z : " + accelerometer[2]);
//	    System.out.println("x : " + magnetometer[0] + " y : " + magnetometer[1] + " z : " + magnetometer[2]);

	}
	
	  /** Returns 3 shorts out of 6 bytes from the index in the binary. */
	  private float[] getFloatsFromIndex(final byte[] binary, final int index, final Qvals qval) {
	    float x = twoBytesToFloat(binary, index, qval);
	    float y = twoBytesToFloat(binary, index + 2, qval);
	    float z = twoBytesToFloat(binary, index + 4, qval);
	    return new float[] { x, y, z };
	  }

	  protected float twoBytesToFloat(final byte[] binary, final int index, final Qvals qval) {
	    return binaryToFloat(concatenate(binary[index], binary[index + 1]), qval);
	  }

	/** Returns the concatenated short of two bytes. */
	public static short concatenate(final byte msb, final byte lsb){
		
		//short combined = (short) ((msb << 8 ) | (lsb & 0xff));
		short combined = (short)((short)((short)msb << 8) | ((short)lsb & 0xff));
		//short combined = (short)((short)((short)msb << 8) | (short)lsb);

		return combined;
	}
	
	  public static float binaryToFloat(final short fixedValue, final Qvals qval) {
	    return ((float) (fixedValue) / (float) (1 << (qval.numFractionalBits())));
	  }

}
