package com.awesome.byunghwa.app.mytvapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.awesome.byunghwa.app.mytvapplication.R;


/**
 * Created by ByungHwa on 7/15/2015.
 */
public class SharedPrefsUtil {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_movie_sort_order_key),
                context.getString(R.string.pref_movie_sort_order_default));
    }
}
