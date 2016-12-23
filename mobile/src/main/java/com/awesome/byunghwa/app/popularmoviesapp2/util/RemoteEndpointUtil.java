package com.awesome.byunghwa.app.popularmoviesapp2.util;

import android.util.Log;

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

    public static JSONArray fetchPopularMovieListJsonArray(int page) {
        String itemsJson;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            Config config = new Config(page);

            request = new Request.Builder()
                    .url(config.getBASE_URL_POPULARITY())
                    .build();

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

    public static JSONArray fetchTopRatedMovieListJsonArray(int page) {
        String itemsJson;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            Config config = new Config(page);

            request = new Request.Builder()
                    .url(config.getBASE_URL_VOTE_AVERAGE())
                    .build();


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

            URL reviewUrl = new URL("http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=YOUR_API_KEY"); // replace MY_API_KEY with your own

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
                LogUtil.log_i(TAG, "Trailer List Results Part of Json: " + results);

                return new JSONArray(results);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Trailer List Error parsing items JSON", e);
        }

        return null;
    }

    public static JSONArray fetchMovieReviewJsonArray(long movieId) {
        String itemsJson;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            URL reviewUrl = new URL("http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=YOUR_API_KEY");

            if (movieId >= 0) {
                request = new Request.Builder()
                        .url(reviewUrl)
                        .build();
            }

            Call call = client.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException("Review Unexpected code " + response);
            }

            LogUtil.log_i(TAG, "response: " + response.toString());

            itemsJson = response.body().string();
            LogUtil.log_i(TAG, "Review Result Json String: " + itemsJson);
        } catch (IOException e) {
            Log.e(TAG, "Review Error fetching items JSON", e);
            return null;
        }

        // Parse JSON
        try {
            if (!itemsJson.isEmpty()) {
                // first turn json string to a jsonobject
                LogUtil.log_i(TAG, "Review ItemsJson: " + itemsJson);
                JSONObject jsonObject = new JSONObject(itemsJson);

                // since we only care about "results" so we will get the "results" part of the string
                String results = jsonObject.getString("results");
                LogUtil.log_i(TAG, "Review Results Part of Json: " + results);

                return new JSONArray(results);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Review Error parsing items JSON", e);
        }

        return null;
    }

}
