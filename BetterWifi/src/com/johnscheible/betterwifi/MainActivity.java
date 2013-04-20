package com.johnscheible.betterwifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private Button mServiceButton;
    //private Button mDisconnectButton;
    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find all our views we need to manipulate...
        mServiceButton = (Button) findViewById(R.id.service_button);
        //mDisconnectButton = (Button) findViewById(R.id.disconnect_button);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the button depending on the SpewService's state
        if (BetterWifiManager.sIsRunning) {
            switchButtonToStop();
        } else {
            switchButtonToStart();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void switchButtonToStart() {
        mServiceButton.setText(R.string.start_button_label);
        mServiceButton.setOnClickListener(new View.OnClickListener() {           
            @Override
            public void onClick(View v) {
                if (!BetterWifiManager.sIsRunning) {
                    Intent intent = new Intent(MainActivity.this, BetterWifiManager.class);
                    
                    Log.i(TAG, "Starting BetterWifiManager");
                    startService(intent);

                    switchButtonToStop();
                }

            }
        });
    }

    private void switchButtonToStop() {
        mServiceButton.setText(R.string.stop_button_label);
        mServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BetterWifiManager.sIsRunning) {
                    Intent intent = new Intent(MainActivity.this, BetterWifiManager.class);

                    Log.i(TAG, "Stoping SpewService");
                    stopService(intent);
                }
                switchButtonToStart();
            }
        });
    }
    
    public void onDisconnectClick(View view) {
    	// If we're not connected to WiFi, let user know we can't disconnect
    	if (mConnectivityManager.getActiveNetworkInfo().getType() !=
    			ConnectivityManager.TYPE_WIFI) {
    		Toast.makeText(getApplicationContext(),
    				       "Not connected to WiFi",
    				       Toast.LENGTH_SHORT).show();
    	} else {
    		mWifiManager.disconnect();
    	}
    }
}
