package com.johnscheible.spewdp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private EditText mIpAddressTextField;
    private EditText mPortNumberTextField;
    private Button mServiceButton;
    private Spinner mPolicySpinner;
//    private ConnectivityManager mConnectivityManager;
    private ComponentName mPolicyReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Find all our views we need to manipulate...
        mIpAddressTextField = (EditText) findViewById(R.id.ip_address);
        mPortNumberTextField = (EditText) findViewById(R.id.port_number);
        mServiceButton = (Button) findViewById(R.id.service_button);
        mPolicySpinner = (Spinner) findViewById(R.id.policy_spinner);
//        mConnectivityManager =
//        		(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up the service button depending on the SpewService's state
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
                    if (!SpewService.sIsRunning) {
                    	// Build Intent
                        Intent intent = new Intent(MainActivity.this, SpewService.class);
                        intent.putExtra(SpewService.EXTRA_IP_ADDRESS, ipAddress);
                        intent.putExtra(SpewService.EXTRA_PORT_NUMBER, portNumber);
                    
                        // Determine which policy to use
                        String policy = mPolicySpinner.getSelectedItem().toString();
                        if (policy.equals(getString(R.string.abandon_ship))) {
                        	mPolicyReceiver = new ComponentName(getApplicationContext(), 
		                                                        AbandonShipReceiver.class);
                        } else if (policy.equals(getString(R.string.bouncer))) {
                        	mPolicyReceiver = new ComponentName(getApplicationContext(), 
                        			                            BouncerReceiver.class);
                        } else if (policy.equals(getString(R.string.none))) {
                        	mPolicyReceiver = null;; // We want mPolicyReceiver to stay false, but don't want to return
                        } else {
                        	Log.e(TAG, "Unrecognized policy. Not starting spewing");
                        	return;
                        } 
                        
                        // Register the policy to handle WiFi changes
                        if (mPolicyReceiver != null) {
                        	Log.i(TAG, "Enabling " + policy + " for WiFi receiver");
                        	PackageManager pm = getApplicationContext().getPackageManager();
                        	pm.setComponentEnabledSetting(mPolicyReceiver,
                        			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        			PackageManager.DONT_KILL_APP);
                        } else {
                        	Log.i(TAG, "No policy enabled: control run");
                        }
                        
                        // Start Spewing
                        Log.i(TAG, "Starting SpewService");
                        startService(intent);
                        
                        switchButtonToStop();
                    } else {
                    	Log.d(TAG, "Tried to start SpewService when it was running");
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
    
    private void switchButtonToStop() {
        mServiceButton.setText(R.string.stop_button_label);
        mServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	AbandonShipService.mKeepRunning = false;
            	
                if (SpewService.sIsRunning) {
                    Intent intent = new Intent(MainActivity.this, SpewService.class);
                    
                    // Stop SpewService
                    Log.i(TAG, "Stoping SpewService");
                    stopService(intent);
                    
                    // Unregister our policy
                    	PackageManager pm = getApplicationContext().getPackageManager();
                    	pm.setComponentEnabledSetting(
                    			new ComponentName(getApplicationContext(), AbandonShipReceiver.class),
                    			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    			PackageManager.DONT_KILL_APP);
                    	pm.setComponentEnabledSetting(
                    			new ComponentName(getApplicationContext(), BouncerReceiver.class),
                    			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    			PackageManager.DONT_KILL_APP);
                    	Log.i(TAG, "Disabled WiFi connection receivers");
                } else {
                	Log.d(TAG, "Tried to stop SpewService when it wasn't running");
                }
                switchButtonToStart();
            }
        });
    }
    
//    public void switchPolicy(View view) {
//    	String policy = mPolicySpinner.getSelectedItem().toString();
//    	Log.i(TAG, "Policy set to " + policy);
//    	Toast.makeText(this, "Policy set to " + policy, Toast.LENGTH_SHORT).show();
//    }
}
