package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.awesome.byunghwa.app.popularmoviesapp2.util.GlobalConsts;
import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;
import com.awesome.byunghwa.app.popularmoviesapp2.util.RemoteEndpointUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class PopularMoviesUpdaterService extends IntentService {

    private static final String TAG = "PopularMoviesUpdaterService";

    public PopularMoviesUpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.log_i(TAG, "PopularMoviesUpdaterService Started");
        boolean refresh = intent.getBooleanExtra("refresh", false);
        int page = intent.getIntExtra("page", 1);
        boolean hasNewItems = intent.getBooleanExtra("new", false);
        LogUtil.log_i(TAG, "refresh boolean value: " + refresh);
        LogUtil.log_i(TAG, "page int value: " + page);
        LogUtil.log_i(TAG, "has new items boolean value: " + hasNewItems);

        Intent intentPopular = new Intent(GlobalConsts.POPULAR_MOVIES_FETCHED);

        Vector<ContentValues> cVVector = null;
        try {
            // Delete old items so that we dont overload the layout with same data
            Uri dirUriItemsContract = ItemsContract.PopularEntry.buildDirUri();

            if (refresh) {
                getContentResolver().delete(dirUriItemsContract, null, null);
            }

            if (hasNewItems) {
                // up till now, we have dealt with "array"
                // Insert the new weather information into the database
                JSONArray array = RemoteEndpointUtil.fetchPopularMovieListJsonArray(page);

                if (array == null) {
                    LogUtil.log_i(TAG, "failed to fetch new pop movies...");
                    // this might be due to lack of internet connection
                    intentPopular.putExtra("fetchNewItemsResult", false);
                    throw new JSONException("Invalid parsed item array");
                }

                if (array != null) {
                    cVVector = new Vector<ContentValues>(array.length());

                    LogUtil.log_i(TAG, "Json Array: " + array.toString());
                    // [{"adult":false,"backdrop_path":"\/lNHw6DxORgqEx6hhSd8J9CSTbgV.jpg","genre_ids":[18],"id":99760,"original_language":"es","original_title":"El pico 2","overview":"Paco, son of the commander of the Guardia Civil Evaristo Torrecuadrada, has been involved in Bilbao in the murder of a drug dealer couple. His fathers' efforts in suppressing evidence have nothing to do when the crime appears in the press. Paco is arrested and goes to prisson, where he return to do drugs.","release_date":"1984-11-09","poster_path":"\/bfB9Om15x6wQLfvRoEBz2KMxQ5r.jpg","popularity":0.0027,"title":"El pico 2","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":null,"genre_ids":[],"id":97032,"original_language":"en","original_title":"Trop tôt\/Trop tard","overview":"A film about the idea of \"a revolution\", shot in both France and Egypt.","release_date":"1982-02-17","poster_path":"\/cfZC0lgsziLqSa5XK04QyHiZo5g.jpg","popularity":1.000011,"title":"Too Early, Too Late","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":"\/K0AL82rZd9zvshc9B759BSXEnV.jpg","genre_ids":[],"id":98537,"original_language":"es","original_title":"El Concierto Subacuático","overview":null,"release_date":"2010-04-11","poster_path":"\/xCxq0q2yR5KTxWmZCRHlG2NMqNi.jpg","popularity":1.001004,"title":"El Concierto Subacuático","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":"\/sMmAkblJOaMeneHr8aoNTHGGSgH.jpg","genre_ids":[],"id":96156,"original_language":"en","original_title":"Arth","overview":"The semi-autobiographical film was written by Mahesh Bhatt about his extramarital relationship with actress Parveen Babi.","release_date":"1982-12-03","poster_path":"\/9mRTLkwblDUqQIxUwEzCH3sKq9Q.jpg","popularity":1.5E-5,"title":"The Meaning","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":"\/8SymP2P5hvKS16CmQhDyPpZfyK1.jpg","genre_ids":[35,18],"id":96242,"original_language":"en","original_title":"Historias de la radio","overview":"Three short stories based on radio competitions, all linked by speaker Gabriel and his fiancee. Two inventors who want to patent a piston and need money, a thief who answers a phone call while robbing and a child who needs to go to Sweden for an operation are the protagonists of these stories around the radio.","release_date":"1955-07-24","poster_path":"\/yKSR4PG30gRTEhYVGEGcyWxM90m.jpg","popularity":1.000145,"title":"Radio Stories","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":"\/i7KgL9OGxw31fq0cQnsXwQSM29X.jpg","genre_ids":[99],"id":109837,"original_language":"en","original_title":"Deep Space Explorer","overview":"Fly through the tunnel and galactic cosmic particle clouds; explore the depths of space with distant worlds, alien planets and unknown mist spirals. Dive into the subspace and discover fascinating parallel universes with mysterious beings of light and the birth of new star systems. Experience legendary and future space missions.  This high-quality state-of-the-art production is a must for every science fiction and space enthusiasts!","release_date":"2010-11-10","poster_path":"\/wtanmz940zsotDTKfS5K5DdBhwS.jpg","popularity":1.012351,"title":"Deep space explorer","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":null,"genre_ids":[35,10749],"id":112161,"original_language":"en","original_title":"6 Month Rule","overview":"A womanizer teaches his clueless friend the rules about being single and avoiding emotional attachment.","release_date":"2012-06-01","poster_path":"\/jcVhomttAiaeabeK30luUQeAlzd.jpg","popularity":1.00272,"title":"6 Month Rule","video":false,"vote_average":10,"vote_count":1},{"adult":false,"backdrop_path":null,"genre_ids":[],"id":114323,"original_language":"en","original_title":"Geheime Reichssache","overview":"This documentary chronicles the assassination attempt made on Adolph Hitler on July 20, 1944 and the subsequent trial of the conspirators. Film footage is used to lay the foundation for the failed conspiracy t

                    Cursor cursorCheck = null;
                    for (int i = 0; i < array.length(); i++) {
                        ContentValues values = new ContentValues();
                        JSONObject object = array.getJSONObject(i);

                        // Note: We CAN'T insert _id into the database
                        //values.put(ItemsContract.Items._ID, object.getString("_id" ));

                        int movieId = Integer.parseInt(object.getString("id"));
                        // to avoid repetitive insertion, its better practice to query first before inserting
                        cursorCheck = getContentResolver().query(ItemsContract.PopularEntry.buildItemUri(movieId), null, null, null, null);

                        if (!cursorCheck.moveToFirst()) {
                            values.put(PopularMoviesLoader.Query.COL_NAME_MOVIE_ID, object.getString("id"));
                            LogUtil.log_i(TAG, "inserting popular movie data movie id: " + object.getString("id"));
                            values.put(PopularMoviesLoader.Query.COL_NAME_ORIGINAL_TITLE, object.getString(PopularMoviesLoader.Query.COL_NAME_ORIGINAL_TITLE));
                            LogUtil.log_i(TAG, "inserting popular movie data original title: " + object.getString(PopularMoviesLoader.Query.COL_NAME_ORIGINAL_TITLE));
                            values.put(PopularMoviesLoader.Query.COL_NAME_OVERVIEW, object.getString(PopularMoviesLoader.Query.COL_NAME_OVERVIEW));
                            LogUtil.log_i(TAG, "inserting popular movie data overview: " + object.getString(PopularMoviesLoader.Query.COL_NAME_OVERVIEW));
                            values.put(PopularMoviesLoader.Query.COL_NAME_VOTE_AVERAGE, object.getString(PopularMoviesLoader.Query.COL_NAME_VOTE_AVERAGE));
                            LogUtil.log_i(TAG, "inserting popular movie data vote average: " + object.getString(PopularMoviesLoader.Query.COL_NAME_VOTE_AVERAGE));
                            values.put(PopularMoviesLoader.Query.COL_NAME_RELEASE_DATE, object.getString(PopularMoviesLoader.Query.COL_NAME_RELEASE_DATE));
                            LogUtil.log_i(TAG, "inserting popular movie data release date: " + object.getString(PopularMoviesLoader.Query.COL_NAME_RELEASE_DATE));
                            values.put(PopularMoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL, object.getString(PopularMoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL));
                            LogUtil.log_i(TAG, "inserting popular movie data poster thumbnail: " + object.getString(PopularMoviesLoader.Query.COL_NAME_POSTER_THUMBNAIL));
                            cVVector.add(values);
                        }

                    }
                    if ((cursorCheck != null) && !cursorCheck.isClosed()) {
                        cursorCheck.close();
                    }

                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        int result = getContentResolver().bulkInsert(ItemsContract.PopularEntry.buildDirUri(), cvArray);

                        LogUtil.log_i(TAG, "Popular Movies Bulk Insert Result: " + result);
                    }
                }
            }


        } catch (Exception e) {
            //Log.e(TAG, "Error updatin");
        }

        // send a broadcast to update refreshing ui and notify the adapter of the range of items inserted
        sendBroadcast(intentPopular);
    }
}
