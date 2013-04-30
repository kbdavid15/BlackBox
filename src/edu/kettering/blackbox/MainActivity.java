package edu.kettering.blackbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothReceiver btReceiver;
	public BluetoothDevice btOBD2device;
	public static BluetoothAdapter mBluetoothAdapter;
	private static Handler mHandler;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView)findViewById(R.id.textView1);
        ListView listViewPairedDevices = (ListView)findViewById(R.id.listViewPairedDevices);
        // create an array adapter for the listview
        ArrayAdapter<String> mArrayAdapterPairedDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        // set up bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        	// configure the handler
        	mHandler = new Handler() {
        		@Override
        		public void handleMessage(Message msg) {
        			
        		}
        	};
        	
        	ConnectThread thread = new ConnectThread(btOBD2device);
        	thread.run();
        	
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
    
    
    public class OBD2Device extends Thread {
    	public final int MESSAGE_READ = 0xAAAA;
    	
    	private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        
        /**
         * Class constructor for the device
         * @param socket	The Bluetooth socket created in ConnectThread
         */
        public OBD2Device(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
     
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
     
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
     
        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
     
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
     
        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
     
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }
    
}
