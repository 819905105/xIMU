package imu.communicator.data.processor;

import imu.communicator.data.processor.Enums.Qvals;

public class PacketConverter {

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

		Enums.Type type = Enums.Type.typeForOrdinal(decodedPacket[0]);
		if (type == null) {
			System.out.println("Packet not of type");
		}

		if (type != null && decodedPacket.length > 1)
		{
			//System.out.println(type.toString());
			new PackageSplitter(type, decodedPacket);
		}

	}

	/** Returns 3 shorts out of 6 bytes from the index in the binary. */
	public float[] getFloatsFromIndex(final byte[] binary, final int index, final Qvals qval) {
		float x = twoBytesToFloat(binary, index, qval);
		float y = twoBytesToFloat(binary, index + 2, qval);
		float z = twoBytesToFloat(binary, index + 4, qval);
		return new float[] { x, y, z };
	}

	/** Returns 3 shorts out of 6 bytes from the index in the binary. */
	public float[] getFloats(final byte[] binary, final Qvals qval) {

		float[] data = new float[((binary.length - 2) / 2)];	  
		int count = 0;

		for (int i = 1; i < (binary.length - 1); i = i + 2) {
			data[count] = twoBytesToFloat(binary, i, qval);
			count = count + 1;
		}

		return data;
	}

	public float twoBytesToFloat(final byte[] binary, final int index, final Qvals qval) {
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
