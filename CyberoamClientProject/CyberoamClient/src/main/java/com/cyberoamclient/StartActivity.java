package com.cyberoamclient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class StartActivity extends Activity implements View.OnClickListener {

    private static final int RESULT_SETTINGS = 1;
    private String Username,Password;
    private String Mode="191";
    private Button b1,b2;
    private TextView t1;
    private static final String TAG = "BroadcastTest";
    private Intent intent;
    private Boolean AutoLogin;
    private ProgressBar progressBar;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getResult();

        b1 = (Button)findViewById(R.id.login);
        b2 = (Button)findViewById(R.id.logout);
        t1 = (TextView)findViewById(R.id.output);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);

        progressBar= (ProgressBar)findViewById(R.id.progressBar);
        //intent = new Intent(this, WifiService.class);
        //startService(intent);

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    private void updateUI(Intent intent) {
        int strength = intent.getIntExtra("strength",0);
        //String time = intent.getStringExtra("time");

        Log.d(TAG, strength+"");
           Toast.makeText(StartActivity.this,strength+"",Toast.LENGTH_SHORT).show();
        if (strength<=40){
          Mode="193";
           // Toast.makeText(StartActivity.this,"ok unregistering",Toast.LENGTH_SHORT).show();
            unregisterReceiver(broadcastReceiver);
            stopService(intent);
           // Toast.makeText(StartActivity.this,"ok unregistered",Toast.LENGTH_SHORT).show();

            new ClickAction().execute();
            // Build notification
            // Actions are just fake



        }

        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent i = new Intent(this, UserSetting.class);
                startActivityForResult(i, RESULT_SETTINGS);
                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
            {
                getResult();
                 }

                break;

        }

    }

    public void getResult(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Username=sharedPrefs.getString("prefUsername","NULL");
        Password=sharedPrefs.getString("prefPassword","NULL");
        AutoLogin=sharedPrefs.getBoolean("prefAutoLogin",false);
    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {

        if (view == b1){


           // registerReceiver(broadcastReceiver, new IntentFilter(WifiService.BROADCAST_ACTION));
           // startService(intent);
            Mode = "191";
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {

            Log.e("wifi" ,"Connected"+AutoLogin);
            if(AutoLogin){
                Log.e("Auto Logiin","true");
            ScheduleAlarm();
            }
            progressBar.setVisibility(View.VISIBLE);
            new ClickAction().execute();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please Check Your Wifi Connection",Toast.LENGTH_LONG).show();
            }
        }
        if (view == b2){
            /* ******For AUTO DISCONNECT **** /
          //  registerReceiver(broadcastReceiver, new IntentFilter(WifiService.BROADCAST_ACTION));
           // unregisterReceiver(broadcastReceiver);
           // stopService(intent);
            */

            Mode ="193";
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {
                new ClickAction().execute();
            }
            else{
                Toast.makeText(getApplicationContext(),"Please Check Your Wifi Connection",Toast.LENGTH_LONG).show();
            }

        }
    }

    public class ClickAction extends AsyncTask<Void,Void,String>{
        String output="";
        ItemList itemList;


        @Override
        protected String doInBackground(Void... voids) {

            DefaultHttpClient httpclient = new MyHttpClient(getApplicationContext());
            HttpPost httppost = new HttpPost("https://10.100.56.55:8090/httpclient.html");
            StringBuilder builder = new StringBuilder();
            try {
                Long time= (new Date()).getTime();
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                nameValuePairs.add(new BasicNameValuePair("username", Username));
                nameValuePairs.add(new BasicNameValuePair("password", Password));
                nameValuePairs.add(new BasicNameValuePair("mode", Mode));
                nameValuePairs.add(new BasicNameValuePair("a", time.toString()));
                nameValuePairs.add(new BasicNameValuePair("producttype", "0"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e("==>", "Failed to download file");
                }

            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            try {
                output = builder.toString();
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return output;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try
            {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();
                XMLReader xr = sp.getXMLReader();

                /** Create handler to handle XML Tags ( extends DefaultHandler ) */
                MyXMLHandler myXMLHandler = new MyXMLHandler();
                xr.setContentHandler(myXMLHandler);

                ByteArrayInputStream is = new ByteArrayInputStream(s.getBytes());
                xr.parse(new InputSource(is));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
         try{
            itemList = MyXMLHandler.itemList;

             Intent resultIntent = new Intent(getApplicationContext(), StartActivity.class);

            ArrayList<String> listManu = itemList.getMessage();

             PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, 0);
             t1.setText(listManu.get(0));
             NotificationCompat.Builder mBuilder =
                     new NotificationCompat.Builder(StartActivity.this)
                             .setSmallIcon(R.drawable.ic_launcher)
                             .setContentTitle("Cyberoam Client")
                             .setContentText(listManu.get(0).substring(9))
                             .setContentIntent(pIntent)
                              .setAutoCancel(true)
                             .setDefaults(-1);

             NotificationManager mNotificationManager =
                     (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.


             mNotificationManager.notify(1, mBuilder.build());
             if(progressBar.isShown()){
                 progressBar.setVisibility(View.INVISIBLE);
             }

         }
         catch (Exception e){
             e.printStackTrace();
         }

        }



    }
    public void ScheduleAlarm() {
        Log.e("scheduke alarm","here");
        Long time = new GregorianCalendar().getTimeInMillis() + 60*60*1000;
        Intent intentAlarm = new Intent(getApplicationContext(), AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 1 * 60 * 1000,
                PendingIntent.getBroadcast(getApplicationContext(), 0, intentAlarm, 0));
    }


}
