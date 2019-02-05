package com.ishook.inc.ychat.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Extrra.Session;
import com.ishook.inc.ychat.app.MainActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static String MY_PREFS = "MY_PREFS";
    private SharedPreferences mySharedPreferences;
    int prefMode = Activity.MODE_PRIVATE;
    EditText Username, password;
    LinearLayout newser;
    Button login;
    boolean doubleBackToExitPressedOnce = false;
    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    public static final int CONNECTION_TIMEOUT=10000;
public static final int READ_TIMEOUT=15000;
    Session session;
    TextView fpass;



@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
         getSupportActionBar().hide();
   LoginActivity.this.getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);
        newser= (LinearLayout) findViewById(R.id.newuser);
        newser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });

        Username = (EditText) findViewById(R.id.email);
        password= (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.btnlogin);
        fpass= (TextView) findViewById(R.id.forpass);

    Username.setFocusable(false);
    password.setFocusable(false);

    Username.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Username.setFocusableInTouchMode(true);
            return false;

        }
    });
    password.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            password.setFocusableInTouchMode(true);
            return false;

        }
    });

    fpass.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
        startActivity(new Intent(LoginActivity.this,ForgotPass.class));
        }
    });



             session = new Session(this);
             if(session.loggedin()){
                  startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }


        login.setOnClickListener(new View.OnClickListener()
        {
@Override
public void onClick(View v) {
    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    String UserName,Password;
        UserName= Username.getText().toString();
        Password = password.getText().toString();
         boolean cancel= false;
         View focusView = null;
        //Toast.makeText(getApplicationContext(), UserName +" & "+ Password, Toast.LENGTH_LONG).show();

             if (TextUtils.isEmpty(UserName)) {
             Toast.makeText(getApplicationContext(),"Username and Password is required",Toast.LENGTH_SHORT).show();
              focusView = Username;
              cancel = true;
                  }
             else if (TextUtils.isEmpty(Password)) {
              password.setError(getString(R.string.error_field_required));
                 Toast.makeText(getApplicationContext(),"Username and Password is required",Toast.LENGTH_SHORT).show();
              focusView = password;
               cancel = true;
            }else {
        new UserLoginTask().execute(UserName, Password);}
        }
        });

                        if (Build.VERSION.SDK_INT < 23) {
                            //Do not need to check the permission
                        } else {
                            if (checkAndRequestPermissions()) {
                                //If you have already permitted the permission
                            }
                        }


        }
    private void autoLaunchActivity(Context context) {


        if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
            try {

                Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.setClassName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity");
                startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setClassName("com.oppo.safe",
                            "com.oppo.safe.permission.startup.StartupAppListActivity");
                    startActivity(intent);

                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.coloros.safecenter",
                                "com.coloros.safecenter.startupapp.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception exx) {

                    }
                }
            }

        } else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")){

            Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();

            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.iqoo.secure",
                        "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                context.startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                            "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                    context.startActivity(intent);
                } catch (Exception ex) {
                    try {
                        Intent intent = new Intent();
                        intent.setClassName("com.iqoo.secure",
                                "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                        context.startActivity(intent);
                    } catch (Exception exx) {
                        ex.printStackTrace();
                    }
                }
            }


        }else if(Build.BRAND.equalsIgnoreCase("xiaomi") ){
            Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);


        }else if(Build.BRAND.equalsIgnoreCase("Letv")){
            Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            startActivity(intent);

        }
        else if(Build.BRAND.equalsIgnoreCase("Honor")){
            Toast.makeText(context, "For Better experiance in Background Please Enable yChat app Auto Start mode !", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            startActivity(intent);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        int permissioncotact = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        int permissionwritestorsge = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        int storagePermission = ContextCompat.checkSelfPermission(this,


                Manifest.permission.READ_EXTERNAL_STORAGE);



        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionwritestorsge != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissioncotact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        } if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }



        return true;
    }

    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    autoLaunchActivity(this);
                } else {
                    //You did not accept the request can not use the functionality.
                    autoLaunchActivity(this);

                }
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        }

    public class UserLoginTask extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }


        @Override
    protected String doInBackground(String... params) {
        try {

            // Enter URL address where your php file resides
            url = new URL(Global.HostName+"users/login/login_json");

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
                    .appendQueryParameter("UserName", params[0])
                    .appendQueryParameter("Password", params[1]);
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
                loginstatus = jsonObject.getString("is_login");
                session_id = jsonObject.getString("session_id");
                Log.d("impssh",session_id);
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
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Username and Password did not match", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }else {                Toast.makeText(getApplicationContext(), "Check your Connection!", Toast.LENGTH_SHORT).show();
        }
    }
        @Override
        protected void onCancelled() {

        }
    }
}
