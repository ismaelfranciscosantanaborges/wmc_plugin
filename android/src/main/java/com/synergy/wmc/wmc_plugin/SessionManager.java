package com.synergy.wmc.wmc_plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    //SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "wmc";
    private static final String WIFI = "sessionWifi";
    private static final String MOBILE = "sessionMobile";
    private static final String APIKEY = "Apikey";

    private SharedPreferences preferences;

    public SessionManager(Context context) {
        this.context = context;

        preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setSession(Session session) {

        Gson gson = new Gson();
        String json = gson.toJson(session);

        // Put (all puts are automatically committed)
        editor.putString(session.getTransport().name(), json);
        editor.commit();
    }


    public Session getSession(Session.Transport transport) {

        Gson gson = new Gson();
        String json = preferences.getString(transport.name(),"");
        if(json.isEmpty()){
            return null;
        }
        Session obj = gson.fromJson(json, Session.class);
        //Verfica si session es valida

        long created = obj.getCreated() + (obj.getExpire_seconds() * 1000);

        long now = new Date().getTime();

        // Verifica que sea una sesion valida
        if (now > created){
            return null;
        }
        return obj;
    }

    public void setApiKey(String apiKey){
        editor.putString(APIKEY, apiKey);
        editor.commit();
    }

    public String getApiKey(){
        return preferences.getString(APIKEY,"");
    }
}
