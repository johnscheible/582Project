package com.johnscheible.spewdp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private EditText mIpAddressTextField;
    private EditText mPortNumberTextField;
    private Button mServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Find all our views we need to manipulate...
        mIpAddressTextField = (EditText) findViewById(R.id.ip_address);
        mPortNumberTextField = (EditText) findViewById(R.id.port_number);
        mServiceButton = (Button) findViewById(R.id.service_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the button depending on the SpewService's state
        if (SpewService.sIsRunning) {
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
                String ipAddress = mIpAddressTextField.getText().toString();
                try {
                    int portNumber = Integer.parseInt(mPortNumberTextField.getText().toString());
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                }
                
                if (!SpewService.sIsRunning) {
                    Intent intent = new Intent(MainActivity.this, SpewService.class);
                    intent.putExtra(SpewService.EXTRA_IP_ADDRESS, ipAddress);
                    intent.putExtra(SpewService.EXTRA_PORT_NUMBER, portNumber);
                
                    Log.i(TAG, "Starting SpewService");
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
                if (SpewService.sIsRunning) {
                    Intent intent = new Intent(MainActivity.this, SpewService.class);
                    
                    Log.i(TAG, "Stoping SpewService");
                    stopService(intent);
                }
                switchButtonToStart();
            }
        });
    }

}
