package com.ishook.inc.ychat.fragments;

import static com.ishook.inc.ychat.app.MainActivity.groupsName;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ishook.inc.ychat.Extrra.Constants;
import com.ishook.inc.ychat.Global;
import com.ishook.inc.ychat.R;
import com.ishook.inc.ychat.activitys.CreateGroup;
import com.ishook.inc.ychat.adapters.Clique_Adapter;
import com.ishook.inc.ychat.adapters.YchatTabHolder;
import com.ishook.inc.ychat.app.MainActivity;
import com.ishook.inc.ychat.list.Cliquelist;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.demo.TwilioApplication;
import com.twilio.chat.demo.activities.MessageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shaikh on 05-01-2018.
 */

public class TabClique extends Fragment {

    RecyclerView rvClique;
    Context cont;

    String Uid;
    String Sid;
    String Juser;
    LinearLayout no_Clique;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.tab_clique, container, false);
        cont=getActivity();
        rvClique= rootView.findViewById(R.id.rv_clique);
        rvClique.setLayoutManager(new LinearLayoutManager(getContext()));
        rvClique.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.tab_ychat_adapter, parent, false);
                YchatTabHolder holder = new YchatTabHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
                YchatTabHolder myViewHolder=(YchatTabHolder)holder;
                myViewHolder.uname.setText(groupsName.get(position).getFriendlyName().substring(3));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Loading..");
                        progressDialog.show();
                        TwilioApplication.Companion.getInstance().getBasicClient().getChatClient().getChannels().getChannel(groupsName.get(position).getSid(), new CallbackListener<Channel>() {
                            @Override
                            public void onSuccess(final Channel channel) {
                                progressDialog.dismiss();
                                startActivity(new Intent(getContext(), MessageActivity.class).putExtra("com.twilio.chat.Channel",channel).putExtra("C_SID", channel.getSid()).putExtra("userName", Global.userid).putExtra("group",channel.getFriendlyName()));
                            }
                        });
                    }
                });
            }

            @Override
            public int getItemCount() {
                return groupsName.size();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.CreateGroup:
                startActivity(new Intent(cont, CreateGroup.class));
                return true;


        }
        return super.onOptionsItemSelected(item);
    }
}
