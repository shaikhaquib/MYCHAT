package com.ishook.inc.ychat.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;

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

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.app.MainActivity;

public class CreateGroup extends AppCompatActivity {

    EditText etGroupName;
    Button CreateGroup;
    String sessionid;
    String userid;
    String jabber_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        etGroupName= (EditText) findViewById(R.id.etGroupname);
        CreateGroup= (Button) findViewById(R.id.Creat_group);

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        userid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        jabber_user = sharedPreferences.getString(Constants.KEY_jabber_user, "N/A");

        CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String room_name=etGroupName.getText().toString();

                new AsyncCreateGroup().execute(userid,sessionid,room_name,jabber_user);
            }
        });



    }

    private class AsyncCreateGroup  extends AsyncTask<String, String, String>{

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading =new ProgressDialog(CreateGroup.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tCreating...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }



        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"chatmessage/chat/create_chat_room_andr");
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
                        .appendQueryParameter("UserId", params[0])
                        .appendQueryParameter("sessionId", params[1])
                        .appendQueryParameter("room_alias_name", params[2])
                        .appendQueryParameter("jabber_user", params[3]);
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
            pdLoading.dismiss();
            try {
                JSONObject object=new JSONObject(s);

                if (object.getString("errorStatus").equals("false")){

                    Toast.makeText(getApplicationContext(),object.getString("success_msg"),Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                                 }else {
                    Toast.makeText(getApplicationContext(),object.getString("success_msg"),Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
