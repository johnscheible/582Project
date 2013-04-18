package com.johnscheible.spewdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

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
            while (mKeepRunning) {
                // Log WiFi State
                if (mWifiManager.isWifiEnabled()) {
                    WifiInfo wi = mWifiManager.getConnectionInfo();
                    Log.i(TAG, "Sending packet " + sequenceNumber + ": " + wi.getRssi());
                } else {
                    Log.i(TAG, "Sending packet " + sequenceNumber + ": MOBILE");
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
