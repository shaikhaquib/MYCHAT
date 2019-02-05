package com.ishook.inc.ychat.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.R;

import org.json.JSONException;
import org.json.JSONObject;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Extrra.Session;


public class SharedDemo extends AppCompatActivity {

    private Button button;
    private TextView sessions;
    private TextView user;
    private Session session;
    String Profile=null;
    boolean doubleBackToExitPressedOnce = false;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedemo);
        session = new Session(this);

        sessions = (TextView) findViewById(R.id.seid);
        user= (TextView) findViewById(R.id.uid);
        TextView tz= (TextView) findViewById(R.id.tz);
        TextView country= (TextView) findViewById(R.id.Country);
        TextView address= (TextView) findViewById(R.id.Address);
        TextView city=(TextView)findViewById(R.id.City);
        TextView state=(TextView)findViewById(R.id.State);
        TextView zip=(TextView)findViewById(R.id.Zip);
        TextView gender=(TextView)findViewById(R.id.Gender);
        TextView dob=(TextView)findViewById(R.id.DateOfBirth);
        TextView phone=(TextView)findViewById(R.id.Phone);
        TextView rel_status_id=(TextView)findViewById(R.id.rel_status_id);
        TextView aboutme=(TextView)findViewById(R.id.AboutMe);
        TextView lattitude=(TextView)findViewById(R.id.Lattitude);
        TextView longitude=(TextView)findViewById(R.id.Longitude);
        TextView fname=(TextView)findViewById(R.id.FirstName);
        TextView lname=(TextView)findViewById(R.id.LastName);
        TextView email=(TextView)findViewById(R.id.Email);
        TextView propic=(TextView)findViewById(R.id.ProfilePic);
        TextView covpic=(TextView)findViewById(R.id.CoverPic);




       SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        String seid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        Profile = sharedPreferences.getString(Constants.KEY_PROFILE, "N/A");




        try {
            sessions.setText(seid);

            JSONObject profile =new JSONObject(Profile);
            user.setText(profile.getString("user_id"));
            tz.setText(profile.getString("TimeZone"));
            address.setText(profile.getString("Address"));
            city.setText(profile.getString("City"));
            state.setText(profile.getString("State"));
            zip.setText(profile.getString("Zip"));
            gender.setText(profile.getString("Gender"));
            dob.setText(profile.getString("DateOfBirth"));
            phone.setText(profile.getString("Phone"));;
            rel_status_id.setText(profile.getString("rel_status_id"));
            aboutme.setText(profile.getString("AboutMe"));
            lattitude.setText(profile.getString("Lattitude"));
            longitude.setText(profile.getString("Longitude"));
            fname.setText(profile.getString("FirstName"));
            lname.setText(profile.getString("LastName"));
            email.setText(profile.getString("Email"));
            propic.setText(profile.getString("ProfilePic"));
            covpic.setText(profile.getString("CoverPic"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),Profile,Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exmenu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.exit) {


            finish();
        }
        else if(id==R.id.logout){
            logout();

        }

        return super.onOptionsItemSelected(item);
    }
    private void logout(){

        finish();
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

}
