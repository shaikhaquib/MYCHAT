package com.ishook.inc.ychat.activitys;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText  etName, etEmail, etPass, etRPasss;
     ScrollView scrollView;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    LinearLayout loginscr;
    View focusView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);


        loginscr= (LinearLayout) findViewById(R.id.loginscr);

        loginscr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });


        etName = (EditText) findViewById(R.id.etname);
        etPass = (EditText) findViewById(R.id.etPassword);
        etRPasss = (EditText) findViewById(R.id.etCPassword);

        etEmail = (EditText) findViewById(R.id.etemail);
        Button reg = (Button) findViewById(R.id.btnreg);


        etName.setFocusable(false);
        etPass.setFocusable(false);
        etRPasss.setFocusable(false);
        etEmail.setFocusable(false);


        etName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                etName.setFocusableInTouchMode(true);
                return false;
            }
        });
        etPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                etPass.setFocusableInTouchMode(true);
                return false;
            }
        });
        etRPasss.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                etRPasss.setFocusableInTouchMode(true);
                return false;
            }
        });
        etEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                etEmail.setFocusableInTouchMode(true);
                return false;
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkfield();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    private void checkfield(){

        etName.setError(null);
        etPass.setError(null);
        etRPasss.setError(null);

        etEmail.setError(null);


        String uname = etName.getText().toString();
        String pass = etPass.getText().toString();
        String rpass = etRPasss.getText().toString();
        String email = etEmail.getText().toString();

        Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher1=pattern1.matcher(email);

        boolean cancel= false;
        View focusView = null;


        if (TextUtils.isEmpty(uname)) {
            etName.setError(getString(R.string.error_field_required));
            focusView = etName;
            cancel = true;
        }
        else if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        }
        else if (!matcher1.matches()) {
            Toast.makeText(getApplicationContext(),"Enter valid Email",Toast.LENGTH_SHORT).show();
            focusView = etEmail;
            cancel = true;
        }
        else if (TextUtils.isEmpty(pass)) {
            etPass.setError(getString(R.string.error_field_required));
            focusView = etPass;
            cancel = true;
        }
        else if (TextUtils.isEmpty(rpass)) {
            etRPasss.setError(getString(R.string.error_field_required));
            focusView = etRPasss;
            cancel = true;
        }


       else if(!TextUtils.equals(pass,rpass))
        {
            Toast.makeText(getApplicationContext(),"Password not match!",Toast.LENGTH_SHORT).show();
            focusView = etRPasss;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
        else {
            new UserAddTask().execute( email,uname, pass ,"android");
        }
    }

public class UserAddTask extends AsyncTask<String, String, String> {


        ProgressDialog pr = new ProgressDialog(Register.this);
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
                url = new URL(Global.HostName+"registeration/register_json");

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
                        .appendQueryParameter("Email", params[0])
                        .appendQueryParameter("UserName", params[1])
                        .appendQueryParameter("Password", params[2])
                        .appendQueryParameter("reg_device", params[3]);
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
        protected void onPostExecute(String result) {


            String abc =null;
            String errorMessage=null;
            String msg;
            pr.dismiss();





            try {
                JSONObject jsonObject = new JSONObject(result);
                abc=jsonObject.getString("reg_success");
                errorMessage=jsonObject.getString("errorMessage");
                msg=jsonObject.getString("msg");
                if(abc.equals("true")){


                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                }else {
                    Toast.makeText(getApplicationContext(), errorMessage ,Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),errorMessage ,Toast.LENGTH_SHORT).show();
                e.printStackTrace();}

        }
        @Override
        protected void onCancelled() {

        }
    }
}
