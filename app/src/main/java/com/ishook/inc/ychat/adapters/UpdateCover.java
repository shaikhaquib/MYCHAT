package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
 * Created by Shaikh on 04-Oct-17.
 */

public class UpdateCover extends AsyncTask<String,String,String>  {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    HttpURLConnection conn;
    URL url = null;

    private final Context context;
    private final ProgressBar updcoer_prog;
    ImageView coverpic;Bitmap imgpath;

    public UpdateCover(Context context, ProgressBar updcoer_prog, ImageView coverpic, Bitmap imgPath) {
        this.context=context;
        this.updcoer_prog=updcoer_prog;
        this.coverpic=coverpic;
        this.imgpath=imgPath;
    }

    @Override
    protected void onPreExecute() {
        updcoer_prog.setVisibility(View.VISIBLE);

    }

    @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            url = new URL(Global.HostName+"home/profile/cover_pic_image_save_json");

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "exception";
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
                    .appendQueryParameter("cover_pic", params[2])
                    .appendQueryParameter("img_extension", params[3]);
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
            Log.d("No Internet", String.valueOf(e));
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
        updcoer_prog.setVisibility(View.GONE);
        String errormsg=null;
        String succsmsg=null;

        try {

            JSONObject jsonObject=new JSONObject(s);
            errormsg=jsonObject.getString("error_msg");
            succsmsg=jsonObject.getString("success_msg");
            if (errormsg.isEmpty()) {
                coverpic.setImageBitmap(imgpath);
                Toast.makeText(context,succsmsg,Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context,"error occured while updating Coverpic!",Toast.LENGTH_LONG).show();
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
