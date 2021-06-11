package com.ishook.inc.ychat.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Setting;
import com.ishook.inc.ychat.app.MainActivity;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

import static android.app.Activity.RESULT_OK;

public class Update_ProfilePic extends Fragment {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    EditText editfname;
    EditText editlname;
    EditText edituname;
    EditText editemail;
    Spinner timezone;
    Spinner country;

    ImageView open_gallery;

    Button save_setting;

    TextView tv_time;
    TextView tv_country;



    String firstname=null;
    String lastname=null;
    String UserName=null;
    String Email=null;
    String TimeZone=null;
    String Country=null;




    ImageView Profile;
    String userid=null;
    String sessionid=null;
    String imgPath, fileName;
    Bitmap bm;

    private static int RESULT_LOAD_IMG = 1;
    String encodedString;
    String imgextention;
    Setting setting;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_update__profile_pic,container,false);

        Profile= (ImageView)rootView. findViewById(R.id.profile_pic);
        editfname= (EditText)rootView. findViewById(R.id.edfname);
        editlname= (EditText)rootView. findViewById(R.id.edlname);
        edituname= (EditText)rootView. findViewById(R.id.eduname);
        editemail= (EditText)rootView. findViewById(R.id.edemail);
        country= (Spinner)rootView. findViewById(R.id.country);
        timezone= (Spinner) rootView.findViewById(R.id.time_zone);
        open_gallery= (ImageView) rootView.findViewById(R.id.open_gallery);

        tv_time= (TextView)rootView. findViewById(R.id.tv_time);
        tv_country= (TextView)rootView. findViewById(R.id.tv_country);

        save_setting= (Button) rootView.findViewById(R.id.submit_setting);

        open_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadpicturefromGallery(v);

            }
        });

        setting =new Setting();

        //Setting Auto Focus off

        editfname.setFocusable(false);
        editlname.setFocusable(false);
        edituname.setFocusable(false);
        editemail.setFocusable(false);

        editfname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                editfname.setFocusableInTouchMode(true);
                return false;
            }
        });
        editlname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                editlname.setFocusableInTouchMode(true);
                return false;
            }
        });
        edituname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                edituname.setFocusableInTouchMode(true);
                return false;
            }
        });
        editemail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                editemail.setFocusableInTouchMode(true);
                return false;
            }
        });

        //binding String array to adapter

        ArrayAdapter<String> TimeZoneAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.time_Zone));
        ArrayAdapter<String>  CountryAdapter= new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.country));

        //binding adapter to Spinner

        timezone.setAdapter(TimeZoneAdapter);
        timezone.setPrompt("Select TimeZone");
        country.setAdapter(CountryAdapter);
        country.setPrompt("Select Country");
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time.setVisibility(View.GONE);
                timezone.setVisibility(View.VISIBLE);
                timezone.performClick();
            }
        });

        tv_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_country.setVisibility(View.GONE);
                country.setVisibility(View.VISIBLE);
                country.performClick();
            }
        });
// Set initial selection
        timezone.setSelection(0);

