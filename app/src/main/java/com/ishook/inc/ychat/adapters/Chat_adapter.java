package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.list.Chat_list;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 04-01-2018.
 */

public class Chat_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Chat_list> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public Chat_adapter(Context cont, List<Chat_list> data) {

        this.context = cont;
        inflater = LayoutInflater.from(context);
        this.mData = data;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_adapter, parent, false);
        ChatHolder holder=new ChatHolder(view);
        return holder;
        }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ChatHolder myViewHolder=(ChatHolder) holder;
        final Chat_list current = mData.get(position);

        myViewHolder.Send_bubble.setTag(current);
        myViewHolder.Receive_bubble.setTag(current);
        myViewHolder.Send_txt.setTag(current);
        myViewHolder.Receive_txt.setTag(current);
        myViewHolder.rec_thumb.setTag(current);
        myViewHolder.rec_preTitle.setTag(current);
        myViewHolder.rec_preDesc.setTag(current);
        myViewHolder.send_preDesc.setTag(current);
        myViewHolder.send_thumb.setTag(current);
        myViewHolder.send_preTitle.setTag(current);


      /*  myViewHolder.new_message.setTag(current);
        myViewHolder.new_bubble.setTag(current);*/

      /*  if (current.new_msg!=null) {

        }*/
      /*  myViewHolder.new_bubble.setVisibility(View.VISIBLE);
        System.out.println("md"+current.new_msg);
        myViewHolder.new_message.setText(current.new_msg);*/


        myViewHolder.Send_bubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
               alertbox.setMessage("Delete  Message");
              //  alertbox.setTitle("");
               alertbox.setIcon(R.drawable.ic_delete_black_24dp);

                alertbox.setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                new DeleteMessage(context).execute(current.sid,current.uid,current.id);
                                mData.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,mData.size());                            }
                        });
                alertbox.show();
                return true;
            }
        });



        myViewHolder.Receive_bubble.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("Delete message");
                //  alertbox.setTitle("");
                alertbox.setIcon(R.drawable.ic_delete_black_24dp);

                alertbox.setNeutralButton("Delete",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                new DeleteMessage(context).execute(current.sid,current.uid,current.id);
                                mData.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,mData.size());                            }
                        });
                alertbox.show();
                return true;
            }
        });


        if (current.sender_id.equals(current.reciver_id) ){

            myViewHolder.Send_bubble.setVisibility(View.VISIBLE);
            myViewHolder.Send_txt.setText(current.chat_body);


            //web link

            myViewHolder.send_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("THIMG",current.thumbimg);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(current.thumbUrl));
                    context.startActivity(intent);
                    Log.d("preee",current.thumbUrl);

                }

            });


            if (!current.thumbtitle.isEmpty()){
                myViewHolder.send_thumb.setVisibility(View.VISIBLE);
                //   myViewHolder.post_link.setLin\\(Html.fromHtml(current.link_preview));
                myViewHolder.send_preTitle.setText(current.thumbtitle);Log.d("preadap",current.thumbtitle);
                myViewHolder.send_preDesc.setText(current.thumbdesc);Log.d("preadap",current.thumbdesc);
              //  myViewHolder.thumburl.setText(current.thumbUrl);

                if (!current.thumbimg.isEmpty()){
                    myViewHolder.send_preimg.setVisibility(View.VISIBLE);

                    Glide.with(context).load(current.thumbimg)
                            .into(myViewHolder.send_preimg); }            }




        }
        else{
            myViewHolder.Receive_bubble.setVisibility(View.VISIBLE);
            myViewHolder.Receive_txt.setText(current.chat_body);




            //web link

            myViewHolder.rec_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("THIMG",current.thumbimg);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(current.thumbUrl));
                    context.startActivity(intent);
                }

            });


            if (!current.thumbtitle.isEmpty()){
                myViewHolder.rec_thumb.setVisibility(View.VISIBLE);
                //   myViewHolder.post_link.setLin\\(Html.fromHtml(current.link_preview));
                myViewHolder.rec_preTitle.setText(current.thumbtitle);
                myViewHolder.rec_preDesc.setText(current.thumbdesc);
                //  myViewHolder.thumburl.setText(current.thumbUrl);

                if (!current.thumbimg.isEmpty()){
                    myViewHolder.rec_preimg.setVisibility(View.VISIBLE);

                    Glide.with(context).load(current.thumbimg)
                            .into(myViewHolder.rec_preimg); }            }




        }



    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return(position);
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        TextView Send_txt;
        TextView Receive_txt;
        TextView new_message;
        TextView send_preTitle;
        TextView send_preDesc;
        ImageView send_preimg;
        TextView rec_preTitle;
        TextView rec_preDesc;

        ImageView rec_preimg;


        LinearLayout Send_bubble;
        LinearLayout Receive_bubble;
        LinearLayout new_bubble;
        LinearLayout send_thumb;
        LinearLayout rec_thumb;
        ImageView imageView;

        public ChatHolder(View itemView) {
            super(itemView);

            Send_txt= (TextView) itemView.findViewById(R.id.send_Text);
            Receive_txt=(TextView) itemView.findViewById(R.id.recive_Text);
            Receive_bubble=(LinearLayout) itemView.findViewById(R.id.recive_chat_bubble);
            Send_bubble= (LinearLayout) itemView.findViewById(R.id.send_chat_bubble);
            rec_preDesc= (TextView) itemView.findViewById(R.id.rec_preDesc);
            rec_preTitle= (TextView) itemView.findViewById(R.id.rec_preTitle);
            rec_preimg= (ImageView) itemView.findViewById(R.id.rec_preimg);
            rec_thumb= (LinearLayout) itemView.findViewById(R.id.rec_thumb);
            send_preDesc= (TextView) itemView.findViewById(R.id.send_preDesc);
            send_preTitle= (TextView) itemView.findViewById(R.id.send_preTitle);
            send_preimg= (ImageView) itemView.findViewById(R.id.send_preimg);
            send_thumb= (LinearLayout) itemView.findViewById(R.id.send_thumb);



       /*     new_message= (TextView) itemView.findViewById(R.id.new_send_Text);
            new_bubble= (LinearLayout) itemView.findViewById(R.id.newbubble);*/


        }
    }

}
