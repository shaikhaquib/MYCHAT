package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.FriendAdapt;
import com.ishook.inc.ychat.adapters.YchatTabHolder;
import com.ishook.inc.ychat.list.FreindData;

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

public class FriendsList extends AppCompatActivity {


    String profile=null;
    String sessionid=null;
    String userid=null;
    String ProfileUserId=null;
    String friendKeyword="";
    SwipeRefreshLayout mSwipeRefreshLayout;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    RecyclerView rvytab;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    Context cont;
    SwipeRefreshLayout swipeRefreshLayout;
    YchatTabHolder ychatTabHolder;
    LinearLayout no_friend;
    private String[] strArrData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);


        SharedPreferences sharedPreferences = getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        String profile = sharedPreferences.getString(Constants.KEY_PROFILE, "N/A");
        try {
            JSONObject object =new JSONObject(profile);
            userid=object.getString("user_id");
        } catch (JSONException e) {e.printStackTrace();}

        /*  RecyclerView Code  */
        setTitle("Friends");

        rvytab = (RecyclerView)findViewById(R.id.rvfriend);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swfriend);
        rvytab.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());
        cont = getApplicationContext();
        ProfileUserId="";
        no_friend= (LinearLayout)findViewById(R.id.no_friend_error);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new friendlist().execute(sessionid,userid);
            }
        });


        new friendlist().execute(sessionid,userid);

    }

    private class friendlist extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;


        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"users/friends/friends_list_andr");
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
                        Log.d("result", result.toString());
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
            swipeRefreshLayout.setRefreshing(false);

            List<FreindData> data=new ArrayList<>();
            String rvcount;
            rvcount=rvytab.toString();
            ArrayList<String> dataList = new ArrayList<String>();
            String search  = "loginUserfriendsList";

            if (s.toLowerCase().contains(search.toLowerCase())) {

                System.out.println("I found the keyword");

            } else {

                System.out.println("not found");
                no_friend.setVisibility(View.VISIBLE);
            }

            try {

                JSONObject object=new JSONObject(s);

                JSONArray listOfriend=object.getJSONArray("loginUserfriendsList");

                Log.d("list", String.valueOf(listOfriend));

                for(int i=0;i<listOfriend.length();i++){
                    JSONObject json_data = listOfriend.getJSONObject(i);

                    FreindData friend_list =new FreindData();
                    friend_list.Sessionid=(sessionid);
                    friend_list.uid=(userid);



                    friend_list.UserId=json_data.getString("userId");
                    Log.d("FriendName",friend_list.UserId);
                    friend_list.UserName=json_data.getString("FullName");
                    Log.d("FriendName",friend_list.UserName);
                    friend_list.ProfilePic=json_data.getString("ProfilePic");
                    Log.d("FriendName",friend_list.ProfilePic);
                    friend_list.Email=json_data.getString("Email");
                    Log.d("FriendName",friend_list.Email);

                    no_friend.setVisibility(View.GONE);

                    dataList.add(json_data.getString("FullName"));

                    strArrData = dataList.toArray(new String[dataList.size()]);





                    data.add(friend_list);




                }

                rvytab.setAdapter(new FriendAdapt(cont,data));
                rvytab.setLayoutManager(recyclerViewlayoutManager);

            } catch (JSONException e) {
                Log.e("erre", String.valueOf(e));

                e.printStackTrace();
            }


        }



    }

}
