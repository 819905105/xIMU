package xIMU.communicator;

public class PrimitiveConversion {
  /// <summary>
  /// Number of fractional bits used by fixed-point representations.
  /// </summary>
  /// <remarks>
  /// A matching enumeration exists in firmware source.
  /// </remarks> 
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
  
  /** Returns the concatenated short of two bytes. */
  public static short concatenate(final byte msb, final byte lsb){
      return (short)((short)((short)msb << 8) | (short)lsb);
  }

  /** 
   * Returns the 16 bit float of the binary string, shifting by q.
   * 
   * Q is specified in the x-IMU protocol for each data type.
   * 
   * Analogous to FixedFloat in Seb Madwick's x-IMU API.
   */
  public static float binaryToFloat(final short fixedValue, final Qvals qval) {
    return ((float) (fixedValue) / (float) (1 << (qval.numFractionalBits())));
  }
  
  /** 
   * Returns the 16-bit fixed-point value from the specified floating-point value with saturation.
   * 
   * Analogous to FixedFloat in Seb Madwick's x-IMU API.
   */
  public static short floatToBinary(final float floatValue, final Qvals q) {
      int temp = (int)((floatValue) * (int)(1 << (q.numFractionalBits())));
      if (temp > 32767) {
        temp = 32767;
      } else if (temp < -32768) {
        temp = -32768;
      }
      return (short)temp;
  }

  /** Returns the first half of the 16 bit value, i.e. for 1001 0110, it returns 1001. */
  public static byte extractFirstByte(final short value) {
    return (byte) (value >> 4);
  }

  /** Returns the second half of the 16 bit value, i.e. for 1001 0110, it returns 0110. */
  public static byte extractSecondByte(final short value) {
    return (byte) (value & 0x0F);
  }
}
