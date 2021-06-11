package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.InboxAdapter;
import com.ishook.inc.ychat.list.Inboxlist;

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

public class InboxMessage extends AppCompatActivity {

    String sessionId;
    String UserId;
    RecyclerView rvInbox;
    Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_message);
        rvInbox= (RecyclerView) findViewById(R.id.rvInbox);
        rvInbox.setHasFixedSize(true);
        cont = getApplicationContext();
        setTitle("Inbox");

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionId = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        UserId = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

        new AsyncInbox().execute(sessionId,UserId);
    }

    private class AsyncInbox extends AsyncTask<String ,String,String> {
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"messages/index/inbox_andr");
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
            List<Inboxlist> data=new ArrayList<>();


            try {
                JSONObject object=new JSONObject(s);


                JSONArray jsonArray=object.getJSONArray("messages");
                Log.d("ja",jsonArray.toString());
                for (int i=0 ; i<jsonArray.length() ; i++){
                    JSONObject object1=jsonArray.getJSONObject(i);
                    Inboxlist inboxlist=new Inboxlist();

                    inboxlist.sender_id=object1.getString("friend_id");
                    Log.d("inbox",inboxlist.sender_id);
                    inboxlist.ProfilePic=object1.getString("ProfilePic");
                    Log.d("inbox",inboxlist.ProfilePic);
                    inboxlist.timeAgo=object1.getString("timeAgo");
                    Log.d("inbox",inboxlist.timeAgo);
                    inboxlist.user_name=object1.getString("friend_name");
                    inboxlist.body=object1.getString("body");

                    data.add(inboxlist);

                }

                rvInbox.setAdapter(new InboxAdapter(cont,data));
                rvInbox.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
