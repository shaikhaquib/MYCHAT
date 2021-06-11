package com.ishook.inc.ychat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.list.Photolist;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 17-01-2018.
 */

public class PhotoListAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Photolist> mData= Collections.emptyList();
    private LayoutInflater inflater;
    public PhotoListAdapt(Context cont, List<Photolist> data) {
        this.context = cont;
        inflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.photolist_adapt, parent, false);
        PhotoHolder holder=new PhotoHolder(view);
        return holder;    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final PhotoHolder myViewHolder=(PhotoHolder) holder;
        final Photolist current = mData.get(position);

        myViewHolder.Likecount.setText(current.likeCount+ " Like");
        myViewHolder.Like.setTag(current);
        Glide.with(context).load(current.mediaLink).into(myViewHolder.photo);


        //Like button Operation

        if (current.selfLike.equals(false)){
            myViewHolder.Like.setChecked(false);
        }else{
            myViewHolder.Like.setChecked(true);
        }

        //Setting visibility of TextView Like.

        final int Likes = Integer.parseInt(current.likeCount);

        Log.d("Likes", String.valueOf(Likes));
        if (Likes != 0) {
            myViewHolder.Likecount.setVisibility(View.VISIBLE);
            myViewHolder.Likecount.setText(current.likeCount + " Likes");
        } else if (Likes == 1 && current.selfLike.equals(false)) {
            myViewHolder.Likecount.setVisibility(View.VISIBLE);
        }


        myViewHolder.Like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                int intValue = Integer.valueOf(current.likeCount);
                int likeIncrement = intValue + 1;

                int selflikevalue = Integer.valueOf(current.likeCount);
                Log.d("selflike", String.valueOf(selflikevalue));


                if (isChecked) {

                    myViewHolder.Likecount.setVisibility(View.VISIBLE);


                    if (current.selfLike.equals(true)) {
                        myViewHolder.Likecount.setText(intValue + "  Likes");
                    } else {
                        myViewHolder.Likecount.setText(likeIncrement++ + "  Likes");
                    }
                }

                int likeDecrement = likeIncrement - 1;
                if (!isChecked) {
                    if (Likes == 0) {
                        myViewHolder.Likecount.setVisibility(View.GONE);
                    } else if (Likes == 0 && current.selfLike.equals(true)) {
                        myViewHolder.Likecount.setVisibility(View.GONE);
                    } else if (Likes == 1 && current.selfLike.equals(true)) {
                        myViewHolder.Likecount.setVisibility(View.GONE);
                    }
                    if (current.selfLike.equals(true)) {
                        myViewHolder.Likecount.setText(likeIncrement - 2 + "  Likes");
                    } else {
                        myViewHolder.Likecount.setText(likeDecrement-- + "  Likes");
                    }
                }


            }
        });

        myViewHolder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlbumLike().execute(current.sessionid, current.UserId, current.MediaId);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



    public class PhotoHolder extends RecyclerView.ViewHolder{

        TextView Likecount;
        ImageView photo;
        CheckBox Like;


        public PhotoHolder(View itemView) {
            super(itemView);

            photo= (ImageView) itemView.findViewById(R.id.Alimg);
            Likecount= (TextView) itemView.findViewById(R.id.allikecount);
            Like= (CheckBox) itemView.findViewById(R.id.allike);

        }
    }
}
