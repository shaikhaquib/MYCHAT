package com.ishook.inc.ychat.adapters;


import static com.ishook.inc.ychat.app.MainActivity.channelNames;
import static com.ishook.inc.ychat.app.MainActivity.channels;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Chat_Room;
import com.ishook.inc.ychat.app.MainActivity;
import com.ishook.inc.ychat.list.FreindData;
import com.bumptech.glide.Glide;
import com.twilio.chat.Attributes;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.StatusListener;
import com.twilio.chat.User;
import com.twilio.chat.demo.BasicChatClient;
import com.twilio.chat.demo.ChannelModel;
import com.twilio.chat.demo.Constants;
import com.twilio.chat.demo.TwilioApplication;
import com.twilio.chat.demo.activities.MessageActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Shaikh on 12-Sep-17.
 */

public class ychat_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatClientListener {
    private static final String TAG = "Ychat Adapter";
    private Context context;
    List<FreindData> mData= Collections.emptyList();
    private LayoutInflater inflater;
    TextView no_friend;
    BasicChatClient  basicClient;
    public ychat_adapter(Context context, List<FreindData> mData) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mData;

        basicClient = TwilioApplication.Companion.getInstance().basicClient;
//        basicClient.getChatClient().addListener(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.tab_ychat_adapter, parent, false);
        YchatTabHolder holder = new YchatTabHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

       YchatTabHolder myViewHolder=(YchatTabHolder)holder;
        final FreindData current = mData.get(position);

        myViewHolder.uname.setText(current.UserName);
        myViewHolder.uid.setText(current.UserId);
        myViewHolder.linearLayout.setTag(current);
        Glide.with(context).load(current.ProfilePic)
                .into(myViewHolder.profilePic);

        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  createPrivateChannel(current.UserName+"_"+current.UserId,current.UserName);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    protected void createPrivateChannel(final String identity, final String userName) {
        String channelFormatMain = MainActivity.UserName+"_"+identity;
        String channelFormatAlternate= MainActivity.UserName+"_"+identity;
        if (channelNames.containsKey(channelFormatMain)) {

            Log.d(TAG, "createPrivateChannel: "+channelFormatMain);
            ChannelModel channelModel = channelNames.get(channelFormatMain);
            TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().getChannel(channelModel.getSid(), new CallbackListener<Channel>() {
                @Override
                public void onSuccess(final Channel channel) {
                    if (channel.getMembers().getMembersList().size() < 2){
                        channel.getMembers().addByIdentity(identity, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                // context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));

                                channel.getMembers().addByIdentity(MainActivity.UserName, new StatusListener() {
                                    @Override
                                    public void onSuccess() {
                                        context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        super.onError(errorInfo);
                                        Log.d(TAG, "onError: "+errorInfo.getMessage());
                                        Toast.makeText(context, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                super.onError(errorInfo);
                                Log.d(TAG, "onError: "+errorInfo.getMessage());

                            }
                        });

                    }
                    else
                        context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                }
            });
        }else if (channelNames.containsKey(channelFormatAlternate)) {
            Log.d(TAG, "createPrivateChannel: "+channelFormatAlternate);
            ChannelModel channelModel = channelNames.get(channelFormatAlternate);
            TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().getChannel(channelModel.getSid(), new CallbackListener<Channel>() {
                @Override
                public void onSuccess(final Channel channel) {

                    if (channel.getMembers().getMembersList().size() < 2){
                        channel.getMembers().addByIdentity(identity, new StatusListener() {
                            @Override
                            public void onSuccess() {
                                // context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));

                                channel.getMembers().addByIdentity(MainActivity.UserName, new StatusListener() {
                                    @Override
                                    public void onSuccess() {
                                        context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        super.onError(errorInfo);
                                        Log.d(TAG, "onError: "+errorInfo.getMessage());
                                        Toast.makeText(context, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                super.onError(errorInfo);
                                Log.d(TAG, "onError: "+errorInfo.getMessage());

                            }
                        });

                    }
                    else
                    context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                }
            });
        }else if (channelNames.containsKey(identity)) {
            Log.d(TAG, "createPrivateChannel: "+identity);
            ChannelModel channelModel = channelNames.get(identity);
            TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().getChannel(channelModel.getSid(), new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                }
            });
        }else {
            Log.d(TAG, "createPrivateChannel: "+channelFormatMain);
            TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().createChannel(channelFormatMain, Channel.ChannelType.PRIVATE, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(final Channel channel) {
                    channels.put(channel.getSid(), new ChannelModel(channel));
                    channelNames.put(channel.getFriendlyName(), new ChannelModel(channel));


                    channel.getMembers().addByIdentity(identity, new StatusListener() {
                        @Override
                        public void onSuccess() {
                           // context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));

                            channel.getMembers().addByIdentity(MainActivity.UserName, new StatusListener() {
                                @Override
                                public void onSuccess() {
                                    context.startActivity(new Intent(context, MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", userName));
                                }

                                @Override
                                public void onError(ErrorInfo errorInfo) {
                                    super.onError(errorInfo);
                                    Log.d(TAG, "onError: "+errorInfo.getMessage());
                                    Toast.makeText(context, errorInfo.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Log.d(TAG, "onError: "+errorInfo.getMessage());

                        }
                    });

                }
            });
        }
    }

    private void createChannelWithType(Channel.ChannelType type) {
        Random rand = new Random();
        int value = rand.nextInt(50);

        JSONObject attrs = new JSONObject();
        try {
            attrs.put("topic", "testing channel creation with options "+value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String typ;
        if (type == Channel.ChannelType.PRIVATE)
            typ = "Priv";
        else
            typ = "Pub";

        Channels.ChannelBuilder builder = basicClient.getChatClient().getChannels().channelBuilder();

        builder.withFriendlyName(typ+"_TestChannelF_"+value)
                .withUniqueName(typ+"_TestChannelU_"+value)
                .withType(type)
                .withAttributes(new Attributes(attrs))
                .build(new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(Channel channel) {
                        Log.d("TAG", "onSuccess: Successfully created a channel with options.");
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                        Log.d("TAG", "onSuccess: Successfully created a channel with options.");
                    }
                });
    }

    @Override
    public void onChannelJoined(Channel channel) {
        
    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onChannelAdded(Channel channel) {

    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

    }

    @Override
    public void onChannelDeleted(Channel channel) {

    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {

    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {

    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onNewMessageNotification(String s, String s1, long l) {

    }

    @Override
    public void onAddedToChannelNotification(String s) {

    }

    @Override
    public void onInvitedToChannelNotification(String s) {

    }

    @Override
    public void onRemovedFromChannelNotification(String s) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }

    @Override
    public void onTokenExpired() {

    }

    @Override
    public void onTokenAboutToExpire() {

    }
}

