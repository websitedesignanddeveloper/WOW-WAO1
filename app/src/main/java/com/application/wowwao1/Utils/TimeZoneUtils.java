package com.application.wowwao1.Utils;

import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by nct119 on 16/10/17.
 */

public class TimeZoneUtils {

    Calendar cal;
    TimeZone tz;
    String result = "";

    public String getTimeZone() {
        cal = Calendar.getInstance();
        tz = cal.getTimeZone();
        result = tz.getID();
        Log.e("Time zone", "=" + result);
        return result;
    }
}
