package com.johnscheible.spewdp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AbandonShipReceiver extends BroadcastReceiver {
	private static final String TAG = "AbandonShipReceiver";
	
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
				Log.i(TAG, "Connected to WiFi. Starting Abandon Ship! service");
				Toast.makeText(context,
			                   "Connected to " + netInfo.toString(),
			                   Toast.LENGTH_LONG).show();
				context.startService(service);
				break;
			case DISCONNECTING:
			case DISCONNECTED:
				Log.i(TAG, "Disconnecting from WiFi. Stopping Abandon Ship! service");
				context.stopService(service);
		}
		
	}
}
