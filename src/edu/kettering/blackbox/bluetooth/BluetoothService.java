//package edu.kettering.blackbox.bluetooth;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.UUID;
//
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import edu.kettering.blackbox.MainActivity;
//
//public class BluetoothService {
//	
//		
//	private class ConnectThread extends Thread {
//		private final BluetoothSocket mmSocket;
//	    private final BluetoothDevice mmDevice;
//	    private final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//	 
//	    public ConnectThread(BluetoothDevice device) {
//	        // Use a temporary object that is later assigned to mmSocket,
//	        // because mmSocket is final
//	        BluetoothSocket tmp = null;
//	        mmDevice = device;
//	        
//	        // Get a BluetoothSocket to connect with the given BluetoothDevice
//	        try {
//	            // MY_UUID is the app's UUID string, also used by the server code
//	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
//	        } catch (IOException e) { }
//	        mmSocket = tmp;
//	    }
//	 
//	    public void run() {
//	        // Cancel discovery because it will slow down the connection
//	        MainActivity.mBluetoothAdapter.cancelDiscovery();
//	        
//	        try {
//	            // Connect the device through the socket. This will block
//	            // until it succeeds or throws an exception
//	            mmSocket.connect();
//	        } catch (IOException connectException) {
//	            // Unable to connect; close the socket and get out
//	            try {
//	                mmSocket.close();
//	            } catch (IOException closeException) { }
//	            return;
//	        }
//	 
//	        // Do work to manage the connection (in a separate thread)
//	        //manageConnectedSocket(mmSocket);
//	    }
//	 
//	    /** Will cancel an in-progress connection, and close the socket */
//	    public void cancel() {
//	        try {
//	            mmSocket.close();
//	        } catch (IOException e) { }
//	    }
//	}
//
//	private class AcceptThread extends Thread {
//        private final BluetoothServerSocket mmServerSocket;
//     
//        public AcceptThread() {
//            // Use a temporary object that is later assigned to mmServerSocket,
//            // because mmServerSocket is final
//            BluetoothServerSocket tmp = null;
//            try {
//                // MY_UUID is the app's UUID string, also used by the client code
//                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//            } catch (IOException e) { }
//            mmServerSocket = tmp;
//        }
//     
//        public void run() {
//            BluetoothSocket socket = null;
//            // Keep listening until exception occurs or a socket is returned
//            while (true) {
//                try {
//                    socket = mmServerSocket.accept();
//                } catch (IOException e) {
//                    break;
//                }
//                // If a connection was accepted
//                if (socket != null) {
//                    // Do work to manage the connection (in a separate thread)
//                    //manageConnectedSocket(socket);
//                    try {
//						mmServerSocket.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//                    break;
//                }
//            }
//        }
//     
//        /** Will cancel the listening socket, and cause the thread to finish */
//        public void cancel() {
//            try {
//                mmServerSocket.close();
//            } catch (IOException e) { }
//        }
//    }
//
//	public class OBD2Device extends Thread {
//    	public final int MESSAGE_READ = 0xAAAA;
//    	
//    	private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//        
//        /**
//         * Class constructor for the device
//         * @param socket	The Bluetooth socket created in ConnectThread
//         */
//        public OBD2Device(BluetoothSocket socket) {
//            mmSocket = socket;
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//     
//            // Get the input and output streams, using temp objects because
//            // member streams are final
//            try {
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) { }
//     
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//     
//        public void run() {
//            byte[] buffer = new byte[1024];  // buffer store for the stream
//            int bytes; // bytes returned from read()
//     
//            // Keep listening to the InputStream until an exception occurs
//            while (true) {
//                try {
//                    // Read from the InputStream
//                    bytes = mmInStream.read(buffer);
//                    // Send the obtained bytes to the UI activity
//                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
//                            .sendToTarget();
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }
//     
//        /* Call this from the main activity to send data to the remote device */
//        public void write(byte[] bytes) {
//            try {
//                mmOutStream.write(bytes);
//            } catch (IOException e) { }
//        }
//     
//        /* Call this from the main activity to shutdown the connection */
//        public void cancel() {
//            try {
//                mmSocket.close();
//            } catch (IOException e) { }
//        }
//
//    }
//	
//}
