package com.awesome.byunghwa.app.mytvapplication.util;

import android.util.Log;

import com.awesome.byunghwa.app.mytvapplication.fragment.MainFragment;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class RemoteEndpointUtil {
    private static final String TAG = "RemoteEndpointUtil";

    private RemoteEndpointUtil() {
    }

    public static JSONArray fetchMovieListJsonArray(String sortOrderParameter) {
        String itemsJson;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            if (sortOrderParameter != null) {
                if (sortOrderParameter.equals(MainFragment.VALUE_SORT_ORDER_POPULARITY)) {
                    request = new Request.Builder()
                            .url(Config.BASE_URL_POPULARITY)
                            .build();
                } else if (sortOrderParameter.equals(MainFragment.VALUE_SORT_ORDER_VOTE_AVERAGE)) {
                    request = new Request.Builder()
                            .url(Config.BASE_URL_VOTE_AVERAGE)
                            .build();
                }
            }

            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException("Movie List Unexpected code " + response);
            }

            itemsJson = response.body().string();
            LogUtil.log_i(TAG, "Movie List Result Json String: " + itemsJson);
        } catch (IOException e) {
            Log.e(TAG, "Movie List Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            if (!itemsJson.isEmpty()) {
                // first turn json string to a jsonobject
                LogUtil.log_i(TAG, "Movie List ItemsJson: " + itemsJson);
                JSONObject jsonObject = new JSONObject(itemsJson);

                // since we only care about "results" so we will get the "results" part of the string
                String results = jsonObject.getString("results");
                LogUtil.log_i(TAG, "Movie List Results Part of Json: " + results);

                return new JSONArray(results);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Movie List Error parsing items JSON", e);
        }

        return null;
    }

    public static JSONArray fetchTrailerListJsonArray(long movieId) {
        String itemsJson;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            URL reviewUrl = new URL("http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=YOUR_API_KEY");

            if (movieId >= 0) {
                request = new Request.Builder()
                        .url(reviewUrl)
                        .build();
            }

            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException("Trailer List Unexpected code " + response);
            }

            itemsJson = response.body().string();
            LogUtil.log_i(TAG, "Trailer List Result Json String: " + itemsJson);
        } catch (IOException e) {
            Log.e(TAG, "Trailer List Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            if (!itemsJson.isEmpty()) {
                // first turn json string to a jsonobject
                LogUtil.log_i(TAG, "Trailer List ItemsJson: " + itemsJson);
                JSONObject jsonObject = new JSONObject(itemsJson);

                // since we only care about "results" so we will get the "results" part of the string
                String results = jsonObject.getString("results");
                //Trailer List Results Part of Json: [{"id":"543391a80e0a265834006c5a","iso_639_1":"en","key":"1t0A_tZGrYw","name":"Inside Out US Teaser Trailer","site":"YouTube","size":720,"type":"Teaser"},{"id":"54a954299251414d5d004843","iso_639_1":"en","key":"_MC3XuMvsDI","name":"Inside Out Trailer 2","site":"YouTube","size":1080,"type":"Trailer"}]
                LogUtil.log_i(TAG, "Trailer List Results Part of Json: " + results);

                return new JSONArray(results);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Trailer List Error parsing items JSON", e);
        }

        return null;
    }

}
