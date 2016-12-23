package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class TopRatedMoviesLoader extends CursorLoader {

    public static TopRatedMoviesLoader newAllTopRatedMoviesInstance(Context context) {
        return new TopRatedMoviesLoader(context, ItemsContract.HighestRatedEntry.buildDirUri());
    }

    public static TopRatedMoviesLoader newInstanceForItemId(Context context, long itemId) {
        return new TopRatedMoviesLoader(context, ItemsContract.HighestRatedEntry.buildItemUri(itemId));
    }

    private TopRatedMoviesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.HighestRatedEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.HighestRatedEntry._ID,
                ItemsContract.HighestRatedEntry.MOVIE_ID,
                ItemsContract.HighestRatedEntry.VOTE_AVERAGE,
                ItemsContract.HighestRatedEntry.POSTER_THUMBNAIL,
                ItemsContract.HighestRatedEntry.RELEASE_DATE,
                ItemsContract.HighestRatedEntry.OVERVIEW,
                ItemsContract.HighestRatedEntry.ORIGINAL_TITLE
        };

        String COL_NAME_ID = ItemsContract.HighestRatedEntry._ID;
        String COL_NAME_MOVIE_ID = ItemsContract.HighestRatedEntry.MOVIE_ID;
        String COL_NAME_VOTE_AVERAGE = ItemsContract.HighestRatedEntry.VOTE_AVERAGE;
        String COL_NAME_POSTER_THUMBNAIL = ItemsContract.HighestRatedEntry.POSTER_THUMBNAIL;
        String COL_NAME_RELEASE_DATE = ItemsContract.HighestRatedEntry.RELEASE_DATE;
        String COL_NAME_OVERVIEW = ItemsContract.HighestRatedEntry.OVERVIEW;
        String COL_NAME_ORIGINAL_TITLE = ItemsContract.HighestRatedEntry.ORIGINAL_TITLE;

        int _ID = 0;
        int MOVIE_ID = 1;
        int VOTE_AVERAGE = 2;
        int POSTER_THUMBNAIL = 3;
        int RELEASE_DATE = 4;
        int OVERVIEW = 5;
        int ORIGINAL_TITLE = 6;
    }

}
