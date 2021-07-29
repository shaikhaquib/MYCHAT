package com.ishook.inc.ychat.fcm;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.SoundPoolManager;
import com.ishook.inc.ychat.VoiceActivity;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.CancelledCallInvite;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;


import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VoiceFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "VoiceFCMService";
    public static final String NOTIFICATION_ID_KEY = "NOTIFICATION_ID";
    public static final String CALL_SID_KEY = "CALL_SID";
    private static final String VOICE_CHANNEL = "default";

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received onMessageReceived()");
        Log.d(TAG, "Bundle data: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            boolean valid = Voice.handleMessage(this, remoteMessage.getData(), new MessageListener() {
                @Override
                public void onCallInvite(@NonNull CallInvite callInvite) {
                    final int notificationId = (int) System.currentTimeMillis();
                    handleInvite(callInvite, notificationId);
                }

                @Override
                public void onCancelledCallInvite(@NonNull @NotNull CancelledCallInvite cancelledCallInvite, @Nullable @org.jetbrains.annotations.Nullable CallException e) {
                    handleCanceledCallInvite(cancelledCallInvite);
                }

                /*@Override
                public void onCancelledCallInvite(@NonNull @NotNull CancelledCallInvite cancelledCallInvite) {
                }*/
            });

            if (!valid) {
                Log.e(TAG, "The message was not a valid Twilio Voice SDK payload: " +
                        remoteMessage.getData());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Intent intent = new Intent(VoiceActivity.ACTION_FCM_TOKEN);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleInvite(CallInvite callInvite, int notificationId) {
        Intent intent = new Intent(this, IncomingCallNotificationService.class);
        intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
        intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, callInvite);

        startService(intent);
    }

    private void handleCanceledCallInvite(CancelledCallInvite cancelledCallInvite) {
        Intent intent = new Intent(this, IncomingCallNotificationService.class);
        intent.setAction(VoiceActivity.ACTION_CANCEL_CALL);
        intent.putExtra(VoiceActivity.CANCELLED_CALL_INVITE, cancelledCallInvite);

        startService(intent);
    }
}
