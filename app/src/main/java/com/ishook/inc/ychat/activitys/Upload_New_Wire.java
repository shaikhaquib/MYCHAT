package com.ishook.inc.ychat.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.app.MainActivity;
import com.bumptech.glide.Glide;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Upload_New_Wire extends AppCompatActivity {

    ImageView Profile;
    TextView Username;
    EditText etWireText;
    ImageView gallery;
    ImageView Cam;
    ImageView wireImage;
    Button post;
    Bitmap camimage;
 //   String encodedString=null;

    private String selectedImagePath;




    public static final int CAMERA_REQUEST=123;
    private static final int SELECT_PICTURE = 1;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;



    String sessionid;
    String uid;
    String privacy = null;

    private Button submitButton, postButton, randomButton;

    private Context context;

    private ViewGroup dropPreview, dropPost;

    private ProgressBar previewAreaTitle ;
    String str;

    private static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;


    private String currentTitle, currentUrl, currentCannonicalUrl,
            currentDescription;

        private Bitmap[] currentImageSet;
        private Bitmap currentImage;
        private int currentItem = 0;
        private int countBigImages = 0;
        private boolean noThumb;
        String Htmlcode;
        private static int RESULT_LOAD_IMG = 1;
        String encodedString;
        String imgextention;
        String imgPath, fileName;
        Bitmap bm;

    private Timer timer;

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
            // user typed: start the timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // do your actual work here
                    Upload_New_Wire.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (etWireText.getText().toString().contains("https://")){
                                System.out.println("Contains");
                                System.out.println("Console"+Htmlcode);
                            }
                            else{
                                System.out.println("Not Contains url");
                                Htmlcode="";
                            }
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
                post.setVisibility(View.GONE);
            } else {
                post.setVisibility(View.VISIBLE);
            }

            if (timer != null) {
                timer.cancel();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_new_wire);

        Profile = (ImageView) findViewById(R.id.post_dp);
        Username = (TextView) findViewById(R.id.post_uname);
        etWireText = (EditText) findViewById(R.id.post_text);
        wireImage = (ImageView) findViewById(R.id.post_image);
        post = (Button) findViewById(R.id.post_submit);
        gallery = (ImageView) findViewById(R.id.post_gallery);
        Cam = (ImageView) findViewById(R.id.post_cam);

        etWireText.addTextChangedListener(searchTextWatcher);


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadpicturefromGallery(v);
            }
        });

        sharing();
        previewAreaTitle = (ProgressBar) findViewById(R.id.preview_area);

        /** Where the previews will be dropped */
        dropPreview = (ViewGroup) findViewById(R.id.drop_preview);
        System.out.println("dropview"+dropPreview.getChildCount());

        /** Where the previews will be dropped */



     /*   Cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT < 23) {
                    //Do not need to check the permission
                } else {
                    if (checkAndRequestPermissions()) {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,CAMERA_REQUEST);
                        //If you have already permitted the permission
                    }
                }

            }
        });

*/


        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String profile = sharedPreferences.getString(Constants.KEY_ProfileDetail, "N/A");
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");

        try {
            JSONObject jprofile = new JSONObject(profile);
            uid = jprofile.getString("user_id");
            Username.setText(jprofile.getString("UserName"));
            Glide.with(getApplicationContext()).load(Global.HostName+ jprofile.getString("ProfilePic")).into(Profile);
        } catch (JSONException e) {
            e.printStackTrace();
        }



        post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String wiretext = null;
                wiretext = etWireText.getText().toString();

                    post.setVisibility(View.GONE);
                    new PostAsync().execute(sessionid, uid, wiretext,encodedString,Htmlcode,imgextention);


            }
        });




    }

/*
    Hear The Code OF Open Image From Gallery
*/


                    public void loadpicturefromGallery(View view) {
                        // Create intent to Open Image applications like Gallery, Google Photos
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

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
                                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                                // Move to first row

                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imgPath = cursor.getString(columnIndex);




                                Log.d("imagepath", imgPath);
                                imgextention = imgPath.substring(imgPath.lastIndexOf(".") + 1);
                                Log.d("imgextention", imgextention);
                                bm = BitmapFactory.decodeFile(imgPath);

                                // Set the Image in ImageView

                                wireImage.setVisibility(View.VISIBLE);
                                post.setVisibility(View.VISIBLE);
                                wireImage.setImageBitmap(bm);
                                etWireText.setHint("Add Caption");

                                encodedString = getEncoded64ImageStringFromBitmap(bm);
                                Log.d("encodedString", encodedString);


                                // Get the Image's file name

                                String fileNameSegments[] = imgPath.split("/");

                                fileName = fileNameSegments[fileNameSegments.length - 1];
                                Log.d("filename", fileName);

                                // Put file name in Async Http Post Param which will used in Php web app
                                // Convert image to String using Base64
                            ///    new AsyncProfile().execute(userid, sessionid, encodedString, imgextention);




                            }

                        } catch (Exception e) {
                            Log.d("error", String.valueOf(e));
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();

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

/*
    UP TO HEAR "UPLOAD IMAGE"
*/


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }



        return true;
    }


    @Override    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(this, "You Can't Access Camera Without Permission !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



    private void sharing() {
    // Get iantent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        /** --- From ShareVia Intent */
        if (getIntent().getExtras() != null) {
            String shareVia = (String) getIntent().getExtras().get(Intent.EXTRA_TEXT);
            if (shareVia != null) {
                shareVia = shareVia.replaceAll("\\[", "").replaceAll("\\]","");
                etWireText.setText(shareVia);
            }
        }
        if (getIntent().getAction() == Intent.ACTION_VIEW) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            List<String> params = data.getPathSegments();
            String builded = scheme + "://" + host + "/";

            for (String string : params) {
                builded += string + "/";
            }

            if (data.getQuery() != null && !data.getQuery().equals("")) {
                builded = builded.substring(0, builded.length() - 1);
                builded += "?" + data.getQuery();
            }

            System.out.println(builded);

            etWireText.setText(builded);
        }

    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }


    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }





private class PostAsync extends AsyncTask<String, String, String> {

        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(Global.HostName+"activity/index/wall_post_andr_json");

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
                        .appendQueryParameter("postText", params[2])
                        .appendQueryParameter("MediaFiles", params[3])
                        .appendQueryParameter("linkPreview", params[4])
                        .appendQueryParameter("img_extension", params[5])

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
            String result=null;
            String errorMessage=null;

            try {
                JSONObject jsonObject=new JSONObject(s);

                result=jsonObject.getString("errorStatus");
                errorMessage=jsonObject.getString("errorMessage");




            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (result != "true"){startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(),"Uploding...",Toast.LENGTH_SHORT).show();
            }else{Toast.makeText(getApplicationContext(),errorMessage,Toast.LENGTH_SHORT).show();}

        }


    }
}