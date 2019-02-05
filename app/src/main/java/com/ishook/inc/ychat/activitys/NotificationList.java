package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.NotiAdapt;
import com.ishook.inc.ychat.list.NotiList;

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
import java.util.ArrayList;
import java.util.List;

public class NotificationList extends AppCompatActivity {

    String sessionId;
    String UserId;
    String msg_type="1";
    String fetch_data="true";
    RecyclerView rvNoti;
    Context cont;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        rvNoti= (RecyclerView) findViewById(R.id.rvNotification);
        rvNoti.setHasFixedSize(true);
        rvNoti.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        setTitle("Notification");

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionId = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        UserId = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

        cont=getApplicationContext();

        new AsyncNoti().execute(sessionId,UserId,msg_type,fetch_data);
    }

    private class AsyncNoti extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"notifications/index/get_msg_data_andr");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("msg_type", params[2])
                        .appendQueryParameter("fetch_data",params[3])
                        ;
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
                    Log.d("Result",result.toString());
                    return(result.toString());


                }else{

                    return("unsuccessful");
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

            String Sucssmsg = null;

            List<NotiList> data=new ArrayList<>();

            try {

                JSONObject object = new JSONObject(s);
                Sucssmsg = object.getString("success_msg");
                JSONArray msg_data=object.getJSONArray("msg_data");

                for (int i=0; i<msg_data.length();i++){

                    JSONObject jsonObject =msg_data.getJSONObject(i);
                    NotiList notiList=new NotiList();

                    notiList.notification_body=jsonObject.getString("notification_body");
                    notiList.notification_date=jsonObject.getString("notification_date");
                    notiList.notification_id=jsonObject.getString("notification_id");
                    notiList.notification_user_name=jsonObject.getString("notification_user_name");
                    notiList.notification_user_photo=jsonObject.getString("notification_user_photo");


                    data.add(notiList);
                }
                rvNoti.setAdapter(new NotiAdapt(cont,data));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}