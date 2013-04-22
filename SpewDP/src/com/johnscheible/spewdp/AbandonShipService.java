package com.johnscheible.spewdp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

public class AbandonShipService extends Service {
	static boolean mIsRunning;
	static boolean mKeepRunning;
    
	private WifiManager mWifiManager;
    
	public AbandonShipService() {
		Log.i("ABANDON", "WE'RE IN the constructor");
		mIsRunning = false;
		mKeepRunning = true;
	}
	
	private final class ShipCaptain implements Runnable {
		private final Intent mIntent;
		private final int STRENGTH_THRESHOLD = -70;
		private final int DURATION_THRESHOLD = 69;
		private int sampleCount;
		
		public ShipCaptain(Intent intent) {
			mIntent = intent;
			sampleCount = 0;
		}
		
		// If we get DURATION_THRESHOLD consecutive samples below STRENGTH_THRESHOLD, abandon ship!
		public void run() {
            FileOutputStream fos = null;
//            Date date = new Date();
//            File file = new File(getExternalFilesDir(null), date.toString() + ".txt");
            File file = new File(getExternalFilesDir(null), "str.txt");
    		try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			while(mKeepRunning) {
				Log.i("ABANDON", "IM RUNNING BOB");
				if (mWifiManager.isWifiEnabled()) {
//				if(true) {
                    WifiInfo wi = mWifiManager.getConnectionInfo();
                    int sigStrength = wi.getRssi();
                    
                    try {
						fos.write((sigStrength + "\n").getBytes());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                    if(sigStrength <= STRENGTH_THRESHOLD)
                    	sampleCount++;
                    else
                    	sampleCount = 0;
                    
                    if(sampleCount >= DURATION_THRESHOLD) {
                    	Log.i("ABANDON", "We are disconnecting!");
                    	try {
							fos.write("# WE ARE DISCONNECTING, HOMIE".getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    	mWifiManager.disconnect();
                    	mKeepRunning = false;
                    }
                } else {
                	sampleCount = 0;
                    continue;
                }

                
                try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ABANDON", "WE'RE IN onStartCommand()");
        mKeepRunning = true;
        mWifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        Thread ohCaptainMyCaptain = new Thread(new ShipCaptain(intent));
        ohCaptainMyCaptain.start();
        return START_REDELIVER_INTENT;
    }
    
    @Override
    public void onDestroy() {
        mIsRunning = false;
        mKeepRunning = false;
        return;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
