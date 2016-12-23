package com.awesome.byunghwa.app.mytvapplication.util;

import android.util.Log;

/**
 * Created by ByungHwa on 4/19/2015.
 */
public class LogUtil {

    static boolean isReleased = false;

    public static boolean DEVELOPER_MODE = false;

    // message type set to Object so that we can handle
    // all types of messages
    public static void log_i(String tag, Object msg) {
        if (isReleased) {
            return;
        }
        Log.i(tag, String.valueOf(msg));
    }

    public static void log_e(String tag, Object msg) {
        if (isReleased) {
            return;
        }
        Log.e(tag, String.valueOf(msg));
    }
}
