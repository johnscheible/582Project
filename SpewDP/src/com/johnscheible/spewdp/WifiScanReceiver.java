package com.johnscheible.spewdp;
import java.util.HashMap;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


public class WifiScanReceiver extends BroadcastReceiver {
	private static final String TAG = "WifiScanReceiver";
	public static HashMap<String, HashMap<Integer, Integer>> sStrengthsMap;
	public static int sCount = 0;

	@Override
	public void onReceive(Context c, Intent intent) {
		WifiManager wfm = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		
		List<ScanResult> scanResultsList = wfm.getScanResults();
		for(ScanResult result : scanResultsList) {
			Log.i(TAG, "SSID: " + result.SSID);
			if(result.SSID.equals("MGuest") || result.SSID.equals("\"MGuest\"")) {
				if(!sStrengthsMap.containsKey(result.BSSID)) {
					sStrengthsMap.put(result.BSSID, new HashMap<Integer, Integer>());
				}
				sStrengthsMap.get(result.BSSID).put(sCount, result.level);
			}
		}
		// SCAN AGAIN
		sCount++;
		wfm.startScan();
	}
}
