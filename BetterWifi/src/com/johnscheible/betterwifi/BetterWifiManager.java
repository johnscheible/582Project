package com.johnscheible.betterwifi;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class BetterWifiManager extends Service {
    private static final String TAG = "SpewService";

    private Thread mThread;
    private boolean mKeepRunning;

    static boolean sIsRunning;

    public BetterWifiManager() {
        sIsRunning = false;
    }

    private final class BetterWifiRunner implements Runnable {
        
        public void run() {
            sIsRunning = true;
            Log.i(TAG, "Service running");
            while (mKeepRunning) {
                
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mKeepRunning = true;
        mThread = new Thread(new BetterWifiRunner());
        mThread.start();
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