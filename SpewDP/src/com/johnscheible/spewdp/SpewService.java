package com.johnscheible.spewdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SpewService extends IntentService {
    private static final String TAG = "SpewService";
    public static final String EXTRA_IP_ADDRESS = "com.johnscheible.spewdp.IP_ADDRESS";
    public static final String EXTRA_PORT_NUMBER = "com.johnscheible.spewdp.PORT_NUMBER";
    
    static boolean sIsRunning;

    public SpewService() {
        super("SpewService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sIsRunning = true;

        while (true) {
            try {
                // Build a packet
                DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[256];
                InetAddress address = InetAddress
                        .getByName(intent.getStringExtra(EXTRA_IP_ADDRESS));
                int port = intent.getIntExtra(EXTRA_PORT_NUMBER, 4738);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

                Log.i(TAG, "Sending packet");
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
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        sIsRunning = false;
    }
}
