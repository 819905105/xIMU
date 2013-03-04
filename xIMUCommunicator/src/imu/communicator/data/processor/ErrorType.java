package imu.communicator.data.processor;

public class ErrorType {

	public String obtainError(short errorCode)
	{

		Enums.ErrorCodes err = Enums.ErrorCodes.typeForOrdinal(errorCode);

		if (err == null)
		{
			System.err.println("Invalid error code.");
		}

		String message = "Unknown error.";

		switch (err) {
		case NoError: message = "No error."; break;
		case LowBattery: message = "Low battery."; break;
		case USBreceiveBufferOverrun: message = "USB receive buffer overrun."; break;
		case USBtransmitBufferOverrun: message = "USB transmit buffer overrun."; break;
		case BluetoothReceiveBufferOverrun: message = "Bluetooth receive buffer overrun."; break;
		case BluetoothTransmitBufferOverrun: message = "Bluetooth transmit buffer overrun."; break;
		case SDcardWriteBufferOverrun: message = "SD card write buffer overrun."; break;
		//        case TransmitBufferOvun: message = "Transmit buffer overrun."; break;
		//        case ReceiveBufferOvun: message = "USB receive buffer overrun."; break;
		//        case SDcardBufferOverrun: message = "SD card buffer overrun."; break;
		case TooFewBytesInPacket: message = "Too few bytes in packet."; break;
		case TooManyBytesInPacket: message = "Too many bytes in packet."; break;
		case InvalidChecksum: message = "Invalid checksum."; break;
		case UnknownHeader: message = "Unknown packet header."; break;
		case InvalidNumBytesForPacketHeader: message = "Invalid number of bytes for packet header."; break;
		case InvalidRegisterAddress: message = "Invalid register address."; break;
		case RegisterReadOnly: message = "Cannot write to read-only register."; break;
		case InvalidRegisterValue: message = "Invalid register value."; break;
		case InvalidCommand: message = "Invalid command."; break;
		case GyroscopeNotStationary: message = "Gyroscope not stationary.  Calibration aborted."; break;
		case MagnetometerSaturation: message = "Magnetometer saturation occurred.  Calibration aborted."; break;
		case IncorrectAuxillaryPortMode: message = "Auxiliary port in incorrect mode."; break;
		default: break;
		}

		return message;
	}

}
