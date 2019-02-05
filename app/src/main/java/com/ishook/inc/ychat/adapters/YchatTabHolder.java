package com.ishook.inc.ychat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishook.inc.ychat.R;


/**
 * Created by Shaikh on 12-Sep-17.
 */

public class YchatTabHolder extends RecyclerView.ViewHolder{
    TextView uname;
    TextView uid;
    ImageView profilePic;
    LinearLayout linearLayout;

    public YchatTabHolder(View itemView) {
        super(itemView);
        uname= (TextView) itemView.findViewById(R.id.freindname);
        uid= (TextView) itemView.findViewById(R.id.userid);
        profilePic= (ImageView) itemView.findViewById(R.id.userprofilePic);
        linearLayout= (LinearLayout) itemView.findViewById(R.id.chat_layout);
    }
}