// Post to avoid initial invocation
        timezone.post(new Runnable() {
            @Override public void run() {

                //on item selected listener
                timezone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position <1) {
                            timezone.setVisibility(View.GONE);
                            tv_time.setVisibility(View.VISIBLE);
                        }
                        Log.d("position", String.valueOf(position));
                        TextView txttime;
                        txttime=(TextView)view;
                        TimeZone=txttime.getText().toString();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        // Set initial selection
        country.setSelection(0);

// Post to avoid initial invocation
        country.post(new Runnable() {
            @Override public void run() {
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position <1) {
                    country.setVisibility(View.GONE);
                    tv_country.setVisibility(View.VISIBLE);
                }
                TextView coutrytxt;
                coutrytxt=(TextView)view;
                Country=coutrytxt.getText().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
            }
        });


        //Fetching value from shared pref

        try {
            SharedPreferences preferences = getActivity().getSharedPreferences( getContext().getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            String profiledetail = preferences.getString(Constants.KEY_ProfileDetail, "N/A");
            JSONObject jsonObject=new JSONObject(profiledetail);

            firstname=jsonObject.getString("FirstName");
            lastname=jsonObject.getString("LastName");
            UserName=jsonObject.getString("UserName");
            Email=jsonObject.getString("Email");
            TimeZone=jsonObject.getString("TimeZone");
            Country=jsonObject.getString("Country");


            sessionid = preferences.getString(Constants.KEY_SESSION, "N/A");
            String profile = preferences.getString(Constants.KEY_PROFILE, "N/A");

            String ProfilePic=jsonObject.getString("ProfilePic");
            getActivity().setTitle(jsonObject.getString("UserName"));

            JSONObject object =new JSONObject(profile);
            userid=object.getString("user_id");

//Checking Strings are empty or not if not then set text to EditText .

            if(!lastname.isEmpty()){
            editlname.setText(lastname);}

            if(!firstname.isEmpty()){
            editfname.setText(firstname);}

            if (!UserName.isEmpty()){
            edituname.setText(UserName);}

            if (!Email.isEmpty()){
            editemail.setText(Email);}

            if (!TimeZone.isEmpty()){
            tv_time.setText(TimeZone);}

            if (!Country.isEmpty()){
            tv_country.setText(Country);}


            Glide.with(getContext()).load(Global.HostName+ProfilePic).into(Profile);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        save_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkfield();

            }
        });
        return rootView;
    }

    private void checkfield() {
        editfname.setError(null);
        editlname.setError(null);
        edituname.setError(null);
        editemail.setError(null);

        String fname=editfname.getText().toString();
        String lname=editlname.getText().toString();
        String uname=edituname.getText().toString();
        String email=editemail.getText().toString();
        String timezone=tv_time.getText().toString();
        String country= tv_country.getText().toString();

        Pattern pattern1=Pattern.compile("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher1=pattern1.matcher(email);

        boolean cancel= false;
        View focusView = null;

        if (TextUtils.isEmpty(fname)) {
            editfname.setError(getString(R.string.error_field_required));
            focusView = editfname;
            cancel = true;
        }else if (TextUtils.isEmpty(lname)) {
            editlname.setError(getString(R.string.error_field_required));
            focusView = editlname;
            cancel = true;
        }else if (TextUtils.isEmpty(uname)) {
            edituname.setError(getString(R.string.error_field_required));
            focusView = edituname;
            cancel = true;
        }else if (TextUtils.isEmpty(email)) {
            editemail.setError(getString(R.string.error_field_required));
            focusView = editemail;
            cancel = true;
        }  else if (!matcher1.matches()) {
            Toast.makeText(getContext(),"Enter valid Email",Toast.LENGTH_SHORT).show();
            focusView = editemail;
            cancel = true;
        }

        new AsyncSaveSetting().execute(userid, sessionid,uname,email,fname,lname,TimeZone,Country);

    }

    public void loadpicturefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(getView() == null){
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    startActivity(new Intent(getActivity(),MainActivity.class));
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                // Move to first row

                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgPath = cursor.getString(columnIndex);

                // Set the Image in ImageView
                Log.d("imagepath", imgPath);
                imgextention = imgPath.substring(imgPath.lastIndexOf(".") + 1);
                Log.d("imgextention", imgextention);
                 bm = BitmapFactory.decodeFile(imgPath);
                encodedString = getEncoded64ImageStringFromBitmap(bm);
                Log.d("encodedString", encodedString);


                // Get the Image's file name

                String fileNameSegments[] = imgPath.split("/");

                fileName = fileNameSegments[fileNameSegments.length - 1];
                Log.d("filename", fileName);

                // Put file name in Async Http Post Param which will used in Php web app
                // Convert image to String using Base64
                new AsyncProfile().execute(userid, sessionid, encodedString, imgextention);



            } else {


            }

        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();

        }

    }

    private String getEncoded64ImageStringFromBitmap(Bitmap camimage) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        camimage.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString;

    }

    public static android.app.Fragment newInstance() {
        return null;
    }




/*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);    }*/

    public class AsyncProfile extends AsyncTask<String,String,String> {

        HttpURLConnection conn;
        URL url = null;
    ProgressDialog pg=new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pg.setMessage("\tLoading...");
            pg.setCancelable(false);
            pg.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(Global.HostName+"home/profile/profile_pic_image_save_json");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
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
                        .appendQueryParameter("profile_pic_ori", params[2])
                        .appendQueryParameter("img_extension", params[3]);
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
                Log.d("No Internet", String.valueOf(e));
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
        protected void onPostExecute(String s) {
            pg.dismiss();
            String errormsg=null;
            String succsmsg=null;

            try {

                JSONObject jsonObject=new JSONObject(s);
                errormsg=jsonObject.getString("error_msg");
                succsmsg=jsonObject.getString("success_msg");
                if (errormsg.isEmpty()) {
                    Profile.setImageBitmap(bm);
                    Toast.makeText(getContext(),succsmsg,Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getContext(),"error occured while updating Coverpic!",Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class AsyncSaveSetting extends AsyncTask<String, String, String>{

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
                url = new URL(Global.HostName+"settings/save_account_settings_andr");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
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
                        .appendQueryParameter("UserName", params[2])
                        .appendQueryParameter("Email", params[3])
                        .appendQueryParameter("FirstName", params[4])
                        .appendQueryParameter("LastName", params[5])
                        .appendQueryParameter("TimeZone", params[6])
                        .appendQueryParameter("Country", params[7]);

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pr.dismiss();
            try {
                JSONObject object=new JSONObject(s);

                if (object.getString("error_msg").equals("")){

                    Toast.makeText(getActivity(),object.getString("success"),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i=new Intent(getActivity(), MainActivity.class);
                    Toast.makeText(getActivity(),object.getString("error_msg"),Toast.LENGTH_LONG).show();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(i);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}

