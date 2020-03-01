package com.victorengineer.limpiamiciudad.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionHandler {

    private static SessionHandler instance;
    private static SharedPreferences pref;

    public static final String ID_USER = "ID_USER";

    public static synchronized SessionHandler init(Context context){
        if(instance != null){
            return  instance;
        }

        return new SessionHandler(context);
    }

    public static synchronized SessionHandler getInstance(Context context){
        if(instance != null){
            return instance;
        }

        return init(context);
    }

    private SessionHandler(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void putIdser(String idUser, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(ID_USER, idUser).apply();
    }

    public static String getIdUser(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ID_USER, (String)null);
    }
}

