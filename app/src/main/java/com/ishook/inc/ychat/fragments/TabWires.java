package com.ishook.inc.ychat.fragments;


/**
 * Created by Aquib on 21/01/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Upload_New_Wire;
import com.ishook.inc.ychat.adapters.WiredAdapter;
import com.ishook.inc.ychat.list.Wire_list;
import com.ishook.inc.ychat.list.upload_text;

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

@RequiresApi(api = Build.VERSION_CODES.M)
public class TabWires extends Fragment {


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView rvWire;
    String sessionid = null;
    String userid = null;
    LinearLayout no_wire;
    List<Wire_list> data = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Context cont;
    private int page;
    public ProgressBar progressBar;
    String[] media;
    WiredAdapter adapter;
    TextView new_wire;
    String fristwire;
    String fristwire_id;
    private static int FIRST_ELEMENT = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_wire, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.loadwire);

        no_wire = (LinearLayout) rootView.findViewById(R.id.no_wire);
        new_wire = (TextView) rootView.findViewById(R.id.wire_again);

        new_wire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cont, Upload_New_Wire.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getContext().getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        String profile = sharedPreferences.getString(Constants.KEY_PROFILE, "N/A");
        String profiled = sharedPreferences.getString(Constants.KEY_ProfileDetail, "N/A");
        Log.d("profiledetail", profile);
        try {
            JSONObject object = new JSONObject(profile);
            userid = object.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rvWire = (RecyclerView) rootView.findViewById(R.id.rvWiredlist);
        rvWire.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());


        cont = getActivity();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swifeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncFetch().execute(sessionid, userid);
            }
        });
/*
        rvWire.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {


new AsyncFetch().execute(sessionid, userid);


*/
        new AsyncFetch().execute(sessionid, userid);

        return rootView;
    }


    private class AsyncFetch extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"activity/index/home_wire_json");
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
                        .appendQueryParameter("UserId", params[1]);
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
        protected void onPostExecute(String result) {
            mSwipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            //       String listOfPosts = null;
            String post = null;
            String mediaList = null;
            Log.d("result", result);
            //  String stats=null;

            if (result != "exception") {


                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getContext().getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_Post_Result, result);


                editor.apply();
            } else {
                Toast.makeText(getContext(), "You are offline!", Toast.LENGTH_SHORT).show();
            }


            List<Wire_list> data = new ArrayList<>();

            try {

                SharedPreferences preferences = getActivity().getSharedPreferences(getContext().getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                String postss = preferences.getString(Constants.KEY_Post_Result, "N/A");
                JSONObject jsonObject = new JSONObject(postss);

                String stats = jsonObject.getString("stats");
                JSONObject jstats = new JSONObject(stats);

                String wire_count = jstats.getString("wire_count");


                JSONArray listOfPosts = jsonObject.getJSONArray("listOfPosts");


                if(jsonObject.getString("listOfPosts") == null){
                    System.out.println("come here");
                    new_wire.setVisibility(View.VISIBLE);
                    no_wire.setVisibility(View.VISIBLE);

                }

//                JSONArray jArray = new JSONArray(listOfPosts);


                // Extract data from json and store into ArrayList as class objects

                fristwire = listOfPosts.getJSONObject(FIRST_ELEMENT).toString();

                JSONObject fw = new JSONObject(fristwire);
                String po = fw.getString("post");
                JSONObject jpo = new JSONObject(po);

                fristwire_id = jpo.getString("action_id");
                Log.d("fid", fristwire);



                for (int i = 0; i < listOfPosts.length(); i++) {
                    JSONObject json_data = listOfPosts.getJSONObject(i);
                    post = json_data.getString("post");
                    mediaList = json_data.getString("mediaList");
                    JSONObject postobject = new JSONObject(post);

                    Wire_list wire_list = new Wire_list();
                    wire_list.sessionid = sessionid;
                    wire_list.userid = userid;
                    wire_list.subjectid=postobject.getString("object_id");
                    Log.d("Subjecttttttttttt",wire_list.subjectid+"ndndndnd");
                    wire_list.sujectname = postobject.getString("ObjectUserName");
                    Log.d("SubjectUserName", postobject.getString("ObjectUserName"));
                    wire_list.objectname = postobject.getString("SubjectUserName");
                    Log.d("ObjectUserName", postobject.getString("SubjectUserName"));
                    wire_list.Subject = postobject.getString("ObjectFirstName");
                    wire_list.uplodetime = postobject.getString("timeAgo");
                    Log.d("timeAgo", postobject.getString("timeAgo"));
                    wire_list.wiredtext = postobject.getString("body");
                    Log.d("wiretext", wire_list.wiredtext);
                    wire_list.link_preview = postobject.getString("link_preview");

                    Log.d("Link", wire_list.link_preview);

                    org.jsoup.nodes.Document doc = Jsoup.parse(wire_list.link_preview);

                    Elements element1 = doc.getElementsByClass("img_link");
                    Elements element2 = doc.getElementsByClass("description");
                    Elements element3 = doc.getElementsByClass("url");
                    Elements element4 = doc.select("div.title");
                    String title = element4.text();
                    String image = element1.attr("src");
                    String descrpt = element2.text();
                    String url = element3.text();


                    Log.d("image", image);
                    wire_list.thumbimg = image;
                    Log.d("descrpt", descrpt);
                    wire_list.thumbdesc = descrpt;
                    Log.d("url", url);
                    wire_list.thumbUrl = url;
                    Log.d("title", title);
                    wire_list.thumbtitle = title;


                    wire_list.subject_dp = postobject.getString("ObjectProfilePic");
                    wire_list.postLikes = json_data.getString("postLikes");
                    wire_list.SelfLikes = json_data.getString("selfLikes");
                    Log.d("selflike", wire_list.SelfLikes);
                    wire_list.CommentsCounts = json_data.getString("postComments");
                    Log.d("postComments", wire_list.CommentsCounts);

                    new_wire.setVisibility(View.GONE);
                    no_wire.setVisibility(View.GONE);
                    wire_list.ActionId = postobject.getString("action_id");
                    Log.d("acction_id", wire_list.ActionId);


                    JSONArray jsonArray = json_data.getJSONArray("mediaList");
                    wire_list.media = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject object = jsonArray.getJSONObject(j);
                        Log.d("m_arr", String.valueOf(object));
                        wire_list.wiredimage = object.getString("thumbImage");
                        Log.d("wimg", wire_list.wiredimage);


                        wire_list.media[j] = Global.HostName+ object.getString("thumbImage");


                    }
                    Log.d("img_array", Arrays.toString(wire_list.media));


                    data.add(wire_list);

                    wire(data);
                }


                // Log.d("wireadapter", String.valueOf(adapter.getItemCount()));



            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("come here");
                new_wire.setVisibility(View.VISIBLE);
                no_wire.setVisibility(View.VISIBLE);
            }


            //   Toast.makeText(getContext(),mediaList,Toast.LENGTH_LONG).show();
        }



    }
    public void wire(List<Wire_list> data) throws JSONException {
        adapter = new WiredAdapter(cont, data, getHeader());
        rvWire.setAdapter(adapter);
        //rvWire.setAdapter(new WiredAdapter(cont,data));
        rvWire.setLayoutManager(new LinearLayoutManager(cont));
    }
    public upload_text getHeader() {
        upload_text header = new upload_text();
        return header;
    }


    private class LoadMore extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute () {
            /*mSwipeRefreshLayout.setRefreshing(true);
            progressBar.setVisibility(View.VISIBLE);
*/
        }

        @Override
        protected String doInBackground (String...params){
            try {
                url = new URL(Global.HostName+"activity/index/load_more_posts_json");
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
                        .appendQueryParameter("firstWiredId", params[2]);
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
        protected void onPostExecute (String result){
            mSwipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            //       String listOfPosts = null;
            String post = null;
            String mediaList = null;
            Log.d("result", result);
            //  String stats=null;


            try {
                List<Wire_list> data = new ArrayList<>();


                JSONObject jsonObject = new JSONObject(result);

              /*  String stats = jsonObject.getString("stats");
                JSONObject jstats = new JSONObject(stats);

                String wire_count = jstats.getString("wire_count");*/


                JSONArray listOfPosts = jsonObject.getJSONArray("previousPosts");
/*

                if(listOfPosts.length()==0){
                    System.out.println("come here");
                    new_wire.setVisibility(View.VISIBLE);
                    no_wire.setVisibility(View.VISIBLE);

                }
*/

//                JSONArray jArray = new JSONArray(listOfPosts);


                // Extract data from json and store into ArrayList as class objects


                fristwire = listOfPosts.getJSONObject(FIRST_ELEMENT).toString();

                JSONObject fw = new JSONObject(fristwire);
                String po = fw.getString("post");
                JSONObject jpo = new JSONObject(po);

                fristwire_id = jpo.getString("action_id");
                Log.d("fid", fristwire);

                for (int i = 0; i < listOfPosts.length(); i++) {
                    JSONObject json_data = listOfPosts.getJSONObject(i);
                    post = json_data.getString("post");
                    mediaList = json_data.getString("mediaList");
                    JSONObject postobject = new JSONObject(post);

                    Wire_list wire_list = new Wire_list();
                    wire_list.sessionid = sessionid;
                    wire_list.userid = userid;



                    wire_list.sujectname = postobject.getString("ObjectUserName");
                    Log.d("SubjectUserName", postobject.getString("ObjectUserName"));
                    wire_list.objectname = postobject.getString("SubjectUserName");
                    Log.d("ObjectUserName", postobject.getString("SubjectUserName"));
                    wire_list.Subject = postobject.getString("ObjectFirstName");
                    wire_list.uplodetime = postobject.getString("timeAgo");
                    Log.d("timeAgo", postobject.getString("timeAgo"));
                    wire_list.wiredtext = postobject.getString("body");

                    Log.d("wiretext", wire_list.wiredtext);
                    wire_list.link_preview = postobject.getString("link_preview");

                    Log.d("Link", wire_list.link_preview);

                    org.jsoup.nodes.Document doc = Jsoup.parse(wire_list.link_preview);

                    Elements element1 = doc.getElementsByClass("img_link");
                    Elements element2 = doc.getElementsByClass("description");
                    Elements element3 = doc.getElementsByClass("url");
                    Elements element4 = doc.select("div.title");
                    String title = element4.text();
                    String image = element1.attr("src");
                    String descrpt = element2.text();
                    String url = element3.text();


                    Log.d("image", image);
                    wire_list.thumbimg = image;
                    Log.d("descrpt", descrpt);
                    wire_list.thumbdesc = descrpt;
                    Log.d("url", url);
                    wire_list.thumbUrl = url;
                    Log.d("title", title);
                    wire_list.thumbtitle = title;


                    wire_list.subject_dp = postobject.getString("ObjectProfilePic");
                    wire_list.postLikes = json_data.getString("postLikes");
                    wire_list.SelfLikes = json_data.getString("selfLikes");
                    Log.d("selflike", wire_list.SelfLikes);
                    wire_list.CommentsCounts = json_data.getString("postComments");
                    Log.d("postComments", wire_list.CommentsCounts);


                    wire_list.ActionId = postobject.getString("action_id");
                    Log.d("acction_id", wire_list.ActionId);


                    JSONArray jsonArray = json_data.getJSONArray("mediaList");
                    wire_list.media = new String[jsonArray.length()];
                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject object = jsonArray.getJSONObject(j);
                        Log.d("m_arr", String.valueOf(object));
                        wire_list.wiredimage = object.getString("thumbImage");
                        Log.d("wimg", wire_list.wiredimage);


                        wire_list.media[j] = Global.HostName+ object.getString("thumbImage");


                    }
                    Log.d("img_array", Arrays.toString(wire_list.media));


                    data.add(wire_list);
                    wire(data);
                }


                // Log.d("wireadapter", String.valueOf(adapter.getItemCount()));

                //rvWire.setAdapter(new WiredAdapter(cont,data));





            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
