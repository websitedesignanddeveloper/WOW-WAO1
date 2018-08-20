package com.application.wowwao1.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class IntroManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public IntroManager(Context context)
    {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("FirstLaunch",0);
        editor = sharedPreferences.edit();
    }

    public void setFirst(boolean isFirst)
    {
        editor.putBoolean("isFirstLaunch",isFirst);
        editor.commit();
    }

    public boolean getFirst()
    {
        return sharedPreferences.getBoolean("isFirstLaunch",true);
    }
}
