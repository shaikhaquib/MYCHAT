package com.ishook.inc.ychat.activitys;

import static com.ishook.inc.ychat.app.MainActivity.channelNames;
import static com.ishook.inc.ychat.app.MainActivity.channels;
import static com.ishook.inc.ychat.app.MainActivity.groupsName;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
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
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.demo.ChannelModel;
import com.twilio.chat.demo.TwilioApplication;
import com.twilio.chat.demo.activities.MessageActivity;

public class CreateGroup extends AppCompatActivity {

    EditText etGroupName;
    Button CreateGroup;
    String sessionid;
    String userid;
    String jabber_user;
    String TAG = "CreateGroup";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        etGroupName= (EditText) findViewById(R.id.etGroupname);
        CreateGroup= (Button) findViewById(R.id.Creat_group);
        progressDialog = new ProgressDialog(this);

        SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
        sessionid = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
        userid = sharedPreferences.getString(Constants.KEY_USERID, "N/A");
        jabber_user = sharedPreferences.getString(Constants.KEY_jabber_user, "N/A");

        CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading..");
                progressDialog.show();
                String room_name=etGroupName.getText().toString();
                TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().createChannel("GRP"+room_name, Channel.ChannelType.PRIVATE, new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(final Channel channel) {
                        groupsName.add(new ChannelModel(channel));
                        channel.getMembers().addByIdentity(MainActivity.UserName, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(), MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", Global.userid).putExtra("group",channel.getFriendlyName()));
                                        finish();

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                super.onError(errorInfo);
                                Log.d(TAG, "onError: "+errorInfo.getMessage());
                                Toast.makeText(getApplicationContext(), errorInfo.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });

                    }
                });

            }
        });



    }
}
