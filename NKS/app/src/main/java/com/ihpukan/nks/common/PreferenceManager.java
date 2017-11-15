package com.ihpukan.nks.common;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private static final int INTEGER_DEFAULT_VALUE = -1;
    private static final String STRING_DEFAULT_VALUE = "";
    private static final boolean BOOLEAN_DEFAULT_VALUE = false;

    private static SharedPreferences sSharedPreferences;
    private static PreferenceManager INSTANCE;

    private PreferenceManager(Context context) {
        if (sSharedPreferences == null) {
            sSharedPreferences = context.getApplicationContext().getSharedPreferences(FieldName.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        }
    }

    public static PreferenceManager get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PreferenceManager(context);
        }
        return INSTANCE;
    }

    private void putString(String name, String value) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public String getString(String name) {
        return sSharedPreferences.getString(name, STRING_DEFAULT_VALUE);
    }

    private void putInt(String name, int value) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public int getInt(String name) {
        return sSharedPreferences.getInt(name, INTEGER_DEFAULT_VALUE);
    }

    private void putBoolean(String name, boolean value) {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public boolean getBoolean(String name) {
        return sSharedPreferences.getBoolean(name, BOOLEAN_DEFAULT_VALUE);
    }

    public void saveToken(String token) {
        putString(FieldName.ACCESS_TOKEN, token);
    }

    public boolean hasToken() {
        return !getToken().equals(STRING_DEFAULT_VALUE);
    }

    public String getToken() {
        return getString(FieldName.ACCESS_TOKEN);
    }

    public void clearData() {
        SharedPreferences.Editor editor = sSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private interface FieldName {

        String SHARED_PREFERENCES_NAME = "nks_settings";
        String ACCESS_TOKEN = ""; //access_token

    }

}
