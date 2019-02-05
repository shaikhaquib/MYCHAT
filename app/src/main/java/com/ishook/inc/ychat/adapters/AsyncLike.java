package com.ishook.inc.ychat.adapters;

import android.net.Uri;
import android.os.AsyncTask;

import com.ishook.inc.ychat.Global;

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

import static com.ishook.inc.ychat.adapters.WiredAdapter.CONNECTION_TIMEOUT;
import static com.ishook.inc.ychat.adapters.WiredAdapter.READ_TIMEOUT;






/**
 * Created by Shaikh on 20-Sep-17.
 */

public class AsyncLike extends AsyncTask<String,String,String> {
    HttpURLConnection conn;
    URL url = null;






    @Override
    protected String doInBackground(String... params) {
        try {
            url = new URL(Global.HostName+"activity/index/do_like_json");
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
                    .appendQueryParameter("postId", params[2]);
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
    protected void onPostExecute(String result) {
    }
}
