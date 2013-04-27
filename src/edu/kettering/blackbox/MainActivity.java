package edu.kettering.blackbox;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothReceiver btReceiver;
	public BluetoothDevice btOBD2device;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView)findViewById(R.id.textView1);
        ListView listViewPairedDevices = (ListView)findViewById(R.id.listViewPairedDevices);
        // create an array adapter for the listview
        ArrayAdapter<String> mArrayAdapterPairedDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        // set up bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	//TODO Device does not support bluetooth        	
        	textView.setText("Bluetooth not supported");
        	return;
        }
        // make sure bluetooth is enabled, otherwise ask user to enable it
        try {
	        if (!mBluetoothAdapter.isEnabled()) {
	        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	        }
        } catch (Exception e) {
        	textView.setText(e.toString());
        }
        
        // add event listener for changes in bluetooth state (such as being turned off during operation)
        registerReceiver(btReceiver = new BluetoothReceiver(), new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        
        //TODO paired device list not populated if bluetooth is not enabled before starting the app
        // find devices (first look through paired devices to find the correct one)
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {        	
        	// Loop through paired devices
        	for (BluetoothDevice device : pairedDevices) {
        		// Add the name and address to an array adapter to show in a ListView
        		mArrayAdapterPairedDevices.add(device.getName() + "\n" + device.getAddress());
        	}
        	listViewPairedDevices.setAdapter(mArrayAdapterPairedDevices);
        }
        
/*        // discover new devices
        if (!mBluetoothAdapter.startDiscovery()) {
        	Toast.makeText(this, "Discovery failed", Toast.LENGTH_SHORT).show();
        	return;
        }
      
        // receiver for when a device is found
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				// When discovery finds a device
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					// get the BluetoothDevice object from the Intent
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					// add the name and address to an array adapter to show in a listview
					mArrayAdapterNewDevices.add(device.getName() + "\n" + device.getAddress());
				}				
			}        	
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
*/        
        // If the correct device is not already paired, show the bluetooth settings window to let the user pair the device
//        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
        
        
        // connect to the device
        for (BluetoothDevice device : pairedDevices) {
        	if (device.getName().contains("OBDII")) {
        		// this is most likely the correct device.
        		//TODO provide a way for the user to change/choose the bluetooth device to connect to
        		btOBD2device = device;
        		break;
        	}
        }
        
        // if the device is found
        if (btOBD2device != null) {        
        	
        	
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch (requestCode) {
    	case REQUEST_ENABLE_BT:
    		if (resultCode == RESULT_OK) {
    			//TODO catch bt error, or user did not enable bt
    			Toast.makeText(getApplicationContext(), "Bluetooth enabled", Toast.LENGTH_SHORT).show();
    		} else if (resultCode == RESULT_CANCELED){
    			Toast.makeText(getApplicationContext(), "Error: Bluetooth must be enabled for this app to work", Toast.LENGTH_LONG).show();
    		}
    		break;
    	default:
    		
    	}
    }
    
    @Override
    protected void onDestroy() {
    	// unregister BroadcastReceiver
    	unregisterReceiver(btReceiver);
    	super.onDestroy();
    }
}
