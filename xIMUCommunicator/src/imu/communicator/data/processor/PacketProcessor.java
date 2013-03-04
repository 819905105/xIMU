package imu.communicator.data.processor;

import java.util.HashMap;

public class PacketProcessor {

	private final PacketDecoder packetDecode;
	private final PacketConverter packetConvert;

	private final HashMap<String, byte[]> remainingDataBySensor = new HashMap<String, byte[]>();
	private final int FRAMING_CHAR = 0x80;

	public PacketProcessor() {
		this.packetDecode = new PacketDecoder();
		this.packetConvert = new PacketConverter();
	}

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

				byte[] decodedPacket = packetDecode.decode(rawPacket);
				packetConvert.convertPacket(decodedPacket);

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

	private boolean isFramingChar(final byte b) {
		boolean framingCharCheck = (b & FRAMING_CHAR) == FRAMING_CHAR;
		return framingCharCheck;
	}

}
