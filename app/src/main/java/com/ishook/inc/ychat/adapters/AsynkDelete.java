package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Created by Shaikh on 29-12-2017.
 */

public class AsynkDelete extends AsyncTask<String, String, String> {
    HttpURLConnection conn;
    URL url = null;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    Context context;

    public AsynkDelete(Context context) {
        this.context=context;
    }


    @Override
    protected String doInBackground(String... params) {
        try {
            url = new URL(" ");
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
                    .appendQueryParameter("friendId", params[2]);

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
        String errorStatus=null;


        try {

            if(!s.equals("unsuccessful")){
                JSONObject object =new JSONObject(s);
                Log.d("jo",object.toString());
                errorStatus=object.getString("errorStatus");}else {

                Toast.makeText(context,"Could't Process Your request!",Toast.LENGTH_LONG).show();
            }
            // {"errorStatus":false,"successMessage":"Request accepted successfully."}


            if (errorStatus.equals("false")){
                Toast.makeText(context,"Request cancel successfully.",Toast.LENGTH_SHORT).show();
                    }
            Log.d("result",s);



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}