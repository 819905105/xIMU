package xIMU.communicator;

public class whatsTheType {
	
//	public whatIsIt(dataTypes.Type type, byte[] decodedPacket) {
//        // Interpret packet according to header
//        switch (type){
//            case ErrorMessage:
//                if (decodedPacket.length != 4)
//                {
//                    System.err.println("Invalid number of bytes for packet header.");
//                }
//                return new ErrorData((ushort)Concat(decodedPacket[1], decodedPacket[2]));
//            case Command:
//                if (decodedPacket.length != 4)
//                {
//                    System.err.println("Invalid number of bytes for packet header.");
//                }
//                //return new CommandData((ushort)Concat(decodedPacket[1], decodedPacket[2]));
//            case WriteRegister:
//                if (decodedPacket.length != 6)
//                {
//                    System.err.println("Invalid number of bytes for packet header.");
//                }
//                //return new RegisterData((ushort)Concat(decodedPacket[1], decodedPacket[2]), (ushort)Concat(decodedPacket[3], decodedPacket[4]));
//            case WriteDateTime:
//                if (decodedPacket.length != 8)
//                {
//                    System.err.println("Invalid number of bytes for packet header.");
//                }
//                return new DateTimeData((int)(10 * ((decodedPacket[1] & 0xF0) >> 4) + (decodedPacket[1] & 0x0F)) + 2000,
//                                        (int)(10 * ((decodedPacket[2] & 0xF0) >> 4) + (decodedPacket[2] & 0x0F)),
//                                        (int)(10 * ((decodedPacket[3] & 0xF0) >> 4) + (decodedPacket[3] & 0x0F)),
//                                        (int)(10 * ((decodedPacket[4] & 0xF0) >> 4) + (decodedPacket[4] & 0x0F)),
//                                        (int)(10 * ((decodedPacket[5] & 0xF0) >> 4) + (decodedPacket[5] & 0x0F)),
//                                        (int)(10 * ((decodedPacket[6] & 0xF0) >> 4) + (decodedPacket[6] & 0x0F)));
//            case RawBattThermData:
//                if (decodedPacket.Length != 6)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new RawBatteryAndThermometerData(Concat(decodedPacket[1], decodedPacket[2]), Concat(decodedPacket[3], decodedPacket[4]));
//            case CalBattThermData:
//                if (decodedPacket.Length != 6)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new CalBatteryAndThermometerData(FixedFloat.FixedToFloat(Concat(decodedPacket[1], decodedPacket[2]), Qvals.CalibratedBattery),
//                                            FixedFloat.FixedToFloat(Concat(decodedPacket[3], decodedPacket[4]), Qvals.CalibratedThermometer));
//            case RawInertialMagData:
//                if (decodedPacket.Length != 20)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new RawInertialAndMagneticData(new short[] { Concat(decodedPacket[1], decodedPacket[2]), Concat(decodedPacket[3], decodedPacket[4]), Concat(decodedPacket[5], decodedPacket[6]) },
//                                              new short[] { Concat(decodedPacket[7], decodedPacket[8]), Concat(decodedPacket[9], decodedPacket[10]), Concat(decodedPacket[11], decodedPacket[12]) },
//                                              new short[] { Concat(decodedPacket[13], decodedPacket[14]), Concat(decodedPacket[15], decodedPacket[16]), Concat(decodedPacket[17], decodedPacket[18]) });
//            case CalInertialMagData:
//                if (decodedPacket.Length != 20)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new CalInertialAndMagneticData(new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[1], decodedPacket[2]), Qvals.CalibratedGyroscope), FixedFloat.FixedToFloat(Concat(decodedPacket[3], decodedPacket[4]), Qvals.CalibratedGyroscope), FixedFloat.FixedToFloat(Concat(decodedPacket[5], decodedPacket[6]), Qvals.CalibratedGyroscope) },
//                                                   new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[7], decodedPacket[8]), Qvals.CalibratedAccelerometer), FixedFloat.FixedToFloat(Concat(decodedPacket[9], decodedPacket[10]), Qvals.CalibratedAccelerometer), FixedFloat.FixedToFloat(Concat(decodedPacket[11], decodedPacket[12]), Qvals.CalibratedAccelerometer) },
//                                                   new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[13], decodedPacket[14]), Qvals.CalibratedMagnetometer), FixedFloat.FixedToFloat(Concat(decodedPacket[15], decodedPacket[16]), Qvals.CalibratedMagnetometer), FixedFloat.FixedToFloat(Concat(decodedPacket[17], decodedPacket[18]), Qvals.CalibratedMagnetometer) });
//            case QuaternionData:
//                if (decodedPacket.Length != 10)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new QuaternionData(new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[1], decodedPacket[2]), Qvals.Quaternion), FixedFloat.FixedToFloat(Concat(decodedPacket[3], decodedPacket[4]), Qvals.Quaternion),
//                                                        FixedFloat.FixedToFloat(Concat(decodedPacket[5], decodedPacket[6]), Qvals.Quaternion), FixedFloat.FixedToFloat(Concat(decodedPacket[7], decodedPacket[8]), Qvals.Quaternion)});
//            case ((byte)PacketHeaders.DigitalIOdata):
//                if (decodedPacket.Length != 4)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new DigitalIOdata(decodedPacket[1], decodedPacket[2]);
//            case RawAnalogueData:
//                if (decodedPacket.Length != 18)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new RawAnalogueInputData(Concat(decodedPacket[1], decodedPacket[2]), Concat(decodedPacket[3], decodedPacket[4]), Concat(decodedPacket[5], decodedPacket[6]), Concat(decodedPacket[7], decodedPacket[8]),
//                                                Concat(decodedPacket[9], decodedPacket[10]), Concat(decodedPacket[11], decodedPacket[12]), Concat(decodedPacket[13], decodedPacket[14]), Concat(decodedPacket[15], decodedPacket[16]));
//            case CalAnalogueData:
//                if (decodedPacket.Length != 18)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new CalAnalogueInputData(FixedFloat.FixedToFloat(Concat(decodedPacket[1], decodedPacket[2]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[3], decodedPacket[4]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[5], decodedPacket[6]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[7], decodedPacket[8]), Qvals.CalibratedAnalogueInput),
//                                                FixedFloat.FixedToFloat(Concat(decodedPacket[9], decodedPacket[10]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[11], decodedPacket[12]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[13], decodedPacket[14]), Qvals.CalibratedAnalogueInput), FixedFloat.FixedToFloat(Concat(decodedPacket[15], decodedPacket[16]), Qvals.CalibratedAnalogueInput));
//            case PWMData:
//                if (decodedPacket.Length != 10)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new PWMoutputData((ushort)Concat(decodedPacket[1], decodedPacket[2]), (ushort)Concat(decodedPacket[3], decodedPacket[4]), (ushort)Concat(decodedPacket[5], decodedPacket[6]), (ushort)Concat(decodedPacket[7], decodedPacket[8]));
//            case ADXL345Data:
//                if (decodedPacket.Length != 26)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new RawADXL345busData(new short[] { Concat(decodedPacket[1], decodedPacket[2]), Concat(decodedPacket[3], decodedPacket[4]), Concat(decodedPacket[5], decodedPacket[6]) },
//                                             new short[] { Concat(decodedPacket[7], decodedPacket[8]), Concat(decodedPacket[9], decodedPacket[10]), Concat(decodedPacket[11], decodedPacket[12]) },
//                                             new short[] { Concat(decodedPacket[13], decodedPacket[14]), Concat(decodedPacket[15], decodedPacket[16]), Concat(decodedPacket[17], decodedPacket[18]) },
//                                             new short[] { Concat(decodedPacket[19], decodedPacket[20]), Concat(decodedPacket[21], decodedPacket[22]), Concat(decodedPacket[23], decodedPacket[24]) });
//            case ((byte)PacketHeaders.CalADXL345busData):
//                if (decodedPacket.Length != 26)
//                {
//                    throw new Exception("Invalid number of bytes for packet header.");
//                }
//                return new CalADXL345busData(new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[1], decodedPacket[2]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[3], decodedPacket[4]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[5], decodedPacket[6]), Qvals.CalibratedADXL345) },
//                                             new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[7], decodedPacket[8]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[9], decodedPacket[10]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[11], decodedPacket[12]), Qvals.CalibratedADXL345) },
//                                             new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[13], decodedPacket[14]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[15], decodedPacket[16]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[17], decodedPacket[18]), Qvals.CalibratedADXL345) },
//                                             new float[] { FixedFloat.FixedToFloat(Concat(decodedPacket[19], decodedPacket[20]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[21], decodedPacket[22]), Qvals.CalibratedADXL345), FixedFloat.FixedToFloat(Concat(decodedPacket[23], decodedPacket[24]), Qvals.CalibratedADXL345) });
//            default:
//                throw new Exception("Unknown packet header.");
//        }
//	}

}
