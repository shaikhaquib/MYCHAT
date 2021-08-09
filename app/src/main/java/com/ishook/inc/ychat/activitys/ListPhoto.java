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

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;

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

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.adapters.PhotoListAdapt;
import com.ishook.inc.ychat.list.Photolist;

public class ListPhoto extends AppCompatActivity {

    RecyclerView rvPhoto;
    String Sid;
    String Uid;
    String albumid;
    Context cont;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_photo);

        rvPhoto= (RecyclerView) findViewById(R.id.rvPhotolist);
        rvPhoto.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        cont=getApplicationContext();
        setTitle("Photo's");

        Bundle postdata = getIntent().getExtras();
        if (postdata == null) {
            return;
        }

        albumid= getIntent().getStringExtra("Aid");
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        Sid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        Uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

        new GetAlbum().execute(Uid,Sid,albumid);
    }
    private class GetAlbum extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        ProgressDialog pdLoading =new ProgressDialog(ListPhoto.this);

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
                url = new URL(Global.HostName+"albums/album_web_service/view_album_json");
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
                        .appendQueryParameter("AlbumId", params[2])
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
            List<Photolist>data=new ArrayList<>();

            try {
                JSONObject object =new JSONObject(s);

                JSONArray jsonArray=object.getJSONArray("listOfPhotos");

                for (int i = 0 ; i < jsonArray.length() ; i++){

                    JSONObject object1=jsonArray.getJSONObject(i);
                    Photolist album_list=new Photolist();

                    album_list.AlbumId=object1.getString("AlbumId");
                    album_list.likeCount=object1.getString("likeCount");
                    album_list.mediaLink=Global.HostName+object1.getString("mediaLink");
                    album_list.MediaId=object1.getString("MediaId");
                    album_list.UserId=object1.getString("UserId");
                    album_list.selfLike=object1.getBoolean("selfLike");
                    album_list.sessionid=Sid;
                    album_list.commentCount =object1.getString("commentCount");





                    data.add(album_list);

                }

                rvPhoto.setAdapter(new PhotoListAdapt(cont,data));


            } catch (JSONException e) {
              //  textView.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }

        }
    }

}
