package com.ishook.inc.ychat.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

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
import com.ishook.inc.ychat.adapters.FreindRequest_Adapter;
import com.ishook.inc.ychat.list.FRequest_list;

public class FriendRequests extends AppCompatActivity {

    RecyclerView RvFrlist;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    String sessionid;
    String uid;
    LinearLayout no_request;
//    ProgressDialog pg =new ProgressDialog(FriendRequests.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        RvFrlist = (RecyclerView) findViewById(R.id.rvfrlist);
        RvFrlist.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());
        no_request= (LinearLayout) findViewById(R.id.no_request);

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
         sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
         uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        /*String[] s = sharedPreferences.(Constants.KEY_user_friend, "N/A");
        Toast.makeText(getApplicationContext(), Arrays.toString(s),Toast.LENGTH_LONG).show();*/
        new frienRequestdlist().execute(sessionid,uid);

    }

    private class frienRequestdlist extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        ProgressDialog pg=new ProgressDialog(FriendRequests.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg.setMessage("\tLoading...");
            pg.setCancelable(false);
            pg.show();
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
                pg.dismiss();

                List<FRequest_list> data=new ArrayList<>();



                try {

                    JSONObject object=new JSONObject(s);

                    JSONArray listOfriend=object.getJSONArray("friendRequestList");



                    int s1=listOfriend.length();
                    System.out.println("onPostExecute"+s1);

                    if (s1==0){   no_request.setVisibility(View.VISIBLE);}

                    for(int i=0;i<listOfriend.length();i++){
                        JSONObject json_data = listOfriend.getJSONObject(i);

                        FRequest_list friend_list =new FRequest_list();

                        friend_list.Sessionid=(sessionid);
                        friend_list.Uid=(uid);

                        friend_list.frUid=json_data.getString("UserId");
                        Log.d("FriendName",friend_list.frUid);
                        friend_list.frUname=json_data.getString("UserName");
                        Log.d("FriendName",friend_list.frUname);
                        friend_list.frProfilepic=json_data.getString("ProfilePic");
                        Log.d("FriendName",friend_list.frProfilepic);
                        friend_list.frEmail=json_data.getString("Email");
                        Log.d("FriendName",friend_list.frEmail);


                        data.add(friend_list);




                    }

                    RvFrlist.setAdapter(new FreindRequest_Adapter(getApplicationContext(),data));
                    RvFrlist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }



    }
}
