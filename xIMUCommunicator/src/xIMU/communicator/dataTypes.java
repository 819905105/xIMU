package xIMU.communicator;

public class dataTypes {
	
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
	
	public enum Qvals {
	      CalibratedBatt (12),
	      CalibratedTherm (8),
	      CalibratedGyro (4),
	      CalibratedAccel (11),
	      CalibratedMag (11),
	      Quaternion (15),
	      BattSensitivity (5),
	      BattBias (8),
	      ThermSensitivity (6),
	      ThermBias (0),
	      GyroSensitivity (10),
	      GyroBias (8),
	      GyroBiasTempSens (10),
	      AccelSensitivity (4),
	      AccelBias (8),
	      MagSensitivity (4),
	      MagBias (8),
	      MagHardIronBias (11),
	      AlgorithmKp (8),
	      AlgorithmKi (14),
	      AlgorithmInitKp (6),
	      AlgorithmInitPeriod (8),
	      CalibratedAnalogueInput (12);
	      
	      private int numFractionalBits;

	      Qvals(final int numBits) {
	        this.numFractionalBits = numBits;
	      }
	      
	      public int numFractionalBits() {
	        return numFractionalBits;
	      }
	      
	  }

}
