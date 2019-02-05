package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.list.NotiList;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 18-01-2018.
 */

public class NotiAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    List<NotiList> mData = Collections.emptyList();
    private LayoutInflater inflater;


    public NotiAdapt(Context cont, List<NotiList> data) {

        this.context = cont;
        inflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notificationadapt, parent, false);
        NotiHolder holder=new NotiHolder(view);
        return holder;    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        NotiHolder myViewHolder=(NotiHolder) holder;
        final NotiList current = mData.get(position);

        myViewHolder.uname.setText(current.notification_user_name);
        myViewHolder.body.setText(current.notification_body);

        String[] splited = current.notification_date.split("\\s+");

        String date=splited[0];
        String time=splited[1];


        myViewHolder.time.setText(time);
        Glide.with(context).load(current.notification_user_photo).into(myViewHolder.photo);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NotiHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView uname;
        TextView body;
        TextView time;

        public NotiHolder(View itemView) {
            super(itemView);

            photo= (ImageView) itemView.findViewById(R.id.notdp);
            uname= (TextView) itemView.findViewById(R.id.notiname);
            body= (TextView) itemView.findViewById(R.id.notibody);
            time= (TextView) itemView.findViewById(R.id.notime);


        }
    }
}
