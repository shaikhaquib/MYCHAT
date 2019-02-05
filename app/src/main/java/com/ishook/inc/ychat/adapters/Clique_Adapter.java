package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Chat_Room;
import com.ishook.inc.ychat.activitys.Member_list;
import com.ishook.inc.ychat.list.Cliquelist;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 06-01-2018.
 */

public class Clique_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<Cliquelist> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public Clique_Adapter(Context context, List<Cliquelist> mData) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mData;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.clique_adapter, parent, false);
        CliqueHolder holder = new CliqueHolder(view);
        return holder;    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final CliqueHolder cliqueHolder=(CliqueHolder)holder;
        final Cliquelist current=mData.get(position);

        cliqueHolder.Clique_name.setText(current.room_alias_name);
        cliqueHolder.View_member.setTag(current);
        cliqueHolder.menu.setTag(current);

        Glide.with(context).load(current.room_icon_url).into(cliqueHolder.Clique_icon);

        cliqueHolder.View_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, Member_list.class);
                i.putExtra("group_name",current.room_alias_name);
                context.startActivity(i);
            }
        });

        cliqueHolder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //display option menu
                PopupMenu popupMenu = new PopupMenu(context, cliqueHolder.menu);
                popupMenu.getMenuInflater().inflate(R.menu.groupoptions,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.memberlist){

                            Intent i=new Intent(context, Member_list.class);
                            i.putExtra("group_name",current.room_alias_name);
                            context.startActivity(i);

                        }else if (id == R.id.exitgroup){
                            new DeleteRoom(context).execute(current.UserId,current.sessionId,current.room_alias_name,current.jabber_user)    ;
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        cliqueHolder.Clique_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context, Chat_Room.class);
                intent.putExtra("sessionId",current.sessionId);
                intent.putExtra("Userid",current.UserId);
                intent.putExtra("profilepic",current.room_icon_url);
                intent.putExtra("friendName",current.room_alias_name);
                intent.putExtra("FriendId",current.room_user_id);
                context.startActivity(intent);

            }
        });

/*
        cliqueHolder.Clique_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //display option menu
                final PopupMenu popupMenu = new PopupMenu(context, cliqueHolder.Clique_icon);
                popupMenu.getMenuInflater().inflate(R.menu.delete_menu,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.item_delete){
                            // dbHelper.deleteData("" + current.Id);
                            Toast.makeText(context,"Delete",Toast.LENGTH_SHORT).show();
                            new DeleteRoom(context).execute(current.UserId,current.sessionId,current.room_alias_name,current.jabber_user);
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }

            });
*/


    }

    @Override
    public int getItemCount() {return mData.size();}

    private class CliqueHolder extends RecyclerView.ViewHolder {

        TextView Clique_name;
        ImageView Clique_icon;
        TextView View_member;
        ImageView menu;


        public CliqueHolder(View itemView) {
            super(itemView);

            Clique_icon= (ImageView) itemView.findViewById(R.id.Clique_icon);
            Clique_name= (TextView) itemView.findViewById(R.id.Clique_name);
            View_member= (TextView) itemView.findViewById(R.id.view_member);
            menu= (ImageView) itemView.findViewById(R.id.groupmenu);


        }
    }
}
