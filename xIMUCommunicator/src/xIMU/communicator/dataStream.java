package xIMU.communicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import xIMU.communicator.PrimitiveConversion.Qvals;

public class dataStream extends Thread {

	private static final int BUFFER_SIZE = 256;
	private final BluetoothSocket socket;
	private final BluetoothDevice device;
	/** The id assigned to this connection by the activity, e.g. left or right foot. */
	private final int connectionId;
	private final InputStream inputStream;
	private final OutputStream outputStream;

	private boolean connectionIsOpen = false;
	/** Time of when the connection started. */
	private long timeConnectionStarted = -1;

	private final HashMap<String, byte[]> remainingDataBySensor = new HashMap<String, byte[]>();
	static final int FRAMING_CHAR = 0x80;

	public dataStream(final BluetoothDevice device, final BluetoothSocket socket, final int deviceId) {
		this.device = device;
		this.socket = socket;
		this.connectionId = deviceId;

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because member streams are final.
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
			connectionIsOpen = true;
			timeConnectionStarted = System.currentTimeMillis();
		} catch (IOException e) {
			System.out.println("error : " + e);
		}

		inputStream = tmpIn;
		outputStream = tmpOut;
	}

	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];  // Buffer store for the stream.
		int numBytesRead; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs.
		while (connectionIsOpen) {
			try {
				// Read from the InputStream.
				numBytesRead = inputStream.read(buffer);

				// Copy the data into a separate array to be processed.
				byte[] bytes = new byte[numBytesRead];
				System.arraycopy(buffer, 0, bytes, 0, numBytesRead);

				processData(device.getAddress(), bytes);

			} catch (IOException e) {
				System.out.println("error : " + e);
				break;
			}
		}
		cancel();
	}

	/* Call this from the main Activity to shutdown the connection. */
	/** Shuts down the connection. */
	public void cancel() {
		connectionIsOpen = false;
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			System.out.println("error : " + e);
		}
	}

	/** 
	 * Processes data by extracting packets from this data concatenated to any previous data 
	 * remaining from the previous invocation for this sensor, unless {@link #reset()} has been 
	 * called. 
	 */
	public List<byte[]> processData(final String sensorId, final byte[] data) {

		final LinkedList<byte[]> receivedPackets = new LinkedList<byte[]>();

		byte[] remainingData = remainingDataBySensor.get(sensorId);
		if (remainingData == null) {
			remainingData = new byte[0];
		}

		// If there is still some data left from the previous run, concatenate that first.
		byte[] buffer = new byte[remainingData.length + data.length];
		System.arraycopy(remainingData, 0, buffer, 0, remainingData.length);
		System.arraycopy(data, 0, buffer, remainingData.length, data.length);

		// Keep count of the number of erroneous packets.
		int packetStart = 0;

		for (int i = 0; i < buffer.length; i++) {
			// If the byte is a framing char, then form the packet.
			if (isFramingChar(buffer[i])) {

				// The length is from the start to the current position, including the last byte.
				int packetLength = i + 1;
				byte[] rawPacket = new byte[packetLength];
				System.arraycopy(buffer, 0, rawPacket, 0, packetLength);

				// Set the start of the next packet to after this.
				packetStart = i + 1;

				byte[] decodedPacket = decode(rawPacket);
				
//				byte[] pack = new byte[decodedPacket.length - 2];
//				System.arraycopy(decodedPacket, 1, pack, 0, pack.length);
				
				processPacket(decodedPacket);
			}
		}

		// Copy the remaining bytes to the remainder buffer, so that they can be used in the next call.
		if (buffer.length == packetStart) {
			remainingData = new byte[0];
		} else {
			int remLength = buffer.length - packetStart;
			byte[] remaining = new byte[remLength];
			System.arraycopy(buffer, packetStart, remaining, 0, remLength);
			remainingData = remaining;
		}
		remainingDataBySensor.put(sensorId, remainingData);
		return receivedPackets; 
	}

	/** Processes incoming raw packets and returns the interpreted packet. 
	 * @return */
	private void processPacket(final byte[] rawPacket) {

		float[] gyr = setGyroscope(rawPacket);
		float[] acc = setAccelerometer(rawPacket);
		float[] mag = setMagnetometer(rawPacket);
		
		//Packet.Type type = Packet.Type.typeForOrdinal(rawPacket[0]);
		
		System.out.println("Done");
		
	}
	
	  /** Sets the gyroscope values with the values from the binary. */
	  private float[] setGyroscope(final byte[] binary) {
	    float[] gyroscope = getFloatsFromIndex(binary, 1, PrimitiveConversion.Qvals.CalibratedGyro);
	    return gyroscope;
	  }
	  
	  /** Sets the accelerometer values with the values from the binary. */
	  private float[] setAccelerometer(final byte[] binary) {
	    float[] accelerometer = getFloatsFromIndex(binary, 7, PrimitiveConversion.Qvals.CalibratedAccel);
	    return accelerometer;
	  }
	  
	  /** Sets the magnetometer values with the values from the binary. */
	  private float[] setMagnetometer(final byte[] binary) {
	    float [] magnetometer = getFloatsFromIndex(binary, 13, PrimitiveConversion.Qvals.CalibratedMag); 
	    return magnetometer;
	  }
	  
	  /** Returns 3 shorts out of 6 bytes from the index in the binary. */
	  private float[] getFloatsFromIndex(final byte[] binary, final int index, final Qvals qval) {
	    float x = twoBytesToFloat(binary, index, qval);
	    float y = twoBytesToFloat(binary, index + 2, qval);
	    float z = twoBytesToFloat(binary, index + 4, qval);
	    return new float[] { x, y, z };
	  }

	  protected float twoBytesToFloat(final byte[] binary, final int index, final Qvals qval) {
	    return PrimitiveConversion.binaryToFloat(PrimitiveConversion.concatenate(binary[index], 
	        binary[index + 1]), qval);
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

	/** 
	 * Returns true iff the byte is the framing char, i.e. the byte that marks the termination of the
	 * packet.
	 */
	private boolean isFramingChar(final byte b) {
		boolean framingCharCheck = (b & FRAMING_CHAR) == FRAMING_CHAR;
		return framingCharCheck;
	}

}
