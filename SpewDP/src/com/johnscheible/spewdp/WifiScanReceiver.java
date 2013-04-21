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
	public static int sCount        = 0;
	private static final String TAG = "WifiScanReceiver";
	public static HashMap<String, HashMap<Integer, Integer>> sStrengthsMap = new HashMap<String, HashMap<Integer, Integer>>();

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

// code that used to be in SpewService
   //      	Log.i(TAG, "About to write aps");
   //      	FileOutputStream aps = null;
			// try {
			// 	aps = new FileOutputStream(new File(getExternalFilesDir(null), "aps.txt"));
			// } catch (FileNotFoundException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
   //      	String theData = "";
   //  		ArrayList<HashMap<Integer, Integer>> values = 
   //  				new ArrayList(WifiScanReceiver.sStrengthsMap.values());
   //      	for(int i = 0; i < WifiScanReceiver.sCount; i++) {
   //      		String line = "";
   //  			for(int j = 0; j < values.size(); j++) {
   //  				if(values.get(j).containsKey(i)) {
   //  					line += values.get(j).get(i) + "\t";
   //  				} else {
   //  					line += -100 + "\t";
   //  				}
   //      		}
   //  			theData += line + "\n";
   //      	}
   //      	try {
			// 	aps.write(theData.getBytes());
			// 	aps.close();
   //          	Log.i(TAG, "Wrote aps");
			// } catch (IOException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
