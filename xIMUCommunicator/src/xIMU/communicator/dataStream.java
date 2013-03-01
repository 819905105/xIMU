package xIMU.communicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class dataStream extends Thread {

	decodePacket decode;
	
	private static final int BUFFER_SIZE = 23;
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

		decode = new decodePacket();
		
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
				//System.out.println(Arrays.toString(buffer));

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
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			System.out.println("errorss : " + e);
		}
	}

	/** 
	 * Processes data by extracting packets from this data concatenated to any previous data 
	 * remaining from the previous invocation for this sensor, unless {@link #reset()} has been 
	 * called. 
	 */
	public void processData(final String sensorId, final byte[] data) {

		byte[] remainingData = remainingDataBySensor.get(sensorId);
		if (remainingData == null) {
			remainingData = new byte[0];
		}

		// If there is still some data left from the previous run, concatenate that first.
		byte[] buffer = new byte[remainingData.length + data.length];
		System.arraycopy(remainingData, 0, buffer, 0, remainingData.length);
		System.arraycopy(data, 0, buffer, remainingData.length, data.length);

		int packetStart = 0;

		for (int i = 0; i < buffer.length; i++) {
			// If the byte is a framing char, then form the packet.
			if (isFramingChar(buffer[i])) {

				// The length is from the start to the current position, including the last byte.
				int packetLength = i - packetStart + 1;
				byte[] rawPacket = new byte[packetLength];
				System.arraycopy(buffer, packetStart, rawPacket, 0, packetLength);

				// Set the start of the next packet to after this.
				packetStart = i + 1;

				byte[] decodedPacket = decode.decode(rawPacket);
				decode.convertPacket(decodedPacket);

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
