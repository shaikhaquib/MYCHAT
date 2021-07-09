package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Comment;
import com.ishook.inc.ychat.activitys.Multiple_Image_slide;
import com.ishook.inc.ychat.activitys.Upload_New_Wire;
import com.ishook.inc.ychat.list.Wire_list;
import com.ishook.inc.ychat.list.upload_text;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Akshay on 12-09-2017.
 */

public class WiredAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;



    // Pattern for recognizing a URL, based off RFC 3986

    upload_text header;
    String strurl;


    private Context context;
    List<Wire_list> mData= Collections.emptyList();
    private LayoutInflater inflater;
    Wire_list current;
    int currentPos=0;
    public WiredAdapter(Context context, final List<Wire_list> data,upload_text header) throws JSONException {
        this.context=context;
        this.header = header;
        inflater = LayoutInflater.from(context);
        this.mData = data;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_wire_text, parent, false);
            return new VHHeader(v);
        } else if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.post, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof VHHeader)
        {
            VHHeader VHheader = (VHHeader)holder;
            VHheader.txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, Upload_New_Wire.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }
        else if(holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            final Wire_list current = mData.get(position-1);
            //myViewHolder.subject_profile.setImageResource(Integer.parseInt(current.subjectprofile));

            //seting text into textview

                myViewHolder.subject_name.setText(current.sujectname);
            myViewHolder.object_name.setText(current.objectname);
            myViewHolder.uplode_time.setText(current.uplodetime + "ago");
            myViewHolder.subject.setText("@" + current.Subject);
            myViewHolder.wired_text.setTag(current);
            myViewHolder.textlike.setTag(current);
            myViewHolder.textcomment.setTag(current);
            myViewHolder.like.setTag(current);
            myViewHolder.comment.setTag(current);
            myViewHolder.share.setTag(current);
            myViewHolder.post_link.setTag(current);
            myViewHolder.multiple_image_sign.setTag(current);
            myViewHolder.deletmenu.setTag(current);
            myViewHolder.layout.setTag(current);
            myViewHolder.linearLayout.setTag(current);





/*
            if (current.userid.equals(current.subjectid)){
                myViewHolder.deletmenu.setVisibility(View.GONE);
            }
*/
            //Delete Menu

            myViewHolder.deletmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //display option menu
                    PopupMenu popupMenu = new PopupMenu(context, myViewHolder.deletmenu);
                    popupMenu.getMenuInflater().inflate(R.menu.delete_menu,popupMenu.getMenu());

                    Menu popup=popupMenu.getMenu();
                    MenuItem menuItem= popup.findItem(R.id.item_delete);
                    MenuItem menuItem1=popup.findItem(R.id.item_reportAbuse);

                    if (!current.userid.equals(current.subjectid)){
                        menuItem.setVisible(false);
                    }else {
                        menuItem.setVisible(true);
                    }
                    if (!current.userid.equals(current.subjectid)){
                        menuItem1.setVisible(true);
                    }else {
                        menuItem1.setVisible(false);

                    }

                    //MenuItem menuItem=popupMenu.

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {



                            int id = item.getItemId();
                            if (id == R.id.item_delete){

                                new DeleteWire(context).execute(current.userid,current.sessionid,current.ActionId);
                                mData.remove(position-1);
                                notifyItemRemoved(position-1);
                                notifyItemRangeChanged(position-1,getItemCount());



                            }else if (id == R.id.item_reportAbuse){

                                LayoutInflater li = LayoutInflater.from(context);
                                 View dialogView = li.inflate(R.layout.custom_dialog, null);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        context);
                                // set title
                                alertDialogBuilder.setTitle("Report Abuse");
                                alertDialogBuilder.setView(dialogView);
                                final EditText userInput = (EditText) dialogView
                                        .findViewById(R.id.et_input);

                                final String[] reson = new String[1];
                                final Button Reprt= (Button) dialogView.findViewById(R.id.reportldialog);
                                Button Cancel= (Button) dialogView.findViewById(R.id.canceldialog);


                                reson[0] = String.valueOf(userInput.getText());

                                userInput.addTextChangedListener(new TextWatcher() {

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {



                                        if(s.toString().trim().length()==0){
                                            Reprt.setVisibility(View.GONE);
                                        } else {
                                            Reprt.setVisibility(View.VISIBLE);
                                        }


                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                  int after) {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                        // TODO Auto-generated method stub

                                    }
                                });






                                // create alert dialog
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();

                                Reprt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        new ReportAbuse().execute(current.userid,current.sessionid,current.ActionId,"wires",reson.toString());
                                        mData.remove(position-1);
                                        notifyItemRemoved(position-1);
                                        notifyItemRangeChanged(position-1,getItemCount());
                                        alertDialog.dismiss();
                                    }
                                });


                                Cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();                                    }
                                });


                            }
                            return true;
                        }
                    });
                    popupMenu.show();



                }
            });



            //web link

            if (current.thumbUrl.isEmpty()){
                System.out.println("Khali hai");
            }else {

            myViewHolder.post_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    List<String> extractedUrls = extractUrls(current.wiredtext);

                    for (String url : extractedUrls)
                    {
                      strurl=url;
                        System.out.println(url);
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(strurl));
                    context.startActivity(i);
                  /*  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(current.thumbUrl));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    System.out.println(strurl);
                    context.startActivity(intent);*/

                }

            });}

            // load image into imageview using glide

            Glide.with(context).load(current.subject_dp)
                    .into(myViewHolder.subject_profile);
            Glide.with(context).load(Global.HostName+current.wiredimage)
                    .into(myViewHolder.wired_image);

            if (myViewHolder.wired_image.getDrawable() != null){
                //Image doesn´t exist.
                myViewHolder.post_link.setVisibility(View.VISIBLE);
                myViewHolder.post_link.setBackgroundColor(Color.TRANSPARENT);
            }


            if (!current.thumbtitle.isEmpty() && myViewHolder.wired_image.getDrawable() == null){
                myViewHolder.post_link.setVisibility(View.VISIBLE);
                //   myViewHolder.post_link.setLin\\(Html.fromHtml(current.link_preview));
                myViewHolder.thumbtitle.setText(current.thumbtitle);
                myViewHolder.thumbdesc.setText(current.thumbdesc);
                myViewHolder.thumburl.setText(current.thumbUrl);
                myViewHolder.wired_image.setVisibility(View.GONE);

                if (!current.thumbimg.isEmpty()){
                    myViewHolder.thumbimg.setVisibility(View.VISIBLE);

                    Glide.with(context).load(current.thumbimg)
                            .into(myViewHolder.thumbimg); }            }






            myViewHolder.wired_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Multiple_Image_slide.class);
                    intent.putExtra("img_array", current.media);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Log.d("img_array_in_adapter", Arrays.toString(current.media)+ Arrays.toString(new int[]{current.media.length}));

                }
            });
            if(null!=myViewHolder.wired_image.getDrawable())
            {
                myViewHolder.linearLayout.setVisibility(View.GONE);
            }

            //Setting visibility of TextView Like.

            final int Likes = Integer.parseInt(current.postLikes);
            int selflikevalue = Integer.valueOf(current.SelfLikes);

            Log.d("Likes", String.valueOf(Likes));
            if (Likes != 0) {
                myViewHolder.textlike.setVisibility(View.VISIBLE);
                myViewHolder.likeview.setVisibility(View.VISIBLE);
                myViewHolder.textlike.setText(current.postLikes + " Likes");
            } else if (Likes == 1 && selflikevalue == 0) {
                myViewHolder.textlike.setVisibility(View.VISIBLE);
                myViewHolder.likeview.setVisibility(View.VISIBLE);
            }

            //Maintaning The self Like status of Like button.

            int intValue = Integer.valueOf(current.SelfLikes);
            if (intValue == 1) {
                myViewHolder.like.setChecked(true);

            } else {
                myViewHolder.like.setChecked(false);
            }

            //Setting Visibilty of Wiretext if it Empty

            if (current.wiredtext.isEmpty()) {
                myViewHolder.wired_text.setVisibility(View.GONE);
            } else {

                myViewHolder.wired_text.setVisibility(View.VISIBLE);
                myViewHolder.wired_text.setText(current.wiredtext);

            }


            //adding Listener on LIKE Button.
            //and mantaning the Colour Change Of like Button.

            myViewHolder.like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    int intValue = Integer.valueOf(current.postLikes);
                    int likeIncrement = intValue + 1;

                    int selflikevalue = Integer.valueOf(current.SelfLikes);
                    Log.d("selflike", String.valueOf(selflikevalue));


                    if (isChecked) {

                        myViewHolder.textlike.setVisibility(View.VISIBLE);
                        myViewHolder.likeview.setVisibility(View.VISIBLE);


                        if (selflikevalue == 1) {
                            myViewHolder.textlike.setText(intValue + "  Likes");
                        } else {
                            myViewHolder.textlike.setText(likeIncrement++ + "  Likes");
                        }
                    }

                    int likeDecrement = likeIncrement - 1;
                    if (!isChecked) {
                        if (Likes == 0) {
                            myViewHolder.textlike.setVisibility(View.GONE);
                            myViewHolder.likeview.setVisibility(View.GONE);
                        } else if (Likes == 0 && selflikevalue == 1) {
                            myViewHolder.textlike.setVisibility(View.GONE);
                            myViewHolder.likeview.setVisibility(View.GONE);
                        } else if (Likes == 1 && selflikevalue == 1) {
                            myViewHolder.textlike.setVisibility(View.GONE);
                            myViewHolder.likeview.setVisibility(View.GONE);
                        }
                        if (selflikevalue == 1) {
                            myViewHolder.textlike.setText(likeIncrement - 2 + "  Likes");
                        } else {
                            myViewHolder.textlike.setText(likeDecrement-- + "  Likes");
                        }
                    }


                }
            });


            //Like Post on Server on click of button.

            myViewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncLike().execute(current.sessionid, current.userid, current.ActionId);
                }
            });

            //Comment Section

            int cmtcount = Integer.parseInt(current.CommentsCounts);

            if (cmtcount == 0) {

                myViewHolder.textcomment.setVisibility(View.GONE);
            } else {

                myViewHolder.textcomment.setVisibility(View.VISIBLE);
                myViewHolder.textcomment.setText(current.CommentsCounts + " Comments");

            }

            myViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Comment.class);
                    intent.putExtra("sessionId", current.sessionid);
                    intent.putExtra("UserId", current.userid);
                    intent.putExtra("action_id", current.ActionId);
                    context.startActivity(intent);


                }
            });

            //

            myViewHolder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                    // Add data to the intent, the receiving app will decide
                    // what to do with it.
                    share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
                    share.putExtra(Intent.EXTRA_TEXT, "https://ishook.com");
                    context.startActivity(Intent.createChooser(share, "Share link!"));
                }
            });

        }


    }



    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mData.size()+1;
    }
    public List<Wire_list> getItems() {
        return mData;
    }

    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    class VHHeader extends RecyclerView.ViewHolder{
        TextView txtTitle;
        public VHHeader(View itemView) {
            super(itemView);
            this.txtTitle = (TextView) itemView.findViewById(R.id.text_uopload);
        }


    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView subject_profile;
        TextView subject_name;
        TextView object_name;
        TextView uplode_time;
        TextView subject;
        TextView wired_text;
        ImageView wired_image;
        CheckBox like;
        TextView wire;
        TextView comment;
        View likeview;
        ImageView share;
        TextView textlike;
        TextView textcomment;
        LinearLayout post_link;
        ImageView thumbimg;
        TextView thumbtitle;
        TextView thumbdesc;
        ImageView multiple_image_sign;
        TextView thumburl;
        LinearLayout layout;
        ImageView deletmenu;
        Wire_list current;int position;
        LinearLayout linearLayout;

        public MyViewHolder(View view) {
            super(view);
            subject_profile = (ImageView) itemView.findViewById(R.id.subject_profile);
            subject_name = (TextView) itemView.findViewById(R.id.subject_name);
            object_name = (TextView) itemView.findViewById(R.id.wired_sub);
            uplode_time = (TextView) itemView.findViewById(R.id.post_time);
            subject  = (TextView) itemView.findViewById(R.id.subject);
            wired_text = (TextView) itemView.findViewById(R.id.post_text);
            likeview=itemView.findViewById(R.id.likeview);

            textlike= (TextView) itemView.findViewById(R.id.textlike);
            textcomment= (TextView) itemView.findViewById(R.id.textComment);

            wired_image = (ImageView) itemView.findViewById(R.id.post_image);
            multiple_image_sign=(ImageView)itemView.findViewById(R.id.multiple_image);
            like = (CheckBox) itemView.findViewById(R.id.like);
            wire = (TextView) itemView.findViewById(R.id.wire);
            comment  = (TextView) itemView.findViewById(R.id.comment);
            share = (ImageView) itemView.findViewById(R.id.share);

            post_link= (LinearLayout) itemView.findViewById(R.id.post_link);
            thumbimg= (ImageView) itemView.findViewById(R.id.thumbimg);
            thumbtitle= (TextView) itemView.findViewById(R.id.thumbtitle);
            thumbdesc= (TextView) itemView.findViewById(R.id.thumbdesc);
            thumburl=(TextView)itemView.findViewById(R.id.thumburl);

            linearLayout= (LinearLayout) itemView.findViewById(R.id.linkp);

            layout= itemView.findViewById(R.id.wire_layout);
            deletmenu= (ImageView) itemView.findViewById(R.id.deletewire);

        }
    }
}
