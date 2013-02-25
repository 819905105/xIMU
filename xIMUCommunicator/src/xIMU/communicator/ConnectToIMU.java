package xIMU.communicator;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ConnectToIMU extends AsyncTask<Void, Boolean, BluetoothSocket> {

	private TextView stateBluetooth;
	private Button btDisconnect;
	private Button btScanDevice;
	BluetoothDevice device;
	ArrayAdapter<BluetoothDevice> btDevices;

	private BluetoothSocket btSocket;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	public ConnectToIMU(TextView stateBluetooth, Button btDisconnect, BluetoothDevice device,
			Button btScanDevice, ArrayAdapter<BluetoothDevice> btDevices) {
		this.stateBluetooth = stateBluetooth;
		this.btDisconnect = btDisconnect;
		this.device = device;
		this.btScanDevice = btScanDevice;
		this.btDevices = btDevices;
	}

	protected BluetoothSocket doInBackground(Void... params) {

		BluetoothSocket tmp = null;
		btSocket = null;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) { 
			Log.e("ConnectToIMU", "Error : " + e);
		}

		btSocket = tmp;

		try {
			btSocket.connect();	
			
			dataStream runnable = new dataStream(device, btSocket, 1);
			runnable.start();
			
			publishProgress(true);
		} catch (IOException connectException) {
			try {
				btSocket.close();
				publishProgress(false);
			} catch (IOException closeException) { 
				Log.e("ConnectToIMU", "Error : " + closeException);
			}
		}

		return btSocket;
	}

	public void onProgressUpdate(Boolean... params) {
		if(params[0] == true){
			stateBluetooth.setText("Connected");
			btDisconnect.setEnabled(true);
			btScanDevice.setEnabled(false);
			btDevices.clear();
		} else {
			stateBluetooth.setText("Connection Failed");
		}
	}

}
