package edu.kettering.blackbox;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
	public BluetoothReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)) {
//		case BluetoothAdapter.STATE_CONNECTED:
//			break;
//		case BluetoothAdapter.STATE_CONNECTING:
//			break;
//		case BluetoothAdapter.STATE_DISCONNECTED:
//			break;
//		case BluetoothAdapter.STATE_DISCONNECTING:
//			break;
		case BluetoothAdapter.STATE_OFF:
			//TODO show dialog warning user that bluetooth must be on to use this app
			Toast.makeText(context, "Bluetooth must be enabled to use this app", Toast.LENGTH_SHORT).show();
			break;
		case BluetoothAdapter.STATE_ON:
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			break;
		default:
				
		}
	}
}
