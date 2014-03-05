package com.cyberoamclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.GregorianCalendar;

/**
 * Created by shantanu on 12/2/14.
 */
public class AlarmReciever extends BroadcastReceiver {
    String Username,Password,Mode;
    Boolean AutoLogin;
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Log.e("Alaem reciever", "true");
        getResult();
        ConnectivityManager connManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            new ServerRequest(Username,Password,Mode,this.context);
            if(AutoLogin){
               ScheduleAlarm(this.context);
            }
        }

    }

    private void ScheduleAlarm(Context context) {
        Long time = new GregorianCalendar().getTimeInMillis()+60*60*1000;
        Intent intentAlarm = new Intent(context,AlarmReciever.class);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(context, 1, intentAlarm, 0));
    }

    public void getResult(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Username=sharedPrefs.getString("prefUsername","NULL");
        Password=sharedPrefs.getString("prefPassword","NULL");
        AutoLogin=sharedPrefs.getBoolean("prefAutoLogin",false);
    }

}
