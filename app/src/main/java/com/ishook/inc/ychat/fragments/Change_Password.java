package com.ishook.inc.ychat.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;

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

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.app.MainActivity;

/**
 * Created by Shaikh on 26-Oct-17.
 */

public class Change_Password extends Fragment {

    EditText new_pass;
    EditText old_pass;
    EditText con_pass;
    Button chan_pass;
    String sessionid=null;
    String userid=null;

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_password, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences( getContext().getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        String profile = sharedPreferences.getString(Constants.KEY_PROFILE, "N/A");
        String profiled = sharedPreferences.getString(Constants.KEY_ProfileDetail, "N/A");
        Log.d("profiledetail",profile);
        try {
            JSONObject object =new JSONObject(profile);
            userid=object.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        old_pass= (EditText) rootView.findViewById(R.id.oldpassword);
        new_pass= (EditText) rootView.findViewById(R.id.newpassword);
        con_pass= (EditText) rootView.findViewById(R.id.conewpassword);
        chan_pass= (Button) rootView.findViewById(R.id.ch_pass);

        old_pass.setFocusable(false);
        new_pass.setFocusable(false);
        con_pass.setFocusable(false);


        old_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                old_pass.setFocusableInTouchMode(true);
                return false;
            }
        });
        new_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                new_pass.setFocusableInTouchMode(true);
                return false;
            }
        });
        con_pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                con_pass.setFocusableInTouchMode(true);
                return false;
            }
        });

        chan_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkfield();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });


        return rootView;
    }

    private void checkfield() {

        boolean cancel= false;
        View focusView = null;

        old_pass.setError(null);
        new_pass.setError(null);
        con_pass.setError(null);

        String Oldp = old_pass.getText().toString().trim();
        String NewP = new_pass.getText().toString().trim();
        String Conp = con_pass.getText().toString().trim();

        if (TextUtils.isEmpty(Oldp)) {
            old_pass.setError(getString(R.string.error_field_required));
            focusView = old_pass;
            cancel = true;
        } else  if (TextUtils.isEmpty(NewP)) {
            new_pass.setError(getString(R.string.error_field_required));
            focusView = new_pass;
            cancel = true;
        } else  if (TextUtils.isEmpty(Conp)) {
            con_pass.setError(getString(R.string.error_field_required));
            focusView = con_pass;
            cancel = true;
        } else {
            new AsyncChangePass().execute(userid,sessionid,Oldp,NewP,Conp);
        }
    }

    private class AsyncChangePass  extends AsyncTask<String, String, String> {


        ProgressDialog pr = new ProgressDialog(getActivity());
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pr.setMessage("\tLoading...");
            pr.setCancelable(false);
            pr.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(Global.HostName+"settings/changePassword_andr");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("UserId", params[0])
                        .appendQueryParameter("sessionId", params[1])
                        .appendQueryParameter("old_password", params[2])
                        .appendQueryParameter("Password", params[3])
                        .appendQueryParameter("confirm_password", params[4]);
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
pr.dismiss();
            try {
                JSONObject object=new JSONObject(s);

                if (object.getString("error_flag").equals("true")){

                    Toast.makeText(getActivity(),object.getString("error_msg"),Toast.LENGTH_LONG).show();
                }
                if (object.getString("error_flag").equals("false")){
                    Intent i=new Intent(getActivity(), MainActivity.class);
                    Toast.makeText(getActivity(),object.getString("success"),Toast.LENGTH_LONG).show();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(i);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
