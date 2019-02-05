package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Chat_Room;
import com.ishook.inc.ychat.list.Inboxlist;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 13-01-2018.
 */

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Inboxlist> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public InboxAdapter(Context cont, List<Inboxlist> data) {

        this.context = cont;
        inflater = LayoutInflater.from(cont);
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.inboxadap, parent, false);
        InboxHolder holder = new InboxHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final InboxHolder ih=(InboxHolder) holder;
        final Inboxlist current=mData.get(position);

        ih.body.setText(current.body);
        ih.name.setText(current.user_name);
        ih.time.setText(current.timeAgo);
        ih.inbox.setTag(current);

        Glide.with(context).load(current.ProfilePic).into(ih.dp);

        ih.inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Chat_Room.class);
                intent.putExtra("profilepic",current.ProfilePic);
                intent.putExtra("friendName",current.user_name);
                intent.putExtra("FriendId",current.sender_id);
                Log.d("Friend",current.sender_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private class InboxHolder extends RecyclerView.ViewHolder {

        ImageView dp;
        TextView name;
        TextView time;
        TextView body;
        LinearLayout inbox;

        public InboxHolder(View itemView) {
            super(itemView);

            dp= (ImageView) itemView.findViewById(R.id.senderpic);
            name= (TextView) itemView.findViewById(R.id.sendername);
            time= (TextView) itemView.findViewById(R.id.timeago);
            body= (TextView) itemView.findViewById(R.id.message_body);
            inbox= (LinearLayout) itemView.findViewById(R.id.inbox);
        }
    }
}
