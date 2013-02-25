package xIMU.communicator;

/**
 * An x-IMU packet.
 *
 * This is the base class for all packets in the x-IMU data protocol. It performs sanity checks
 * when decoding the header.
 *
 * @author Richard Woodward (rw1709@imperial.ac.uk)
 */
public abstract class Packet {
  /** The type of the packet. */
  public static enum Type {
    ErrorMessage,
    Command,
    ReadRegister,
    WriteRegister,
    ReadDateTime,
    WriteDateTime,
    RawBattThermData,
    CalBattThermData,
    RawInertialMagData,
    CalInertialMagData,
    QuaternionData,
    CalDigitalIOData,
    RawDigitalIOData,
    CalAnalogueData,
    RawAnalogueData,
    PWMData,
    ADXL345Data;

    /**
     * Returns the packet type at the specified ordinal position in the enum. 
     * @return the packet type at the ordinal position or null, if the ordinal is out of bounds.
     */
    public static Type typeForOrdinal(final int ordinal) {
      Type[] types = Type.class.getEnumConstants();
      if (ordinal >= 0 && ordinal < types.length) {
        return Type.class.getEnumConstants()[ordinal];
      } else {
        return null;
      }
    }
    
    public static byte unsignedByteForType(Type type) {
    	int packetNumber = type.ordinal();
    	return (byte) (Byte.MIN_VALUE + ((byte) packetNumber));
    }
  }

  /** Constructor to generate the packet from a binary. */
  public Packet(final byte[] binary) {
    decode(binary);
  }
  
  /** Constructor to create the data packet which is then later converted to a binary packet. */
  public Packet() {
    super();
  }
  
  /** 
   * Decodes the binary packet. 
   * @throws IncompatiblePacketTypeException 
   * @throws InvalidPacketLengthException 
   * @throws InvalidPacketChecksumException
   */
  protected void decode(final byte[] binary) {
    // Parse header.
    // Verify that the packet is of this type. 
    byte type = binary[0];
    if (getType().ordinal() != type) {
      //throw new IncompatiblePacketTypeException();
    }
    
    // Verify the minimum and maximum length of this packet.
    if (!hasValidLength(binary.length)) {
      //throw new InvalidPacketLengthException();
    }
    
    // Perform checksum.
    if (!hasValidChecksum(binary)) {
      //throw new InvalidPacketChecksumException();
    }
    
    // Parse payload.
    extractPayload(binary);
  }
  
  /** Returns the type of this packet. */
  public abstract Type getType();
  
  /** Returns true iff the type of this packet is the type specified. */
  public boolean isType(Type type) {
    return getType().equals(type);
  }
  
  /** Encodes this packet in binary form. */
  public abstract byte[] getBinary(); 
  
  /** Returns true iff the packet has a valid length. */
  private boolean hasValidLength(final int packetLength) {
    return (packetLength >= 4) && (packetLength <= 23) && hasValidPacketLength(packetLength);
  }

  public abstract boolean hasValidPacketLength(final int packetLength);

	/** Returns true iff the calculated checksum for this packet matches the given checksum. */
  private static boolean hasValidChecksum(final byte[] binary) {
    int expectedChecksum = binary[binary.length - 1];
    int checksum = getChecksum(binary);
    return checksum == expectedChecksum;
  }
  
  /** Returns the checksum for the given binary, excluding the last byte in the calculation. */
  // Package visibility for test.
  static byte getChecksum(final byte[] binary) {
    byte checksum = 0;
    for (int i = 0; i < binary.length - 1; i++) {
      checksum += binary[i];
    }
    return checksum;
  }
  
  /** Inserts the checksum of the decoded packet into the last byte. */
  protected byte[] insertChecksum(final byte[] decodedPacket) {
    if (decodedPacket.length == 0) {
      return new byte[] {0};
    }
    // Zero current checksum.
    decodedPacket[decodedPacket.length - 1] = 0;
    
    int checksum = 0;
    
    // Accumulate checksum.
    for (int i = 0; i < (decodedPacket.length - 1); i++) {
    	checksum += unsignedByteToInt(decodedPacket[i]);
    	//decodedPacket[decodedPacket.length - 1] += decodedPacket[i];
    }
    decodedPacket[decodedPacket.length - 1] = intToPseudoUnsignedByte(checksum);
    return decodedPacket;
  }

  /** Decodes the packet payload form the original binary after all sanity checks have passed. */
  protected abstract void extractPayload(final byte[] binary);
  
  /** Returns the unsigned byte for the integer. */
	protected byte intToPseudoUnsignedByte(final int n) {
		int m = n % 256;
		if (m < 128) {
			return (byte) m;
		}
		return (byte) (m - 256);
	}
	
	protected int unsignedByteToInt(final byte b) {
    return (int) b & 0xFF;
	}
}
