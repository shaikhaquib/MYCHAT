package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import com.ishook.inc.ychat.activitys.ListPhoto;
import com.ishook.inc.ychat.list.Album_list;

/**
 * Created by Shaikh on 14-01-2018.
 */

public class AlbumAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    List<Album_list> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public AlbumAdapt(Context cont, List<Album_list> data) {

        this.context = cont;
        inflater = LayoutInflater.from(context);
        this.mData = data;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.albumadp, parent, false);
        AlbumHolder holder=new AlbumHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        AlbumHolder myViewHolder=(AlbumHolder) holder;
        final Album_list current = mData.get(position);

        myViewHolder.album_name.setText(current.AlbumName);
        Glide.with(context).load(current.thumbImage).
        into(myViewHolder.album_img);

        myViewHolder.album_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, ListPhoto.class);
                i.putExtra("Aid",current.AlbumId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class AlbumHolder extends RecyclerView.ViewHolder{

        TextView album_name;
        ImageView album_img;

        public AlbumHolder(View itemView) {
            super(itemView);

            album_img= (ImageView) itemView.findViewById(R.id.Album_icon);
            album_name= (TextView) itemView.findViewById(R.id.AlbumName);
        }
    }
}
