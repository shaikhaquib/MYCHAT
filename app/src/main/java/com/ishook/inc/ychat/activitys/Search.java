package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.adapters.SearchAdapt;
import com.ishook.inc.ychat.list.SearchList;

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
import java.util.Timer;
import java.util.TimerTask;

public class  Search extends AppCompatActivity {

    ImageView Back,clear;
    EditText query;
    String sessionid,uid;
    RecyclerView rvSearch;
    Context context;

    private Timer timer;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(final Editable arg0) {
            // user typed: start the timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your actual work here
                    Search.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String searchquery=arg0.toString();

                            new AsyncSearch().execute(sessionid,uid,searchquery);
                            System.out.println(arg0);

                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // hide keyboard as well?
                    // InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // in.hideSoftInputFromWindow(searchText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 600); // 600ms delay before the timer executes the "run" method from TimerTask
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // nothing to do here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // user is typing: reset already started timer (if existing)

            if(s.toString().trim().length()==0){
                clear.setVisibility(View.GONE);
            } else {
                clear.setVisibility(View.VISIBLE);
            }

            if (timer != null) {
                timer.cancel();
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_search);

        initialize();

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
         sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
         uid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.setText("");
            }
        });
        query.addTextChangedListener(searchTextWatcher );


    }

    private void initialize() {
        Back= (ImageView) findViewById(R.id.SearchBack);
        clear= (ImageView) findViewById(R.id.SearchClear);
        query= (EditText) findViewById(R.id.SearchQuery);
        rvSearch= (RecyclerView) findViewById(R.id.rvsearch);
        context=getApplicationContext();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public class AsyncSearch extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(Global.HostName+"users/friends/find_friend_andr");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        // .appendQueryParameter("Email", params[0])
                        .appendQueryParameter("sessionId", params[0])
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("searchKeyword", params[2])
                        ;
                       /* .appendQueryParameter("privacy", params[3])*/
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
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

            List<SearchList> data = new ArrayList<>();

            try {
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray("friends");

                for (int i = 0 ; i < jsonArray.length() ; i++){

                    JSONObject object=jsonArray.getJSONObject(i);
                    SearchList list=new SearchList();

                    list.id=object.getString("userId");
                    list.dp=object.getString("ProfilePic");
                    list.name=object.getString("FullName");



                    data.add(list);
                }

                rvSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rvSearch.setAdapter(new SearchAdapt(context,data));




            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
}
