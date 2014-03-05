    package com.cyberoamclient;

    import android.app.Service;
    import android.content.Context;
    import android.content.Intent;
    import android.net.wifi.WifiManager;
    import android.os.Handler;
    import android.os.IBinder;
    import android.util.Log;

    /**
     * Created by Shantanu on 7/1/13.
     */
    public class WifiService extends Service {
       // private final IBinder myBinder = new MyLocalBinder();

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
        private static final String TAG = "BroadcastService";
        public static final String BROADCAST_ACTION = "com.cyberoamclient.wifiservice.displayevent";
        private final Handler handler = new Handler();
        Intent intent;


        @Override
        public void onCreate() {
            super.onCreate();

            intent = new Intent(BROADCAST_ACTION);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
            return super.onStartCommand(intent, flags, startId);
        }


        private Runnable sendUpdatesToUI = new Runnable() {
            public void run() {
                DisplayLoggingInfo();
                handler.postDelayed(this, 3000); // 10 seconds
            }
        };

        private void DisplayLoggingInfo() {


            WifiManager.WifiLock wifiLock;
            int percentage=0;
            try
            {
                WifiManager mywifiManager = (WifiManager)WifiService.this.getSystemService(Context.WIFI_SERVICE);
                wifiLock = mywifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF , "MyWifiLock");
                if(!wifiLock.isHeld()){
                    wifiLock.acquire();}
                int rssi = mywifiManager.getConnectionInfo().getRssi();
                int level = WifiManager.calculateSignalLevel(rssi, 10);
                 percentage = (int) ((level/10.0)*100);
                wifiLock.release();
               // return percentage;

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }



            Log.d(TAG, "entered DisplayLoggingInfo\n"+percentage);
           // Toast.makeText(getApplicationContext(),percentage+"",Toast.LENGTH_SHORT).show();
            intent.putExtra("strength", percentage);
            sendBroadcast(intent);
        }


        @Override
        public void onDestroy() {
            handler.removeCallbacks(sendUpdatesToUI);
            super.onDestroy();
        }
    }