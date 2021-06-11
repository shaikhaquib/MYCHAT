package com.ishook.inc.ychat.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.AlbumAdapt;
import com.ishook.inc.ychat.list.Album_list;

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

public class Album extends AppCompatActivity {

    RecyclerView rvAlbum;
    String Sid;
    String Uid;
    TextView textView;
    String AlbumUserId;
    Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        rvAlbum= (RecyclerView) findViewById(R.id.rvAlbum);
        rvAlbum.setHasFixedSize(true);
        cont=getApplicationContext();
        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        Sid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        Uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        AlbumUserId = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        rvAlbum.setLayoutManager(new GridLayoutManager(cont,2));
        textView= (TextView) findViewById(R.id.no_Album);
        new GetAlbum().execute(Sid,Uid,AlbumUserId);
    }

    private class GetAlbum extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading =new ProgressDialog(Album.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }



        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"albums/album_web_service/get_album_list_json");
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
                        .appendQueryParameter("AlbumUserId", params[2])
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
            List<Album_list> data=new ArrayList<>();

            try {
                JSONObject object =new JSONObject(s);

                JSONArray jsonArray=object.getJSONArray("AlbumList");

                for (int i = 0 ; i < jsonArray.length() ; i++){

                    JSONObject object1=jsonArray.getJSONObject(i);
                    Album_list album_list=new Album_list();

                    album_list.AlbumId=object1.getString("AlbumId");
                    album_list.AlbumName=object1.getString("AlbumName");
                    album_list.thumbImage=Global.HostName+object1.getString("thumbImage");
                    Log.d("Album",album_list.thumbImage);
                    album_list.CreatedOn=object1.getString("CreatedOn");

                    data.add(album_list);

                }

                rvAlbum.setAdapter(new AlbumAdapt(cont,data));


            } catch (JSONException e) {
                textView.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

        }
    }


}
