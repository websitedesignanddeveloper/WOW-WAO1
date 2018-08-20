package com.application.wowwao1.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class PrefsUtil {

    private static final int DEFAULT_INT = 0;
    private static final String DEFAULT_STRING = "";
    private static final float DEFAULT_FLOAT = -1f;
    private static final boolean DEFAULT_BOOLEAN = false;

    private static SharedPreferences sharedPreferences;
    private static PrefsUtil prefsUtil;

    private PrefsUtil(@NonNull Context mContext) {
        if (sharedPreferences == null) {
            sharedPreferences = mContext.getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        }
    }

    public static PrefsUtil with(@NonNull Context context) {
        if (prefsUtil == null) {
            prefsUtil = new PrefsUtil(context);
        }
        return prefsUtil;
    }

    public void write(String name, int number) {
        sharedPreferences.edit().putInt(name, number).apply();
    }

    public void write(String name, String str) {
        sharedPreferences.edit().putString(name, str).apply();
    }

    public void write(String name, float number) {
        sharedPreferences.edit().putFloat(name, number).apply();
    }

    public void write(String name, boolean bool) {
        sharedPreferences.edit().putBoolean(name, bool).apply();
    }

    public int readInt(String name) {
        return sharedPreferences.getInt(name, DEFAULT_INT);
    }

    public String readString(String name) {
        return sharedPreferences.getString(name, DEFAULT_STRING);
    }

    public float readFloat(String name) {
        return sharedPreferences.getFloat(name, DEFAULT_FLOAT);
    }

    public boolean readBoolean(String name) {
        return sharedPreferences.getBoolean(name, DEFAULT_BOOLEAN);
    }

    public void clearPrefs() {
        sharedPreferences.edit().clear().commit();
    }
}