package com.ishook.inc.ychat.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.goodiebag.pinview.Pinview;
import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Extrra.Session;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.app.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidatePin extends AppCompatActivity {
    Session session ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_pin);
        session = new Session(this);
        final Pinview pinview = findViewById(R.id.pinview);

        findViewById(R.id.materialButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinview.getValue().isEmpty()){
                    Toast.makeText(ValidatePin.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                }else {
                    verifieOTP(pinview.getValue());
                }
            }
        });
    }

    private void verifieOTP(final String value) {
        final ProgressDialog pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("\tLoading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.HostName + "users/login/otp_verify_json", new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    pdLoading.dismiss();
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("is_login")){
                        doLogin(Session.UserName,Session.Pass,Session.dID);
                        Toast.makeText(ValidatePin.this, jsonObject.getString("successMessage"), Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ValidatePin.this, jsonObject.getString("errorMessage"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pdLoading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserName", Session.UserName);
                params.put("otp", value);
                params.put("device_id", "4501006464537369104472106537363864153");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void doLogin(final String userName, final String password, final String deviceID) {
        final ProgressDialog pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("\tLogin...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global.HostName + "users/login/login_json", new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                pdLoading.dismiss();
                String loginstatus = null;
                String session_id = null;
                String Profile = null;
                String login_error_msg = null;
                String list_Post = null;
                String Stats = null;
                String medialist = null;
                String ProfilePic;




                if (result != "exception") {
                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.getBoolean("is_login")) {

                            loginstatus = jsonObject.getString("is_login");
                            session_id = jsonObject.getString("session_id");
                            Log.d("impssh", session_id);
                            Profile = jsonObject.getString("user_profile");
                            JSONObject object = new JSONObject(Profile);
                            ProfilePic = object.getString("ProfilePic");
                            login_error_msg = jsonObject.getString("login_error_msg");
                            Stats = jsonObject.getString("stats");
                            list_Post = jsonObject.getString("listOfPosts");
                            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();


                            editor.putString(Constants.KEY_SESSION, session_id);
                            editor.putString(Constants.KEY_POST, list_Post);
                            editor.putString(Constants.KEY_PROFILE, Profile);
                            editor.putString(Constants.KEY_STATS, Stats);
                            editor.putString(Constants.KEY_ProfilePic, ProfilePic);


                            editor.apply();


                            if (loginstatus.equals("true")) {

                                session.setLoggedin(true);
                                //Toast.makeText(getApplicationContext(), session_id, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                            } else {
                                Toast.makeText(getApplicationContext(), "Username and Password did not match", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            if (jsonObject.getInt("status") == 4) {
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Login Alert")
                                        .setMessage(jsonObject.getString("login_error_msg"))
                                        .setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(getApplicationContext(), ValidatePin.class));
                                                finish();
                                            }
                                        })
                                        .setNeutralButton("Cancel", null)
                                        .setCancelable(false)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                new AlertDialog.Builder(getApplicationContext())
                                        .setTitle("Login Alert")
                                        .setMessage(jsonObject.getString("login_error_msg"))
                                        .setPositiveButton("Ok", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Username and Password did not match", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your Connection!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pdLoading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("UserName", userName);
                params.put("Password", password);
                params.put("device_id", "4501006464537369104472106537363864153");
                return params;
            }
        };
        queue.add(stringRequest);

    }

}