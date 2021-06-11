package com.ishook.inc.ychat.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.Search_Result;
import com.ishook.inc.ychat.list.SearchList;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

/**
 * Created by Shaikh on 31-01-2018.
 */

public class SearchAdapt extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    List<SearchList> mData= Collections.emptyList();
    private LayoutInflater inflater;

    public SearchAdapt(Context context, List<SearchList> mData) {

        this.context = context;
        inflater = LayoutInflater.from(context);
        this.mData = mData;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sugestion, parent, false);
        SearchHolder  holder = new SearchHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SearchHolder  myViewHolder=(SearchHolder)holder;
        final SearchList current = mData.get(position);

        myViewHolder.UserName.setText(current.name);
        Glide.with(context).load(current.dp).into(myViewHolder.Dp);
        myViewHolder.panel.setTag(current);
        myViewHolder.panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Search_Result.class);
                intent.putExtra("search_data",current.id);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {return mData.size();}

    private class SearchHolder extends RecyclerView.ViewHolder {

        TextView UserName;
        ImageView Dp;
        LinearLayout panel;


        public SearchHolder(View itemView) {
            super(itemView);

            Dp= (ImageView) itemView.findViewById(R.id.srdp);
            UserName= (TextView) itemView.findViewById(R.id.srname);
            panel=(LinearLayout)itemView.findViewById(R.id.searchpanel);


        }
    }
}

