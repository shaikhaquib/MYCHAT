package com.ishook.inc.ychat.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import com.ishook.inc.ychat.adapters.Comment_List_Adapter;
import com.ishook.inc.ychat.app.MainActivity;
import com.ishook.inc.ychat.list.Comment_List;

public class Comment extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private RecyclerView rvComment;
    List<Comment_List> data=new ArrayList<>();

    EditText etComment;
    ImageView sendcomment;

    RecyclerView.LayoutManager recyclerViewlayoutManager;
    Context cont;
    String sessionid;
    String userid;
    String actionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Bundle postdata = getIntent().getExtras();
        if (postdata == null) {
            return;
        }

        sessionid= getIntent().getStringExtra("sessionId");
        userid=getIntent().getStringExtra("UserId");
        actionid=getIntent().getStringExtra("action_id");

        etComment= (EditText) findViewById(R.id.commenttext);
        sendcomment= (ImageView) findViewById(R.id.postcomment);

        etComment.setFocusable(false);
        etComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                etComment.setFocusableInTouchMode(true);

                return false;
            }
        });

        sendcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checComment();
            }

        });

        rvComment = (RecyclerView) findViewById(R.id.CommentList);
        rvComment.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getApplicationContext());



        new AsyncComment().execute(sessionid,userid,actionid);



    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void checComment() {
        etComment.setError(null);
        boolean cancel= false;
        View focusView = null;

        String txtCmt=etComment.getText().toString();
        if (TextUtils.isEmpty(txtCmt)) {
            Toast.makeText(getApplicationContext(),"You can't send empty Comment!",Toast.LENGTH_SHORT).show();
            focusView=etComment;
            cancel=true; }

            if (cancel) {
                focusView.requestFocus();
            }
            else {
                new AsyncComment().execute( sessionid,userid,actionid );
                new AsyncPostComment().execute( sessionid,userid,actionid,txtCmt );
            }

    }

    private class AsyncComment  extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"activity/index/get_comments_json");
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
                        .appendQueryParameter("action_id", params[2]);
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

            List<Comment_List> data=new ArrayList<>();

            try {

                JSONObject jsonObject=new JSONObject(result);
                String Commentss=jsonObject.getString("Comments");
                JSONObject JsonComment=new JSONObject(Commentss);



                JSONArray commentlist=JsonComment.getJSONArray("comments");

                for(int i=0;i<commentlist.length();i++){

                    JSONObject json_data = commentlist.getJSONObject(i);
                    String Comment=json_data.getString("comment");
                    Log.d("Comment",Comment);
                    JSONObject object=new JSONObject(Comment);
                    Comment_List comment_list =new Comment_List();

                    comment_list.sessionid=sessionid;
                    Log.d("session",comment_list.sessionid);

                    comment_list.userid=userid;
                    Log.d("uid",comment_list.userid);
                    comment_list.commentid=object.getString("CommentId");
                    Log.d("commentid",comment_list.commentid);

                    comment_list.comment_selfLikes=json_data.getString("selfLikes");
                    Log.d("CommentSelfLike",comment_list.comment_selfLikes);
                    comment_list.countcommentLikes=json_data.getString("countcommentLikes");
                    Log.d("countcommentLikes",comment_list.countcommentLikes);


                    comment_list.profile=object.getString("userProfilePic");
                    comment_list.comment_subject_name=object.getString("FriendUserName");
                    comment_list.comment_object=object.getString("FriendFirstName");
                    comment_list.comment_time=object.getString("timeAgo");
                    comment_list.comment_text=object.getString("CommentBody");


                    data.add(comment_list);

                }

                rvComment.setAdapter(new Comment_List_Adapter(getApplicationContext(),data));
                rvComment.setLayoutManager(new LinearLayoutManager(getApplicationContext()));




            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class AsyncPostComment extends AsyncTask<String, String, String>{
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"activity/index/comment_post_json");
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
                        .appendQueryParameter("actionId", params[2])
                        .appendQueryParameter("commentText", params[3]);
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
        protected void onPostExecute(String  result) {

            if (result=="exception"){
                Toast.makeText(getApplicationContext(),"Check Your Internet connection",Toast.LENGTH_SHORT).show();
            }else{
            etComment.setText("");
            new AsyncComment().execute( sessionid,userid,actionid );
                Toast.makeText(getApplicationContext(),"Posting...", Toast.LENGTH_LONG).show();}
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);



        }
    }
}
