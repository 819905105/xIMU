package xIMU.communicator;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class starts the Android process, and sets up the GUI. It's the main UI class and must be used 
 * when updating any UI interface.
 * 
 * @author Richard Woodward (rw1709@imperial.ac.uk)
 */

public class Main extends Activity {
	
	Button btScanDevice;
	Button btDisconnect;
	TextView stateBluetooth;
	BluetoothAdapter bluetoothAdapter;
	ListView listDevicesFound;
	ArrayAdapter<BluetoothDevice> btDevices;

	ConnectToIMU connectToDevice;
	BluetoothSocket getSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		Log.w("Main", "+++ onCreate() +++");
		
		btScanDevice = (Button) findViewById(R.id.btScan);
		btScanDevice.setOnClickListener(btnScanDeviceOnClickListener);
		btScanDevice.setEnabled(false);

		btDisconnect = (Button) findViewById(R.id.btConnection);
		btDisconnect.setEnabled(false);
		btDisconnect.setOnClickListener(btDisconnectOnClickListener);

		stateBluetooth = (TextView) findViewById(R.id.btStatus);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		btDevices = new ArrayAdapter<BluetoothDevice>(Main.this, android.R.layout.simple_list_item_1);

		listDevicesFound = (ListView) findViewById(R.id.btList);
		listDevicesFound.setAdapter(btDevices);
		listDevicesFound.setOnItemClickListener(btListListener);

	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.w("Main", "+++ onStart() +++");

		CheckBlueToothState();
		registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(ActionFoundReceiver);
		Log.w("Main", "+++ onDestroy() +++");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_layout, menu);
		Log.w("Main", "+++ OnCreateOptionsMenu(Menu menu) +++");
		return true;
	}

	private void CheckBlueToothState() {

		String message = null;
		
		if (bluetoothAdapter == null) {
			message = "Bluetooth NOT supported";
		} else {
			if (bluetoothAdapter.isEnabled()) {
				if (bluetoothAdapter.isDiscovering()) {
					message = "Bluetooth is currently in device discovery process.";
				} else {
					message = "Bluetooth is Enabled.";
					btScanDevice.setEnabled(true);
				}
			} else {
				message = "Bluetooth is NOT Enabled!";
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, 0);
			}
		}
		
		stateBluetooth.setText(message);
		
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			stateBluetooth.setText("Bluetooth is Enabled.");
			btScanDevice.setEnabled(true);
		} else {
			Toast.makeText(this, "Exiting", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private ListView.OnItemClickListener btListListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id){

			bluetoothAdapter.cancelDiscovery();

			String deviceName = parent.getItemAtPosition(position).toString();
			String deviceAddress = btDevices.getItem(position).getAddress().toString();
			
			stateBluetooth.setText(deviceName);
			Toast.makeText(getApplicationContext(), deviceAddress, Toast.LENGTH_SHORT).show();

			connectToDevice = new ConnectToIMU(stateBluetooth, btDisconnect, btDevices.getItem(position), btScanDevice, btDevices);
			connectToDevice.execute();

		}
	};

	private Button.OnClickListener btnScanDeviceOnClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			stateBluetooth.setText("Searching...");
			btDevices.clear();
			bluetoothAdapter.startDiscovery();
		}
	};

	private Button.OnClickListener btDisconnectOnClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {

			try {
				getSocket = connectToDevice.get();
				getSocket.close();
				stateBluetooth.setText("Disconnected");
			} catch (InterruptedException e) {
				Log.e("Main", "Error : " + e);
			} catch (ExecutionException e) {
				Log.e("Main", "Error : " + e);
			} catch (IOException e) {
				Log.e("Main", "Error : " + e);
			}

			btScanDevice.setEnabled(true);
			btDisconnect.setEnabled(false);
		}
	};

	private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				btDevices.add(device);

				btDevices.notifyDataSetChanged();
			} 
			
			if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				
                stateBluetooth.setText("Scan Complete");

                if (btDevices.getCount() == 0) {
                	stateBluetooth.setText("None Found");
                    
                }
            }

		}

	};

}
