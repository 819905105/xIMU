package imu.communicator.data.processor;

import java.io.IOException;
import java.io.InputStream;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class PacketStream extends Thread {

	private PacketProcessor process;
	
	private static final int BUFFER_SIZE = 256;
	private final BluetoothSocket socket;
	private final BluetoothDevice device;
	private final InputStream inputStream;

	private boolean connectionIsOpen = false;

	public PacketStream(final BluetoothDevice device, final BluetoothSocket socket, final int deviceId) {
		this.device = device;
		this.socket = socket;

		process = new PacketProcessor();
		
		InputStream tmpIn = null;

		// Get the input and output streams, using temp objects because member streams are final.
		try {
			tmpIn = socket.getInputStream();
			connectionIsOpen = true;
		} catch (IOException e) {
			System.out.println("error : " + e);
		}

		inputStream = tmpIn;
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

				process.processData(device.getAddress(), bytes);

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
		System.out.println("CLOSE");
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

}
