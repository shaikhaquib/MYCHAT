package com.ishook.inc.ychat.adapters;


import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Chat_Room;
import com.ishook.inc.ychat.list.FreindData;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 12-Sep-17.
 */

public class ychat_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    List<FreindData> mData= Collections.emptyList();
    private LayoutInflater inflater;
    TextView no_friend;

    public ychat_adapter(Context context, List<FreindData> mData) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mData;
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
                Intent intent=new Intent(context, Chat_Room.class);
                intent.putExtra("sessionId",current.Sessionid);
                intent.putExtra("Userid",current.uid);
                intent.putExtra("profilepic",current.ProfilePic);
                intent.putExtra("friendName",current.UserName);
                intent.putExtra("FriendId",current.UserId);
                intent.putExtra("fname",current.fname);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

