package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.app.MainActivity;
import com.ishook.inc.ychat.list.Wire_list;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

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
import java.util.Arrays;
import java.util.List;

public class Search_Result extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    String UserData;
    ImageView profilepic;
    TextView Username;
    FloatingActionButton btnAddfriend;
    Button btnMessage;
    String friend_id;
    String sessionid=null;
    String userid=null;
    RecyclerView rvWire;
    Context cont;
    FriendWireAdapt adapter;
    ImageView cover;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Search_Result.this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
// Handling data from Intent
        getextra();
        initialize_object();
        sharepref();

        cont=getApplicationContext();

        new Asyncfriendwire().execute(sessionid,userid,friend_id);



    }

    private void sharepref() {

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        userid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
    }

    private void initialize_object() {
        profilepic= (ImageView) findViewById(R.id.SearchProfilePic);
       // Username= (TextView) findViewById(R.id.SearchUsername);
        btnAddfriend= (FloatingActionButton) findViewById(R.id.addfriend);
        //btnMessage= (Button) findViewById(R.id.Message);
        rvWire= (RecyclerView) findViewById(R.id.rv_friend_wire);
        rvWire.setNestedScrollingEnabled(false);
        cover= (ImageView) findViewById(R.id.cover);
        toolbar= (Toolbar) findViewById(R.id.srchtoolbar);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.srch_collapse);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.trans)); // transperent color = #00000000
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorWhite));

        btnAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncAddfriend(getApplicationContext()).execute(sessionid,userid,friend_id);
                btnAddfriend.setVisibility(View.GONE);
            }
        });

    }

    private void getextra() {
        Bundle postdata = getIntent().getExtras();
        if (postdata == null) {
            return;
        }
        friend_id=(getIntent().getStringExtra("search_data"));

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }



    private class Asyncfriendwire extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"activity/index/wire_json");
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
                        .appendQueryParameter("otherUserId", params[2]);
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

            //       String listOfPosts = null;
            String post = null;
            String mediaList=null;
            Log.d("result",result);
            //  String stats=null;





            List<Wire_list> data=new ArrayList<>();

            try {


                JSONObject jsonObject=new JSONObject(result);

                JSONObject object_detail=new JSONObject(jsonObject.getString("userDetail"));

                Glide.with(getApplicationContext()).load(object_detail.getString("ProfilePic")).into(profilepic);
                Glide.with(getApplicationContext()).load(object_detail.getString("CoverPic")).into(cover);
                toolbar.setTitle(object_detail.getString("UserName"));


                String stats=jsonObject.getString("stats");
                JSONObject jstats=new JSONObject(stats);

                String wire_count =jstats.getString("wire_count");


                JSONArray listOfPosts=jsonObject.getJSONArray("listOfPosts");



                for(int i=0;i<listOfPosts.length();i++){
                    JSONObject json_data = listOfPosts.getJSONObject(i);
                    post=json_data.getString("post");
                    mediaList=json_data.getString("mediaList");
                    JSONObject postobject=new JSONObject(post);

                    Wire_list wire_list =new Wire_list();
                    wire_list.sessionid=sessionid;
                    wire_list.userid=userid;

                    wire_list.sujectname =postobject.getString("ObjectUserName");
                    Log.d("SubjectUserName",postobject.getString("ObjectUserName"));
                    wire_list.objectname =postobject.getString("SubjectUserName");
                    Log.d("ObjectUserName",postobject.getString("SubjectUserName"));
                    wire_list.Subject =postobject.getString("ObjectFirstName");
                    wire_list.uplodetime=postobject.getString("timeAgo");
                    Log.d("timeAgo",postobject.getString("timeAgo"));
                    wire_list.wiredtext =postobject.getString("body");
                    Log.d("wiretext",wire_list.wiredtext);
                    wire_list.link_preview=postobject.getString("link_preview");

                    Log.d("Link", wire_list.link_preview);

                    org.jsoup.nodes.Document doc = Jsoup.parse(wire_list.link_preview);

                    Elements element1=doc.getElementsByClass("img_link");
                    Elements element2=doc.getElementsByClass("description");
                    Elements element3=doc.getElementsByClass("url");
                    Elements element4=doc.select("div.title");
                    String title=element4.text();
                    String image=element1.attr("src");
                    String descrpt=element2.text();
                    String url=element3.text();



                    Log.d("image",image);
                    wire_list.thumbimg=image;
                    Log.d("descrpt",descrpt);
                    wire_list.thumbdesc=descrpt;
                    Log.d("url",url);
                    wire_list.thumbUrl=url;
                    Log.d("title",title);
                    wire_list.thumbtitle=title;



                    wire_list.subject_dp=postobject.getString("ObjectProfilePic");
                    wire_list.postLikes=json_data.getString("postLikes");
                    wire_list.SelfLikes=json_data.getString("selfLikes");
                    Log.d("selflike",wire_list.SelfLikes);
                    wire_list.CommentsCounts=json_data.getString("postComments");
                    Log.d("postComments",wire_list.CommentsCounts);


                    wire_list.ActionId =postobject.getString("action_id");
                    Log.d("acction_id",wire_list.ActionId);





                    JSONArray jsonArray=json_data.getJSONArray("mediaList");
                    wire_list.media=new String[jsonArray.length()] ;
                    for(int j=0;j<jsonArray.length();j++){

                        JSONObject object = jsonArray.getJSONObject(j);
                        Log.d("m_arr", String.valueOf(object));
                        wire_list.wiredimage=object.getString("thumbImage");
                        Log.d("wimg",wire_list.wiredimage);



                        wire_list.media[j]=Global.HostName+ object.getString("thumbImage");




                    }
                    Log.d("img_array", Arrays.toString(wire_list.media));


                    data.add(wire_list);
                }



                // Log.d("wireadapter", String.valueOf(adapter.getItemCount()));
                adapter=new FriendWireAdapt(cont,data);
                rvWire.setAdapter(adapter);
                //rvWire.setAdapter(new WiredAdapter(cont,data));


                rvWire.setLayoutManager(new LinearLayoutManager(cont));




            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("come here");
               // new_wire.setVisibility(View.VISIBLE);
               // no_wire.setVisibility(View.VISIBLE);
            }


            //   Toast.makeText(getContext(),mediaList,Toast.LENGTH_LONG).show();
        }
    }

}
