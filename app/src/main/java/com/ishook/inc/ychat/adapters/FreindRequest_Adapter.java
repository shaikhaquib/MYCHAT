package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Friend;
import com.ishook.inc.ychat.list.FRequest_list;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 28-12-2017.
 */

public class FreindRequest_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<FRequest_list> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public FreindRequest_Adapter(Context context, List<FRequest_list> mdata) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mdata;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_friendrequist_list, parent, false);
        FrHolder holder = new FrHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        FrHolder myViewHolder=(FrHolder)holder;
        final FRequest_list current = mData.get(position);

        myViewHolder.uname.setText(current.frUname);
        myViewHolder.uid.setText(current.frUid);
        myViewHolder.btnAccept.setTag(current);
        myViewHolder.btnAccept.setTag(current);

        myViewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsynkAccept(context).execute(current.Sessionid,current.Uid,current.frUid);
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mData.size());
            }
        });

        myViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsynkDelete(context).execute(current.Sessionid,current.Uid,current.frUid);
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mData.size());
            }
        });

        Glide.with(context).load(Global.HostName+"uploads/"+current.frUid+"/profile/profile_pic/"+current.frProfilepic)
                .into(myViewHolder.profilePic);

        myViewHolder.profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        String string="true";

                        Intent intent=new Intent(context,Friend.class);
                        intent.putExtra("search_data","@"+current.frUid);
                        intent.putExtra("true","true");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class FrHolder extends RecyclerView.ViewHolder{
        TextView uname;
        TextView uid;
        ImageView profilePic;
        Button btnAccept;
        Button btnDelete;

        public FrHolder(View itemView) {
            super(itemView);
            uname= (TextView) itemView.findViewById(R.id.frname);
            uid= (TextView) itemView.findViewById(R.id.fruserid);
            profilePic= (ImageView) itemView.findViewById(R.id.frprofilePic);
            btnAccept= (Button) itemView.findViewById(R.id.btnAccept);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
        }
    }

}
