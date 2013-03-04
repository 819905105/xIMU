package imu.communicator.data.processor;

public class PacketDecoder {

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

}
