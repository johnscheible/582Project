package com.johnscheible.spewdp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SpewService extends Service {
    private static final String TAG = "SpewService";
    public static final String EXTRA_IP_ADDRESS = "com.johnscheible.spewdp.IP_ADDRESS";
    public static final String EXTRA_PORT_NUMBER = "com.johnscheible.spewdp.PORT_NUMBER";
    
    private Thread mSpewThread;
    private boolean mKeepRunning;
    private WifiManager mWifiManager;
    
    static boolean sIsRunning;
    
    public SpewService() {
        sIsRunning = false;
    }

    private final class SpewRunner implements Runnable {
        private final Intent mIntent;
        
        public SpewRunner(Intent intent) {
            mIntent = intent;
        }
        
        public void run() {
            sIsRunning = true;
            int sequenceNumber = 0;
            FileOutputStream fos = null;
            File file = null;
            
            // Set up output file:
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Toast.makeText(getApplicationContext(),
                		       "Not logging WiFi Strength",
                               Toast.LENGTH_SHORT).show();
            } else {
            	String fname = new Date().toString().replace(" ", "_");
            	fname += ".txt";
            	file = new File(getExternalFilesDir(null), fname);
            	try {
            		fos = new FileOutputStream(file);
            	} catch (FileNotFoundException e) {
            		Toast.makeText(getApplicationContext(),
             		       "How was the file not found?",
                            Toast.LENGTH_SHORT).show();
            	}
            }
            
            // set up scanning
            class WifiReceiver extends BroadcastReceiver {
            	public HashMap<String, HashMap<Integer, Integer>> strengthsMap;
            	public int count = 0;
            	
            	 public void onReceive(Context c, Intent intent) {
                     List<ScanResult> scanResultsList = mWifiManager.getScanResults();
                     for(ScanResult result : scanResultsList) {
                    	 Log.i(TAG, "SSID: " + result.SSID);
                    	 if(result.SSID.equals("MGuest") || result.SSID.equals("\"MGuest\"")) {
                    		 if(!strengthsMap.containsKey(result.BSSID)) {
                    			 strengthsMap.put(result.BSSID, new HashMap<Integer, Integer>());
                    		 }
                    		 strengthsMap.get(result.BSSID).put(count, result.level);
                    	 }
                     }
                     // SCAN AGAIN
                     count++;
                     getApplicationContext().registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                     mWifiManager.startScan();
                 }
            }
            
            WifiReceiver receiverWifi = new WifiReceiver();
            getApplicationContext().registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            mWifiManager.startScan();
            
            while (mKeepRunning) {
                // Build message (for WiFi or mobile)
            	String msg;
                if (mWifiManager.isWifiEnabled()) {
                    WifiInfo wi = mWifiManager.getConnectionInfo();
                    msg = "Sending packet " + sequenceNumber + ": " + wi.getRssi() + "\n";
                } else {
                    msg = "Sending packet " + sequenceNumber + ": MOBILE\n";
                }
                
                // If we have a file, write to it otherwise show a toast
                try {	
                	if (fos != null) {
                		fos.write(msg.getBytes());
                	} else {
                		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                	}
                } catch (IOException e) {
                	Toast.makeText(getApplicationContext(),
              		               (CharSequence) ("I guesss we can't write cause FUCK YOU"),
                                   Toast.LENGTH_SHORT).show();
                }
                
                try {
                    // Build a packet
                    DatagramSocket socket = new DatagramSocket();
                    byte[] buf = String.valueOf(sequenceNumber++).getBytes();
                    InetAddress address = InetAddress
                            .getByName(mIntent.getStringExtra(EXTRA_IP_ADDRESS));
                    int port = mIntent.getIntExtra(EXTRA_PORT_NUMBER, 4738);
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

                    Log.d(TAG, "Sending packet");
                    socket.send(packet);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
            
            // Clean up...
            if (fos != null && file != null) {
            	try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            	String[] files = {file.getAbsolutePath()};
            	MediaScannerConnection.scanFile(getApplicationContext(),
            			                        files, 
            			                        null,
            			                        null);
            	
            	FileOutputStream aps;
				try {
					aps = new FileOutputStream(new File(getExternalFilesDir(null), "aps.txt"));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	String theData = "";
        		HashMap<Integer, Integer>[] values = (HashMap<Integer, Integer>[]) receiverWifi.strengthsMap.values().toArray();
            	for(int i = 0; i < receiverWifi.count; i++) {
            		String line = "";
        			for(int j = 0; i < values.length; j++) {
        				if(values[j].containsKey(i)) {
        					line += values[j].get(i) + "\t";
        				} else {
        					line += -200 + "\t";
        				}
            		}
        			theData += line + "\n";
            	}
            	try {
					aps.write(theData.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mKeepRunning = true;
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        mSpewThread = new Thread(new SpewRunner(intent));
        mSpewThread.start();
        return START_REDELIVER_INTENT;
    }
    
    @Override
    public void onDestroy() {
        sIsRunning = false;
        mKeepRunning = false;
        return;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
