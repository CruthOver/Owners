package com.wiradipa.fieldOwners.ApiHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {

    public final static String SHAREDKEY = "LapanganBola-sessionOwner";
    public final static String TOKEN = "token";
    public final static String USERNAME = "username";
    public final static String PHONE_NUMBER = "phone_number";
    public final static String EMAIL = "email";
    public final static String NAME = "name";
    public final static String OWNERID = "userid";
    public final static String PHOTOURL = "photo_url";


    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AppSession(Context mContext){
        sharedPreferences = mContext.getSharedPreferences(SHAREDKEY, Context.MODE_PRIVATE);
    }

    public void clearSession(){
        editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public String getData(String data){
        return sharedPreferences.getString(data, null);
    }

    public String getData(String data, String defaultValue){
        return sharedPreferences.getString(data, defaultValue);
    }

    public void setData(String data, String value){
        editor = sharedPreferences.edit();
        editor.putString(data, value);
        editor.apply();
    }

    public void deleteData(String data){
        editor = sharedPreferences.edit();
        editor.remove(data);
        editor.apply();
    }

    public void createSession(String token, String username, String name, String email, String phoneNumber, String ownerId, String photoUrl){
        editor = sharedPreferences.edit();
        editor.putString(TOKEN, token);
        editor.putString(USERNAME, username);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHONE_NUMBER, phoneNumber);
        editor.putString(OWNERID, ownerId);
        editor.putString(PHOTOURL, photoUrl);
        editor.apply();
    }

    public Boolean isLogin(){
        return (sharedPreferences.getString(TOKEN, null) != null);
    }
}