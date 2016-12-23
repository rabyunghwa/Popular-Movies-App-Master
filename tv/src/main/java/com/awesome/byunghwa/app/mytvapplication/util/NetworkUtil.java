package com.awesome.byunghwa.app.mytvapplication.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ByungHwa on 7/15/2015.
 */
public class NetworkUtil {

    //public static final String ACTION_NETWORK_REESTABLISHED = "com.awesome.byunghwa.app.popularmoviesapp.ACTION_NETWORK_RECONNECTED";

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && (networkInfo.isConnectedOrConnecting());
    }
}
