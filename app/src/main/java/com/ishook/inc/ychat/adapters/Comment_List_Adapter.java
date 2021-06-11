package com.ishook.inc.ychat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import com.ishook.inc.ychat.list.Comment_List;

/**
 * Created by Shaikh on 26-Sep-17.
 */

public class Comment_List_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private Context context;
    List<Comment_List> mData= Collections.emptyList();
    private LayoutInflater inflater;


    public Comment_List_Adapter(Context context, List<Comment_List> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comment_list_adapter, parent, false);
        Comment_Holder holder = new Comment_Holder(view);
        return holder;    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Comment_Holder myViewHolder=(Comment_Holder) holder;
        final Comment_List current = mData.get(position);
        final int cmt_self_like= Integer.parseInt(current.comment_selfLikes);
        final int like= Integer.parseInt(current.countcommentLikes);


        myViewHolder.comment_subject.setText(current.comment_subject_name);
        myViewHolder.comment_object.setText(current.comment_object);
        myViewHolder.comment_text.setText(current.comment_text);
        myViewHolder.comment_time.setText(current.comment_time);
        myViewHolder.comtlike.setTag(current);
        myViewHolder.comt_like_count.setTag(current);
        Glide.with(context).load(current.profile)
                .into(myViewHolder.comment_profile);


        if (like!=0){
            myViewHolder.comt_like_count.setVisibility(View.VISIBLE);
            myViewHolder.comt_like_count.setText(current.countcommentLikes+" Likes");
        }


        if (cmt_self_like==1)
        {
            myViewHolder.comtlike.setChecked(true);

        }
        else {
            myViewHolder.comtlike.setChecked(false);
        }



        myViewHolder.comtlike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {


                int likeIncrement = like+1;

                if (isChecked) {

                    myViewHolder.comt_like_count.setVisibility(View.VISIBLE);


                    if (cmt_self_like==1){
                        myViewHolder.comt_like_count.setText(like + "  Likes");
                    }

                    else { myViewHolder.comt_like_count.setText(likeIncrement++ + "  Likes");}
                }

                int likeDecrement=likeIncrement-1;
                if (!isChecked){
                    if (like==0){
                        myViewHolder.comt_like_count.setVisibility(View.GONE);
                    }else if (like==0&&cmt_self_like==1){
                        myViewHolder.comt_like_count.setVisibility(View.GONE);
                    }
                    else if (like==1&&cmt_self_like==1){
                        myViewHolder.comt_like_count.setVisibility(View.GONE);
                    }
                    if (cmt_self_like==1){
                        myViewHolder.comt_like_count.setText(likeIncrement-2 + "  Likes");
                    }

                    else { myViewHolder.comt_like_count.setText(likeDecrement-- + "  Likes");}


                }

               // Toast.makeText(context,like + "\n" +cmt_self_like,Toast.LENGTH_LONG).show();


            }
        });



        myViewHolder.comtlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsynkComment().execute(current.sessionid,current.userid,current.commentid);
            }
        });



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Comment_Holder extends RecyclerView.ViewHolder{

        ImageView comment_profile;
        TextView comment_subject;
        TextView comment_object;
        TextView comment_text;
        TextView comment_time;

        CheckBox comtlike;
        TextView comt_like_count;



        public Comment_Holder(View itemView) {
            super(itemView);
            comment_profile= (ImageView) itemView.findViewById(R.id.comment_subject_profile);
            comment_subject= (TextView) itemView.findViewById(R.id.comment_subject_name);
            comment_object= (TextView) itemView.findViewById(R.id.comment_object);
            comment_text= (TextView) itemView.findViewById(R.id.comment_text);
            comment_time= (TextView) itemView.findViewById(R.id.comment_time);
            comt_like_count= (TextView) itemView.findViewById(R.id.comment_like_txt);
            comtlike= (CheckBox) itemView.findViewById(R.id.comment_like);


        }
    }


}
