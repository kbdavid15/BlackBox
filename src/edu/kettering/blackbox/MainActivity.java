package edu.kettering.blackbox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import edu.kettering.blackbox.bluetooth.BluetoothReceiver;
import edu.kettering.blackbox.bluetooth.BluetoothService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothReceiver btReceiver;
	public BluetoothDevice btOBD2device;
	public static BluetoothAdapter mBluetoothAdapter;
	
	private Set<BluetoothDevice> pairedDevices;
	private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final String NAME = "BluetoothOBD2Service";
	
	// The service that connects to the device
	private BluetoothService mBluetoothService;
	
	private ArrayAdapter<String> mArrayAdapterPairedDevices;
	
	/**
	 * 
	 */
	private static Handler mHandler = new Handler() {
    	
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        TextView textView = (TextView)findViewById(R.id.textView1);
        ListView listViewPairedDevices = (ListView)findViewById(R.id.listViewPairedDevices);
        // create an array adapter for the listview
        mArrayAdapterPairedDevices = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        
        // add onClick listener for listview
        listViewPairedDevices.setOnItemClickListener(selectPairedDeviceListener);        
        
        // set up bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	//TODO Device does not support bluetooth        	
        	textView.setText("Bluetooth not supported");
        	return;
        }
        // make sure bluetooth is enabled, otherwise ask user to enable it
        if (!mBluetoothAdapter.isEnabled()) {
        	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        // add event listener for changes in bluetooth state (such as being turned off during operation)
        registerReceiver(btReceiver = new BluetoothReceiver(), new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        
        //TODO paired device list not populated if bluetooth is not enabled before starting the app
        // find devices (first look through paired devices to find the correct one)
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {        	
        	// Loop through paired devices
        	for (BluetoothDevice device : pairedDevices) {
        		// Add the name and address to an array adapter to show in a ListView
        		mArrayAdapterPairedDevices.add(device.getName() + "\n" + device.getAddress());
        	}
        	listViewPairedDevices.setAdapter(mArrayAdapterPairedDevices);
        }
               
        // If the correct device is not already paired, show the bluetooth settings window to let the user pair the device
//        startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
        
        
////         connect to the device
//        for (BluetoothDevice device : pairedDevices) {
//        	if (device.getName().contains("OBDII")) {
//        		// this is most likely the correct device.
//        		//TODO provide a way for the user to change/choose the bluetooth device to connect to
//        		btOBD2device = device;
//        		break;
//        	}
//        }        
//        // if the device is found
//        if (btOBD2device != null) {
//        	// configure the handler
//        	mHandler = new Handler() {
//        		@Override
//        		public void handleMessage(Message msg) {
//        			
//        		}
//        	};//        	
////        	ConnectThread thread = new ConnectThread(btOBD2device);
////        	thread.run();//        	
//        	AcceptThread thread = new AcceptThread();
//        	thread.run();
//        }
    }
    
    private OnItemClickListener selectPairedDeviceListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    		// connect to the device that the user chose
    		btOBD2device = (BluetoothDevice) pairedDevices.toArray()[position];
    		tryConnectDevice();
    		
			Toast.makeText(getBaseContext(), btOBD2device.getName(), Toast.LENGTH_LONG).show();
		}
    	
    };
    
    /**
     * Attempts to connect to btOBD2device 
     * @return	False if the connection failed, true otherwise
     */
    private boolean tryConnectDevice() {
    	// instantiate the Bluetooth service
    	mBluetoothService = new BluetoothService(this, mHandler);
    	return false;
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
