package imu.communicator.data.processor;

public class PackageSplitter {

	public PackageSplitter(Enums.Type type, byte[] decodedPacket) {

		PacketConverter convert = new PacketConverter();

		switch (type){
		case ErrorMessage:
			if (decodedPacket.length != 4)
			{
				System.err.println("Invalid number of bytes for packet header.");
				break;
			}

			String message = new ErrorType().obtainError(PacketConverter.concatenate(decodedPacket[1], decodedPacket[2]));
			System.err.println(message);

			break;

		case CalInertialMagData:
			if (decodedPacket.length != 20)
			{
				System.err.println("Invalid number of bytes for packet header.");
				break;
			}

			float[] gyroscope = convert.getFloatsFromIndex(decodedPacket, 1, Enums.Qvals.CalibratedGyro);
			float[] accelerometer = convert.getFloatsFromIndex(decodedPacket, 7, Enums.Qvals.CalibratedAccel);
			float[] magnetometer = convert.getFloatsFromIndex(decodedPacket, 13, Enums.Qvals.CalibratedMag);

			//System.out.println("x : " + accelerometer[0] + " y : " + accelerometer[1] + " z : " + accelerometer[2]);

			break;

		case QuaternionData:
			if (decodedPacket.length != 10)
			{
				System.err.println("Invalid number of bytes for packet header.");
				break;
			}

			float[] quaternion = convert.getFloats(decodedPacket, Enums.Qvals.Quaternion);

			System.out.println("quaternion : " + quaternion[0]);

			break;

		case CalAnalogueData:
			if (decodedPacket.length != 18)
			{
				System.err.println("Invalid number of bytes for packet header.");
				break;
			}

			// need to change value of 1 to whatever my channel of analogue is using.
			float[] mmg = convert.getFloats(decodedPacket, Enums.Qvals.CalibratedAnalogueInput);

			System.out.println("mmg : " + mmg[0]);

			break;

		default:
			System.err.println("default called");
			break;
		}
	}

}
