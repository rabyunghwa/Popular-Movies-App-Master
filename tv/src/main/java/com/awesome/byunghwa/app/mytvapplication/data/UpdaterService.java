package com.awesome.byunghwa.app.mytvapplication.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.awesome.byunghwa.app.mytvapplication.fragment.MainFragment;
import com.awesome.byunghwa.app.mytvapplication.util.LogUtil;
import com.awesome.byunghwa.app.mytvapplication.util.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.log_i(TAG, "UpdaterService Started");

        String sortOrder = intent.getStringExtra(MainFragment.KEY_SORT_ORDER);

        LogUtil.log_i(TAG, "sort order: " + sortOrder);

        try {
            JSONArray array  = RemoteEndpointUtil.fetchMovieListJsonArray(sortOrder);

            Uri dirUriContract = null;

            // Delete old items so that we dont overload the layout with same data
            if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_POPULARITY)) {
                dirUriContract = ItemsContract.PopularityEntry.buildDirUri();
            } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_VOTE_AVERAGE)) {
                dirUriContract = ItemsContract.VoteEntry.buildDirUri();
            }
            if (dirUriContract != null) {
                getContentResolver().delete(dirUriContract, null, null);
            }

            // up till now, we have dealt with "array"
            // Insert the new weather information into the database
            if (array != null) {
                Vector<ContentValues> cVVector = new Vector<ContentValues>(array.length());

                LogUtil.log_i(TAG, "Json Array: " + array.toString());

                for (int i = 0; i < array.length(); i++) {
                    ContentValues values = new ContentValues();
                    JSONObject object = array.getJSONObject(i);

                    // Note: We CAN'T insert _id into the database
                    //values.put(ItemsContract.Items._ID, object.getString("_id" ));

                    values.put(MoviesLoader.Query.COL_NAME_MOVIE_ID, object.getString("id"));
                    LogUtil.log_i(TAG, "inserting movie data movie id: " + object.getString("id"));
                    values.put(MoviesLoader.Query.COL_NAME_ORIGINAL_TITLE, object.getString(MoviesLoader.Query.COL_NAME_ORIGINAL_TITLE));
                    LogUtil.log_i(TAG, "inserting movie data original title: " + object.getString(MoviesLoader.Query.COL_NAME_ORIGINAL_TITLE));
                    values.put(MoviesLoader.Query.COL_NAME_OVERVIEW, object.getString(MoviesLoader.Query.COL_NAME_OVERVIEW));
                    LogUtil.log_i(TAG, "inserting movie data overview: " + object.getString(MoviesLoader.Query.COL_NAME_OVERVIEW));
                    values.put(MoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL, object.getString(MoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL));
                    LogUtil.log_i(TAG, "inserting movie data poster thumbnail: " + object.getString(MoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL));
                    cVVector.add(values);
                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    int result = 0;
                    if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_POPULARITY)) {
                        result = getContentResolver().bulkInsert(ItemsContract.PopularityEntry.buildDirUri(), cvArray);
                    } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_VOTE_AVERAGE)){
                        result = getContentResolver().bulkInsert(ItemsContract.VoteEntry.buildDirUri(), cvArray);
                    }

                    LogUtil.log_i(TAG, "Bulk Insert Result: " + result);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating content.", e);
        }

    }
}
