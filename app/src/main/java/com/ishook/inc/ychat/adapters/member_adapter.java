package com.ishook.inc.ychat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.list.member_list;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 11-01-2018.
 */

public class member_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    List<member_list> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public member_adapter(Context context, List<member_list> mdata) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mdata;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.member_adapter, parent, false);
        MemberHolder holder = new MemberHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final MemberHolder myViewHolder=(MemberHolder) holder;
        final member_list current = mData.get(position);

       myViewHolder.membername.setText(current.jabber_user);
        myViewHolder.role.setText(current.role);
        myViewHolder.layout.setTag(current);
        myViewHolder.imageView.setTag(current);

        if (current.User_Role.equals("owner")){
            myViewHolder.imageView.setVisibility(View.VISIBLE);
        }
        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //display option menu
                PopupMenu popupMenu = new PopupMenu(context, myViewHolder.imageView);
                popupMenu.getMenuInflater().inflate(R.menu.option_menu,popupMenu.getMenu());


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.item_delete){
                           // dbHelper.deleteData("" + current.Id);
                           // Toast.makeText(context,"Delete",Toast.LENGTH_SHORT).show();
                        }else if (id == R.id.item_admin){
                            Toast.makeText(context,"Admin",Toast.LENGTH_SHORT).show();
                            new MakeAdmin(context).execute(current.UserId,current.SessionId,current.room_name,current.jabber_user,current.Admin);
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });



    }


    @Override
    public int getItemCount() {
        return mData.size();
    }
    private class MemberHolder extends RecyclerView.ViewHolder{

        TextView membername;
        TextView role;
        LinearLayout layout;
        ImageView imageView;

        public MemberHolder(View itemView) {
            super(itemView);

            membername= (TextView) itemView.findViewById(R.id.member_name);
            role= (TextView) itemView.findViewById(R.id.role);
            layout= (LinearLayout) itemView.findViewById(R.id.member_layout);
            imageView= (ImageView) itemView.findViewById(R.id.option_menu);

        }
    }

}
