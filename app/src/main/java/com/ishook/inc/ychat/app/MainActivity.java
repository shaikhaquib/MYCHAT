package com.ishook.inc.ychat.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Extrra.Session;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.Notification.MyNotificationService;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.VoiceActivity;
import com.ishook.inc.ychat.activitys.Album;
import com.ishook.inc.ychat.activitys.FriendRequests;
import com.ishook.inc.ychat.activitys.FriendsList;
import com.ishook.inc.ychat.activitys.InboxMessage;
import com.ishook.inc.ychat.activitys.LoginActivity;
import com.ishook.inc.ychat.activitys.NotificationList;
import com.ishook.inc.ychat.activitys.Search;
import com.ishook.inc.ychat.activitys.Setting;
import com.ishook.inc.ychat.adapters.FragmentViewPagerAdapter;
import com.ishook.inc.ychat.adapters.UpdateCover;
import com.ishook.inc.ychat.fragments.TabClique;
import com.ishook.inc.ychat.fragments.TabWires;
import com.ishook.inc.ychat.fragments.TabyChat;
import com.ishook.inc.ychat.fragments.Update_ProfilePic;
import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONArray;
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


@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "https://ishook.com/ishook_voice_call/andr/accessToken.php";
    RegistrationListener registrationListener = registrationListener();

    String sessionid=null;
    String otheruserid=null;
    String userid=null;
    String ProfilePic;
    String Coverpic=null;
    String encodedString;
    String imgextention;
    View imgview;

    String msg_type="1";
    String fetch_data="true";


    String imgPath, fileName;
    SearchView searchView;

    android.app.FragmentTransaction ft;
    ImageView updt_coverpic;
    ProgressBar updcoer_prog;
    ImageView upd_propic;
    TextView Username;
    TextView Email;
    ImageView coverpic;
    ImageView action_profile;
    ImageView profilepic;

    TextView noticount;
    String notiCount;

    private ViewPager viewPager;
    private FragmentViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Session session;
    private String UserName=null;
    int maxLength = 10;
    InputFilter[] FilterArray = new InputFilter[1];


    String js=null;

    private static int RESULT_LOAD_IMG = 1;



    boolean doubleBackToExitPressedOnce = false;
    private int[] tabIcons = {
            R.drawable.ic_wire_white,
            R.drawable.ic_icon2,
/*
            R.drawable.ic_clique_icon
*/
    };
    Toolbar searchtollbar;
    String search_suggestion;
    String search_image;

    String search_data;


    MenuItem item_search;
    Toolbar toolbar;
    private SimpleCursorAdapter myAdapter;
    private String[] strArrData = {"No Suggestions"};
    private String[] strArrimg = {"No img"};

    String newText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ft = getFragmentManager().beginTransaction();


        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String profile = sharedPreferences.getString(Constants.KEY_PROFILE, "N/A");
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        notiCount = sharedPreferences.getString(Constants.KEY_Noti_count, "");


        // ProfilePic = sharedPreferences.getString(Constants.KEY_ProfilePic, "N/A");
        final String[] from = new String[] {"UserName"};
        final int[] to = new int[] {android.R.id.text1};
        FilterArray[0] = new InputFilter.LengthFilter(maxLength);




        myAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.item_suggetion, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);




                    try {
            //            JSONObject jsonObject=new JSONObject(userdata);
                        JSONObject object =new JSONObject(profile);
                            userid=object.getString("user_id");
                            otheruserid=object.getString("user_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Global.sessionid=sessionid;
                    Global.userid=userid;
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setContentInsetStartWithNavigation(0);
            searchtollbar = (Toolbar) findViewById(R.id.search_toolbar);
            setSupportActionBar(toolbar);
        //adding session

            session = new Session(this);
            if(!session.loggedin()){
                logout();
            }


        viewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        prepareDataResource();

        adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);

        // Bind Adapter to ViewPager.
        viewPager.setAdapter(adapter);

        // Link ViewPager and TabLayout

        tabLayout.setupWithViewPager(viewPager);
        setTabIcons();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Adding Nav Header to navigation bar.


        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        navigationView.setNavigationItemSelectedListener(this);
        noticount=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.Notification));

        coverpic= (ImageView) headerLayout.findViewById(R.id.coverPic);
        profilepic= (ImageView)headerLayout.findViewById(R.id.drawer_propic);
        updt_coverpic= (ImageView) headerLayout.findViewById(R.id.update_coverpic);
        updcoer_prog= (ProgressBar) headerLayout.findViewById(R.id.updcoer_prog);
        ImageView pro_setting= (ImageView) headerLayout.findViewById(R.id.pro_setting);

        pro_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, new Update_ProfilePic()).commit();
            }
        });
        profilepic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 getSupportFragmentManager().beginTransaction().add(android.R.id.content, new Update_ProfilePic ()).commit();
             }
         });



        new AsyncUserinfo().execute(sessionid,userid,otheruserid);
        String profiled = sharedPreferences.getString(Constants.KEY_ProfileDetail, "N/A");
        JSONObject jobject = null;
        try {
            jobject = new JSONObject(profiled);
    } catch (JSONException e) {
        e.printStackTrace();
    }
        //Notification
        notfication();
        initializeCountDrawer();
        navigationView.setItemIconTintList(null);



    }

    private void initializeCountDrawer() {

        noticount.setGravity(Gravity.CENTER_VERTICAL);
        noticount.setTypeface(null, Typeface.BOLD);
        noticount.setTextColor(getResources().getColor(R.color.colorAccent));
        noticount.setText(notiCount);
    }

    private void notfication() {

        Intent intent = new Intent(this, MyNotificationService.class);
        intent.putExtra("sid",sessionid);
        intent.putExtra("uid",userid);
        intent.putExtra("mt",msg_type);
        intent.putExtra("fd",fetch_data);		startService(intent);

    }


    public void loadImagefromGallery(View view) {
        imgview=view;
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                // Set the Image in ImageView


                Log.d("imagepath", imgPath);
                imgextention = imgPath.substring(imgPath.lastIndexOf(".") + 1);
                Log.d("imgextention", imgextention);
                Bitmap bm = BitmapFactory.decodeFile(imgPath);
                encodedString = getEncoded64ImageStringFromBitmap(bm);
                Log.d("encodedString", encodedString);


                // Get the Image's file name

                String fileNameSegments[] = imgPath.split("/");

                fileName = fileNameSegments[fileNameSegments.length - 1];
                Log.d("filename", fileName);

                // Put file name in Async Http Post Param which will used in Php web app
                // Convert image to String using Base64
                new UpdateCover(getApplicationContext(), updcoer_prog,coverpic,bm).execute(userid, sessionid, encodedString, imgextention);



            } else {

                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {

            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

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
    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    //All menu realated
    //Implementd SearchView search here.


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_search, menu);

    return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       if (id == R.id.action_search) {

           startActivity(new Intent(getApplicationContext(),Search.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.Notification) {
             startActivity(new Intent(getApplicationContext(),NotificationList.class));
        } else if (id == R.id.freindrequestlist) {
             startActivity(new Intent(getApplicationContext(), FriendRequests.class));
        }else  if (id == R.id.friend) {
             startActivity(new Intent(getApplicationContext(),FriendsList.class));
         } else if (id == R.id.Album) {
startActivity(new Intent(getApplicationContext(), Album.class));
    }else if(id== R.id.nav_message) {
            startActivity(new Intent(getApplicationContext(), InboxMessage.class));
         }else if(id== R.id.nav_setting) {
            startActivity(new Intent(getApplicationContext(), Setting.class));
        }else if(id== R.id.nav_applemusic) {
             boolean isAppInstalled = appInstalledOrNot("com.apple.android.music");

             if(isAppInstalled) {
                 //This intent will help you to launch if the package is already installed
                 Intent LaunchIntent = getPackageManager()
                         .getLaunchIntentForPackage("com.apple.android.music");
                 startActivity(LaunchIntent);

                 System.out.println("Application is already installed.");
             } else {
                 // Do whatever we want to do if application not installed
                 // For example, Redirect to play store

                 System.out.println("Application is not currently installed.");
                 Intent intent = new Intent(Intent.ACTION_VIEW);
                 intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.apple.android.music&referrer=utm_source=https%3A%2F%2Fitunes.apple.com%2Fsubscribe%3Fapp%3Dmusic%26at%3D1001lMLL"));
                 startActivity(intent);
             }       }else if (id == R.id.logout) {
            logout();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void logout(){
        session.setLoggedin(false);
        new AsyncLogOut().execute(userid,sessionid);

    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void prepareDataResource() {

        fragments = new ArrayList<>();
        fragments.add(new TabWires());
        fragments.add(new TabyChat());
/*
        fragments.add(new TabClique());
*/

        titles.add("Wires");
        titles.add("ychat");
/*
        titles.add("Clique");
*/

        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

    }
    // Sets the Icons for the Tabs
    private void setTabIcons() {

        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
/*
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
*/



    }
    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.customeview, null);
        TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
        txt_title.setText(titles.get(position));
        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
        img_title.setImageResource(tabIcons[position]);

        return view;
    }

    private  class AsyncUserinfo extends AsyncTask <String,String,String>{
        HttpURLConnection conn;
        URL url = null;
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"/home/profile/bio_json");
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
                        .appendQueryParameter("otherUserId", params[2]);
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
        protected void onPostExecute(String result) {


            if (result=="exception"){

                try {
                    SharedPreferences preferences = getSharedPreferences( getApplicationContext().getPackageName()+Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                    String profiledetail = preferences.getString(Constants.KEY_ProfileDetail, "N/A");
                    JSONObject jsonObject=new JSONObject(profiledetail);

                    Coverpic=jsonObject.getString("CoverPic");
                    Log.d("coverpic",Coverpic);
                    ProfilePic=jsonObject.getString("ProfilePic");
                    Log.d("ppic",ProfilePic);


                    UserName=jsonObject.getString("FirstName")+"_"+userid;
                    retrieveAccessToken();
                    setTitle(jsonObject.getString("UserName"));
                    Glide.with(getApplicationContext()).load(Global.HostName+Coverpic)
                            .into(coverpic) ;
                    coverpic.getLayoutParams().height = 500;
                    coverpic.requestLayout();
                    Glide.with(getApplicationContext()).load(Global.HostName+ProfilePic)
                            .into(profilepic);






                } catch (JSONException e) {
                e.printStackTrace();
            }


            }else {

            try {

                JSONObject jsonObject=new JSONObject(result);
                String userprofile=jsonObject.getString("profile");
                JSONObject object=new JSONObject(userprofile);



                Coverpic=object.getString("CoverPic");
                Log.d("coverpic",Coverpic);
                ProfilePic=object.getString("ProfilePic");
                Log.d("ppic",ProfilePic);
                String jabber_user=object.getString("jabber_user");



                setTitle(object.getString("UserName"));
                Glide.with(getApplicationContext()).load(Global.HostName+Coverpic).into(coverpic) ;
                coverpic.getLayoutParams().height = 500;
                coverpic.requestLayout();
                Glide.with(getApplicationContext()).load(Global.HostName+ProfilePic)
                        .into(profilepic);

                UserName=object.getString("UserName")+"_"+userid;
                retrieveAccessToken();

                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.KEY_ProfileDetail,userprofile);
                editor.putString(Constants.KEY_USERID,userid);
                editor.putString(Constants.KEY_jabber_user,jabber_user);
                editor.apply();


            } catch (JSONException e) {
                e.printStackTrace();
            }



        }}
    }

    public class AsyncSearch extends AsyncTask <String,String,String> {

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
            try {
                JSONObject jsonObject=new JSONObject(s);
                ArrayList<String> dataList = new ArrayList<>();

               Log.d("s",s);
             /*    search_data=jsonObject.getString("users");
                js=s;



                JSONObject object=new JSONObject(search_data);
                search_suggestion=object.getString("value");
                Log.d("ss",search_suggestion);*/

              /*  String[] strings= (String[]) jsonObject.get("users");
                search_suggestion=strings.g*/

                JSONArray jsonArray=jsonObject.getJSONArray("friends");
                for(int i=0; i<jsonArray.length();i++){

                    JSONObject object=jsonArray.getJSONObject(i);

                    String s1=object.getString("UserName");
                    String s2=object.getString("userId");

                    String s3=s1+"           @"+s2;
                   // String[] strings={s1,s2};

                    dataList.add(s3);

                }

                strArrData = dataList.toArray(new String[dataList.size()]);
                Log.d("exp", String.valueOf(strArrData));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (search_suggestion!=null){
                strArrData=new String[] {search_suggestion};
            }
            Log.d("sa", String.valueOf(strArrData));
            final MatrixCursor mc = new MatrixCursor(new String[]{ BaseColumns._ID, "UserName" });
            for (int i=0; i<strArrData.length; i++) {
                if (strArrData[i].toLowerCase().startsWith(newText.toLowerCase()))
                    mc.addRow(new Object[] {i, strArrData[i]});
            }
            myAdapter.changeCursor(mc);
        }
    }


    private class AsyncLogOut extends AsyncTask<String ,String,String> {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSigning Out....");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"users/login/logout_json");
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
                        .appendQueryParameter("UserId", params[1])
                        .appendQueryParameter("sessionId", params[0]);

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


            pdLoading.dismiss();
            Intent intent = new Intent(MainActivity.this, MyNotificationService.class);
            stopService(intent);
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

        }



    }


    private void registerForCallInvites() {
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
            Log.i("Twilio", "Registering with FCM");
            Voice.register(this, Global.accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);
        }
    }
    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(String accessToken, String fcmToken) {
                Log.d("Twilio", "Successfully registered FCM " + fcmToken);
            }

            @Override
            public void onError(RegistrationException error, String accessToken, String fcmToken) {
                String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
                Log.e("Twilio", message);
                Snackbar.make(tabLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }


    private void retrieveAccessToken() {
        Ion.with(this).load(TWILIO_ACCESS_TOKEN_SERVER_URL + "?identity=" + UserName.replace("\\s+","").toLowerCase()).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null) {
                    Log.d("Twilio", "Access token: " + accessToken);
                    Global.accessToken = accessToken;
                    registerForCallInvites();
                } else {
                    Snackbar.make(tabLayout,
                            "Error retrieving access token. Unable to make calls",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
