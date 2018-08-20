package com.application.wowwao1.Application;

import android.app.Application;

import com.application.wowwao1.R;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by nct119 on 11/8/17.
 */

public class ApplicationController extends Application {

    public ArrayList arrayList = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
