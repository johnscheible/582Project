package com.johnscheible.spewdp;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class WifiChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "WifiChangeReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null) {
			Log.d(TAG, "Well fuck, we didn't get any extras...");
			return;
		}
		
		NetworkInfo netInfo = extras.getParcelable(WifiManager.EXTRA_NETWORK_INFO);
		Intent service = new Intent(context, AbandonShipService.class);
		switch (netInfo.getState()) {
			case CONNECTED:
<<<<<<< HEAD
				if(!AbandonShipService.mIsRunning) {
					AbandonShipService.mIsRunning = true;
					Log.i(TAG, "Connected to WiFi. Starting Monitor service");
					Toast.makeText(context,
				                   "Connected to " + netInfo.toString(),
				                   Toast.LENGTH_LONG).show();
					context.startService(service);
				}
=======
				Log.i(TAG, "Connected to WiFi. Starting Monitor service");
				Toast.makeText(context,
			                   "Connected to " + netInfo.toString(),
			                   Toast.LENGTH_LONG).show();
				context.startService(service);
				new Thread(new BouncerRunner(netInfo, context)).start();
>>>>>>> bouncer
				break;
			case DISCONNECTING:
			case DISCONNECTED:
//				Log.i(TAG, "Disconnecting from WiFi. Stopping Monitor service");
//				context.stopService(service);
		}
		
	}
	
	public class BouncerRunner implements Runnable {
		private final Context mContext;
		
		public BouncerRunner(NetworkInfo netInfo, Context context) {
			mContext = context;
		}
		
		@Override
		public void run() {
			final int NUM_TRIES = 2;
			
			for (int i = 0; i < NUM_TRIES; i++) {
				// Ping Google to make sure we're online
				try {
					HttpsURLConnection urlc = 
							(HttpsURLConnection) (new URL("http://www.google.com").openConnection());
					urlc.setRequestProperty("User-Agent", "Test");
					urlc.setRequestProperty("Connection", "close");
					urlc.setConnectTimeout(1500);
					urlc.connect();
					if (urlc.getResponseCode() != 200) {
						Log.i(TAG, "Could not reach Google on attempt " + i);
						bounce(mContext);
						break;
					}
				} catch (IOException e) {
					Log.e(TAG, "Error in checking Google on attmept " + i);
					bounce(mContext);
					break;
				}
				
				// Sleep 3 seconds between pings
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					Log.e(TAG, "Interrupted in sleep between pings");
				}
			}
			
			Log.i(TAG, "WiFi connection is OK: You may proceed...");
		}
		
		private void bounce(Context context) {
			WifiManager wfm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Log.i(TAG, "Attempting to disconnect");
	    	Log.i(TAG, "WiFi status: " + wfm.getWifiState());
	    	// If we're not connected to WiFi, let user know we can't disconnect
	    	if (wfm.getConnectionInfo() == null) {
	    		Log.d(TAG, "Tried to bounce but wasn't connected to WiFi");
	    	} else {
	    		if (!wfm.disconnect()) {
	    			Log.d(TAG, "Bounce failed to disconnect from WiFi");
	    		}
	    	}
		}
	}

}
