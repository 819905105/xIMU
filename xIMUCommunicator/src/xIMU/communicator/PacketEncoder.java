package xIMU.communicator;

/**
 * This class encodes and decodes packet byte arrays to their Android representation.
 * 
 * An encoded packet is a byte array with the MSB in each byte cleared, except for the last byte, in
 * which the MSB is set to indicate the end of the packet.
 * 
 * @author Ralf D Perpeet (rdp07@imperial.ac.uk), 
 * analogous to the x-IMU API by Sebastian O.H. Madgwick
 */
public class PacketEncoder {
  /** 
   * Encodes packet with consecutive right shifts so that the MSB of each encoded byte is clear. 
   * The MSB of the final byte is set to indicate the end of the packet. 
   */
  public byte[] encode(final byte[] decodedPacket) {
    byte[] encodedPacket = 
        new byte[(int) (Math.ceil(((((float) decodedPacket.length) * 1.125f)) + 0.125f))];
    byte[] shiftRegister = new byte[encodedPacket.length];

    // Copy encoded packet to shift register.
    System.arraycopy(decodedPacket, 0, shiftRegister, 0, decodedPacket.length);

    for (int i = 0; i < encodedPacket.length; i++) {
      // Right shift to clear MSB of byte i
      shiftRegister = rightShiftByteArray(shiftRegister);
      
      // Store encoded byte i.
      encodedPacket[i] = shiftRegister[i];
      
      // Clear byte i in shift register.
      shiftRegister[i] = 0; 
    }
    // Set MSB of framing byte.
    encodedPacket[encodedPacket.length - 1] |= 0x80; 
    return encodedPacket;
  }

  /** Right shifts a byte array by 1 bit. The LSB of byte x becomes the MSB of byte x+1. */
  // Package visibility for test.
  static byte[] rightShiftByteArray(final byte[] byteArray) {
    byteArray[byteArray.length - 1] >>= 1;
    for (int i = byteArray.length - 2; i >= 0; i--) {
      if ((byteArray[i] & 0x01) == 0x01) {
        byteArray[i + 1] |= 0x80;
      } else {
      	// >>> doesn't seem to do the job, so we'll manually set the MSB to 0 in this case.
      	byteArray[i + 1] &= 0x7F;
      }
      byteArray[i] >>= 1;
    }
    // And set the MSB of the first byte to 0 as well.
    byteArray[0] &= 0x7F;
    return byteArray;
  }
  
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
    return decodedPacket;
  }

  /** Left shifts a byte array by 1 bit. The MSB of byte x becomes the LSB of byte x-1. */
  // Package visibility for test.
  static byte[] leftShiftByteArray(final byte[] byteArray) {
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
