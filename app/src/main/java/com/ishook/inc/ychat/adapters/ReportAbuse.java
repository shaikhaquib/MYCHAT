package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ishook.inc.ychat.Global;

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

/**
 * Created by Shaikh on 18-01-2018.
 */

   public class ReportAbuse extends AsyncTask<String,String,String> {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    HttpURLConnection conn;
    URL url = null;
    Context context;


    @Override
    protected String doInBackground(String... params) {
        try {
            url = new URL(Global.HostName+"users/reportAbuse/report_abuse_json");
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
                    .appendQueryParameter("object_id", params[2])
                    .appendQueryParameter("object_type", params[3])
                    .appendQueryParameter("comment", params[4]);
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
        Boolean error_flag;
        String error_msg;
        try {
            JSONObject object=new JSONObject(s);

            error_flag=object.getBoolean("error_flag");
            error_msg=object.getString("error_msg");
            if (object.getString("success").isEmpty()){
                Toast.makeText(context,error_msg,Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context,object.getString("success"),Toast.LENGTH_LONG).show();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}