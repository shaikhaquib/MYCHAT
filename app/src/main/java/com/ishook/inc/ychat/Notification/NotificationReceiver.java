package com.ishook.inc.ychat.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Shaikh on 15-11-2017.
 */

public class NotificationReceiver
        extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      //  Log.i("Service Stops", "Ohhhhhhh");
        context.startService(new Intent(context, MyNotificationService.class));
    }
}