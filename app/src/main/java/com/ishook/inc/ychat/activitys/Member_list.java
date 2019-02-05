package com.ishook.inc.ychat.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.AsyncAdd;
import com.ishook.inc.ychat.adapters.DeleteRoom;
import com.ishook.inc.ychat.adapters.member_adapter;
import com.ishook.inc.ychat.list.member_list;
import com.bumptech.glide.Glide;

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

public class Member_list extends AppCompatActivity {

    String Group_name;
    String sessionid;
    String userid;
    String jabber_user;
    TextView no_member;
    RecyclerView rvMember;
    ImageView back;
    ImageView GroupIcon;
    TextView GroupTitle;
    LinearLayoutManager layoutManager;
    ImageView add;
    Context cont;
    private String[] strArrData;
    Button exit;
    String [] menu ={"Delete","Make Admin"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membe_list);
        no_member= (TextView) findViewById(R.id.no_member);
        rvMember= (RecyclerView) findViewById(R.id.rvroomMember);
        rvMember.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getApplicationContext());
        back= (ImageView) findViewById(R.id.memberlist_back);
        GroupTitle= (TextView) findViewById(R.id.group_name);
        GroupIcon= (ImageView) findViewById(R.id.group_icon);
        add=(ImageView) findViewById(R.id.addmember);
        exit= (Button) findViewById(R.id.exit_room);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new friendlist().execute(sessionid,userid);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle postdata = getIntent().getExtras();
        if (postdata == null) {
            return;
        }
        Group_name= getIntent().getStringExtra("group_name");

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        userid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        jabber_user = sharedPreferences.getString(Constants.KEY_jabber_user, "N/A");
        cont=getApplicationContext();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeleteRoom(cont).execute(userid,sessionid,Group_name,jabber_user)    ;       }
        });
        new AsyncChatmember().execute(userid,sessionid,Group_name,jabber_user);

    }

    private class AsyncChatmember extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;
        ProgressDialog progressDialog=new ProgressDialog(Member_list.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loding....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"chatmessage/chat/get_room_user_list_andr");
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
                        .appendQueryParameter("room_name", params[2])
                        .appendQueryParameter("jabber_user", params[3]);
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
            progressDialog.dismiss();
            String UserRole=null;

            List<member_list> data=new ArrayList<>();
            try {
               JSONObject object =new JSONObject(result);

                JSONArray array=object.getJSONArray("user_list");

                GroupTitle.setText(object.getString("room_name"));
                Glide.with(getApplicationContext()).load(Global.HostName+object.getString("room_icon_url"))
                        .into(GroupIcon);

                for (int i = 0; i < array.length() ; i++){

                    JSONObject jsonObject =array.getJSONObject(i);

                    member_list member_list=new member_list();
                    member_list.User_Role=object.getString("user_role");
                    member_list.room_user_id=jsonObject.getString("room_user_id");
                    member_list.Admin=object.getString("created_by");

                    member_list.room_name=jsonObject.getString("room_name");
                    member_list.role=jsonObject.getString("role");Log.d("member",member_list.role);
                    member_list.jabber_user=jsonObject.getString("jabber_user");Log.d("member",member_list.jabber_user);
                    member_list.jabber_id=jsonObject.getString("jabber_id");
                    member_list.SessionId=sessionid;
                    member_list.UserId=userid;


                    data.add(member_list);

                }
                rvMember.setAdapter(new member_adapter(getApplicationContext(),data));
                rvMember.setLayoutManager(new LinearLayoutManager(getApplicationContext()));




            } catch (JSONException e) {
                no_member.setVisibility(View.GONE);
                e.printStackTrace();
            }


        }
    }


    private class friendlist extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;
        public static final int CONNECTION_TIMEOUT = 10000;
        public static final int READ_TIMEOUT = 15000;



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
            final boolean[] checkedItems;
            final ArrayList<Integer> mUserItems = new ArrayList<>();
            try {

                JSONObject object=new JSONObject(s);
                ArrayList<String> dataList = new ArrayList<String>();



                JSONArray listOfriend=object.getJSONArray("loginUserfriendsList");
                for(int i=0;i<listOfriend.length();i++){
                    JSONObject json_data = listOfriend.getJSONObject(i);
                dataList.add(json_data.getString("JabberUser"));
                }

                strArrData = dataList.toArray(new String[dataList.size()]);
                checkedItems = new boolean[strArrData.length];


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Member_list.this);
                mBuilder.setTitle("Select Friends");
                mBuilder.setMultiChoiceItems(strArrData, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
//                        if (isChecked) {
//                            if (!mUserItems.contains(position)) {
//                                mUserItems.add(position);
//                            }
//                        } else if (mUserItems.contains(position)) {
//                            mUserItems.remove(position);
//                        }
                        if(isChecked){
                            mUserItems.add(position);
                        }else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserItems.size(); i++) {
                            item = item + strArrData[mUserItems.get(i)];
                            if (i != mUserItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        String Item=jabber_user+","+item;
                      //  Toast.makeText(getApplicationContext(),Item,Toast.LENGTH_LONG).show();
                        new AsyncAdd(cont,Group_name).execute(userid,sessionid,Group_name,Item);
                    }
                });

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Un Select All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                          //  mItemSelected.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
