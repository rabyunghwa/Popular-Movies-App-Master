package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class PopularMoviesLoader extends CursorLoader {

    public static PopularMoviesLoader newAllPopularMoviesInstance(Context context) {
        return new PopularMoviesLoader(context, ItemsContract.PopularEntry.buildDirUri());
    }

    public static PopularMoviesLoader newInstanceForItemId(Context context, long itemId) {
        return new PopularMoviesLoader(context, ItemsContract.PopularEntry.buildItemUri(itemId));
    }

    private PopularMoviesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.PopularEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.PopularEntry._ID,
                ItemsContract.PopularEntry.MOVIE_ID,
                ItemsContract.PopularEntry.VOTE_AVERAGE,
                ItemsContract.PopularEntry.POSTER_THUMBNAIL,
                ItemsContract.PopularEntry.RELEASE_DATE,
                ItemsContract.PopularEntry.OVERVIEW,
                ItemsContract.PopularEntry.ORIGINAL_TITLE
        };

        String COL_NAME_ID = ItemsContract.PopularEntry._ID;
        String COL_NAME_MOVIE_ID = ItemsContract.PopularEntry.MOVIE_ID;
        String COL_NAME_VOTE_AVERAGE = ItemsContract.PopularEntry.VOTE_AVERAGE;
        String COL_NAME_POSTER_THUMBNAIL = ItemsContract.PopularEntry.POSTER_THUMBNAIL;
        String COL_NAME_RELEASE_DATE = ItemsContract.PopularEntry.RELEASE_DATE;
        String COL_NAME_OVERVIEW = ItemsContract.PopularEntry.OVERVIEW;
        String COL_NAME_ORIGINAL_TITLE = ItemsContract.PopularEntry.ORIGINAL_TITLE;

        int _ID = 0;
        int MOVIE_ID = 1;
        int VOTE_AVERAGE = 2;
        int POSTER_THUMBNAIL = 3;
        int RELEASE_DATE = 4;
        int OVERVIEW = 5;
        int ORIGINAL_TITLE = 6;
    }

}
