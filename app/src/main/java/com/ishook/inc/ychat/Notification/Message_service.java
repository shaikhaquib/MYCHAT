package com.ishook.inc.ychat.Notification;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import androidx.annotation.Nullable;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Shaikh on 13-01-2018.
 */

public class Message_service extends Service {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    String sessionId;
    String UserId;
    String msg_type="1";
    String fetch_data="true";
    int sleepTime;
    int mid;
    String OldMessage;
    ResultReceiver myReciver;
    private static int FIRST_ELEMENT = 0;



    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 0, 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        if (intent!=null) {
            sleepTime = intent.getIntExtra("sleepTime", 0);
            mid = intent.getIntExtra("mid", 1);
            myReciver = intent.getParcelableExtra("myReciver");
        }

        return START_STICKY;
    }
    private Timer mTimer;


    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            sessionId = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
            UserId = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

            String freindid= String.valueOf(sleepTime);

            new UserConversation().execute(sessionId,UserId,freindid);


        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mTimer.cancel();
            timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();}

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private class UserConversation extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;



        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"messages/index/conversation_andr");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("friend_id", params[2]);

                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String id=null;
            String s1 = null;

            SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
             OldMessage=sharedPreferences.getString(Constants.KEY_OldMessage_Count,"N/A");


            try {

                JSONObject object = new JSONObject(s);
                //  Message_Count = object.getString("messages_count");
           /*    */

                JSONArray listOfriend = object.getJSONArray("all_messages");

                if (listOfriend.length() > 0) {
                    s1= listOfriend.getJSONObject(FIRST_ELEMENT).toString();// parse the date instead of toString()
                }

                if (s1 != null){

                JSONObject jsonObject=new JSONObject(s1);
                id=jsonObject.getString("id");
                System.out.println("IDDDD"+id);

                if(id != null){

                    int iid = Integer.parseInt(id);

                    if (iid!=mid){

                        // You can also include some extra data.

                        Bundle bundle = new Bundle();
                        bundle.putString("Location",s);
                        myReciver.send(18,bundle);
                    }



                }}



                   /* rvChat.setAdapter(new Chat_adapter(cont, data));
                    recyclerViewlayoutManager.setStackFromEnd(true);
                    rvChat.setLayoutManager(recyclerViewlayoutManager);*/

            } catch(JSONException e){
                e.printStackTrace();
            }

        }
    }


}
