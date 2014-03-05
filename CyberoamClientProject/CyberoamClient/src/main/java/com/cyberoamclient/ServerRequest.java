package com.cyberoamclient;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by shantanu on 12/2/14.
 */
public class ServerRequest extends AsyncTask<Void,Void,String> {
    public static String output="";
    ItemList itemList;
    Context context;
    String Username,Password,Mode;

    public ServerRequest(String username,String password,String mode,Context c){
        this.Username=username;
        this.Password=password;
        this.Mode=mode;
        this.context=c;
    }

    @Override
    protected String doInBackground(Void... arg0) {
        DefaultHttpClient httpclient = new MyHttpClient(context);
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
            ArrayList<String> listManu = itemList.getMessage();
            Intent resultIntent = new Intent(context, StartActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle("Cyberoam Client")
                            .setContentText(listManu.get(0).substring(9))
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .setDefaults(-1);

            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.


            mNotificationManager.notify(1, mBuilder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("Output",itemList.getMessage().get(0));
    }


}

