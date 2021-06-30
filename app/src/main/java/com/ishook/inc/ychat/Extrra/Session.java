package com.ishook.inc.ychat.Extrra;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.toolbox.StringRequest;


public class Session {


    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;

    public static String UserName;
    public static String Pass;
    public static String dID;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("Yochat", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }
    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }
}


