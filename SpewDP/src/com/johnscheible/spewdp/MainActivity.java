package com.johnscheible.spewdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    private EditText mIpAddress;
    private EditText mPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mIpAddress = (EditText) findViewById(R.id.ip_address);
        mPortNumber = (EditText) findViewById(R.id.port_number);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void sendPing(View view) {
        new Thread(new Runnable() {
            public void run() {
                /* Build a packet */
                try {
                    DatagramSocket socket = new DatagramSocket();
                    byte[] buf = new byte[256];
                    InetAddress address = InetAddress.getByName(mIpAddress.getText().toString());
                    int port = Integer.parseInt(mPortNumber.getText().toString());
                    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                } catch (IOException e) {
                    Log.e("SpewDP", e.getLocalizedMessage());
                }
            }
        }).start();
    }

}
