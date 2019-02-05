package com.ishook.inc.ychat.Notification;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;

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
import java.util.Timer;
import java.util.TimerTask;

// Intent Service Demo
public class MyNotificationService extends Service {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

	String sessionId;
	String UserId;
	String msg_type="1";
	String fetch_data="true";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mTimer = new Timer();
		mTimer.schedule(timerTask, 2000, 5 * 1000);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

	/*	sessionId=intent.getStringExtra("sid");
		UserId=intent.getStringExtra("uid");
		msg_type=intent.getStringExtra("mt");
		fetch_data=intent.getStringExtra("fd");*/


        return super.onStartCommand(intent, flags, startId);
	}

	private Timer mTimer;

	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
            SharedPreferences sharedPreferences =getSharedPreferences( getPackageName()+ Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            sessionId = sharedPreferences.getString(Constants.KEY_SESSION, "N/A");
            UserId = sharedPreferences.getString(Constants.KEY_USERID, "N/A");

            new AsyncTest().execute(sessionId,UserId,msg_type,fetch_data);


		}
	};

    @Override
    public void onDestroy() {
        super.onDestroy();
		try {
			mTimer.cancel();
			timerTask.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent("shaikh.services.test");
		intent.putExtra("yourvalue", "torestore");
		sendBroadcast(intent);
	}
    private class AsyncTest  extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(Global.HostName+"notifications/index/get_msg_data_andr");
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
                        .appendQueryParameter("msg_type", params[2])
                        .appendQueryParameter("fetch_data",params[3])
                       ;
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
                   // Log.d("Result",result.toString());
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

            String Sucssmsg = null;
            String noticount = null;

            try {

                JSONObject object = new JSONObject(s);
                Sucssmsg = object.getString("success_msg");
                JSONArray msg_data=object.getJSONArray("msg_data");
                noticount= String.valueOf(msg_data.length());
               // Log.d("count",noticount);
                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(Constants.KEY_Noti_count, noticount);

                editor.apply();



              /*  NotificationCompat.Builder nbuilder=(NotificationCompat.Builder)new NotificationCompat.Builder(MyNotificationService.this)
                    .setSmallIcon(R.drawable.ic_ishook)
                    .setContentTitle("Bob has liked your wired")
                    .setContentText(UserId);
                long[] vibrate = { 0, 100, 200, 300 };
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                nbuilder.setSound(alarmSound);
                nbuilder.setVibrate(vibrate);

            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0,nbuilder.build());*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }}
