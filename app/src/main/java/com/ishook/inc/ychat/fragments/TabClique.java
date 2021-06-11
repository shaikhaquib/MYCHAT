package com.ishook.inc.ychat.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.CreateGroup;
import com.ishook.inc.ychat.adapters.Clique_Adapter;
import com.ishook.inc.ychat.list.Cliquelist;

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

/**
 * Created by Shaikh on 05-01-2018.
 */

public class TabClique extends Fragment {

    RecyclerView rvClique;
    GridLayoutManager gridLayoutManager;
    Context cont;
    ProgressBar progress;
    SwipeRefreshLayout swipeRefreshLayout;


    String Uid;
    String Sid;
    String Juser;
    LinearLayout no_Clique;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.tab_clique, container, false);
        cont=getActivity();
        rvClique= (RecyclerView) rootView.findViewById(R.id.rv_clique);
      //  progress= (ProgressBar) rootView.findViewById(R.id.progress);
        rvClique.setLayoutManager(new GridLayoutManager(cont,2));
        no_Clique= (LinearLayout) rootView.findViewById(R.id.no_Clique);
      //  swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swClique);
        // gridLayoutManager=new GridLayoutManager(cont,2);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences( getContext().getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        Sid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        Uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        Juser = sharedPreferences.getString(Constants.KEY_jabber_user, "N/A");


      /*  swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Load_Clique().execute(Uid,Sid,Juser);
            }
        });*/

        new Load_Clique().execute(Uid,Sid,Juser);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CreateGroup:
                startActivity(new Intent(cont, CreateGroup.class));

                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private class Load_Clique extends AsyncTask<String,String,String>{

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // progress.setVisibility(View.VISIBLE);
/*
            swipeRefreshLayout.setRefreshing(true);
*/
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"chatmessage/chat/get_room_list_of_user_andr");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("UserId", params[0])
                        .appendQueryParameter("sessionId", params[1])
                        .appendQueryParameter("jabber_user", params[2]);

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
        //    swipeRefreshLayout.setRefreshing(false);
       // progress.setVisibility(View.GONE);

            List<Cliquelist> data=new ArrayList<>();



            try {
                JSONObject object =new JSONObject(s);
                JSONArray Clique_Array=object.getJSONArray("room_list");
                int s1=Clique_Array.length();
                System.out.println("onPostExecute"+s1);

                if (s1==0){   no_Clique.setVisibility(View.VISIBLE);}
                    for(int i=0;i<Clique_Array.length();i++){

                    JSONObject jsonObject=Clique_Array.getJSONObject(i);
                    Cliquelist cliquelist=new Cliquelist();

                    cliquelist.sessionId=Sid;
                    cliquelist.UserId=Uid;
                    cliquelist.jabber_user=Juser;
                    no_Clique.setVisibility(View.GONE);
                    cliquelist.jabber_id=jsonObject.getString("jabber_id");
                    cliquelist.room_alias_name=jsonObject.getString("room_alias_name");
                    cliquelist.room_name=jsonObject.getString("room_name");
                    cliquelist.room_user_id=jsonObject.getString("room_user_id");
                    cliquelist.room_icon_url=Global.HostName+jsonObject.getString("room_icon_url");



                    data.add(cliquelist);
                }
//                rvClique.setLayoutManager(gridLayoutManager);
                rvClique.setAdapter(new Clique_Adapter(cont,data));

            } catch (JSONException e) {
                no_Clique.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }


        }
    }
}
