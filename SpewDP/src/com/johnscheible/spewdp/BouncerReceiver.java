package com.johnscheible.spewdp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class BouncerReceiver extends BroadcastReceiver {
	private static final String TAG = "BouncerReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null) {
			Log.d(TAG, "Well fuck, we didn't get any extras...");
			return;
		}
		
		NetworkInfo netInfo = extras.getParcelable(WifiManager.EXTRA_NETWORK_INFO);
		switch (netInfo.getState()) {
			case CONNECTED:
				Log.i(TAG, "Connected to WiFi. Starting Monitor service");
				Toast.makeText(context,
			                   "Connected to " + netInfo.toString(),
			                   Toast.LENGTH_LONG).show();
				new Thread(new BouncerRunner(netInfo, context)).start();
				break;
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
					HttpURLConnection urlc = 
							(HttpURLConnection) (new URL("http://www.umich.edu").openConnection());
					urlc.setRequestProperty("User-Agent", "Test");
					urlc.setRequestProperty("Connection", "close");
					urlc.setConnectTimeout(1500);
					urlc.setInstanceFollowRedirects(false);
					urlc.connect();
					Log.d(TAG, "Connected with response "+ urlc.getResponseCode());
					if (urlc.getResponseCode() != 200) {
						Log.i(TAG, "Could not reach UMich on attempt " + i);
						bounce(mContext);
						return;
					}
				} catch (IOException e) {
					Log.e(TAG, "Error in checking UMich on attmept " + i);
					bounce(mContext);
					return;
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
