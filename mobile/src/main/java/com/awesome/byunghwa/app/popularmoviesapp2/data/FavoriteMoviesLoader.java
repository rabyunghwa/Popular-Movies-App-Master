package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Helper for loading a list of articles or a single article.
 */
public class FavoriteMoviesLoader extends CursorLoader {

    public static FavoriteMoviesLoader newAllFavoriteMoviesInstance(Context context) {
        return new FavoriteMoviesLoader(context, ItemsContract.FavoritesEntry.buildDirUri());
    }

//    public static FavoriteMoviesLoader newAllHighestRatedMoviesInstance(Context context) {
//        return new FavoriteMoviesLoader(context, ItemsContract.FavoritesEntry.buildDirUri());
//    }
//
//    public static FavoriteMoviesLoader newInstanceForItemId(Context context, long itemId) {
//        return new FavoriteMoviesLoader(context, ItemsContract.FavoritesEntry.buildItemUri(itemId));
//    }

    public static FavoriteMoviesLoader newInstanceForFavoriteId(Context context, long itemId) {
        return new FavoriteMoviesLoader(context, ItemsContract.FavoritesEntry.buildFavoritesUri(itemId));
    }

    private FavoriteMoviesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, ItemsContract.FavoritesEntry.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract.FavoritesEntry._ID,
                ItemsContract.FavoritesEntry.MOVIE_ID,
                ItemsContract.FavoritesEntry.VOTE_AVERAGE,
                ItemsContract.FavoritesEntry.POSTER_THUMBNAIL,
                ItemsContract.FavoritesEntry.RELEASE_DATE,
                ItemsContract.FavoritesEntry.OVERVIEW,
                ItemsContract.FavoritesEntry.ORIGINAL_TITLE
        };

        String COL_NAME_ID = ItemsContract.FavoritesEntry._ID;
        String COL_NAME_MOVIE_ID = ItemsContract.FavoritesEntry.MOVIE_ID;
        String COL_NAME_VOTE_AVERAGE = ItemsContract.FavoritesEntry.VOTE_AVERAGE;
        String COL_NAME_POSTER_THUMBNAIL = ItemsContract.FavoritesEntry.POSTER_THUMBNAIL;
        String COL_NAME_RELEASE_DATE = ItemsContract.FavoritesEntry.RELEASE_DATE;
        String COL_NAME_OVERVIEW = ItemsContract.FavoritesEntry.OVERVIEW;
        String COL_NAME_ORIGINAL_TITLE = ItemsContract.FavoritesEntry.ORIGINAL_TITLE;

        int _ID = 0;
        int MOVIE_ID = 1;
        int VOTE_AVERAGE = 2;
        int POSTER_THUMBNAIL = 3;
        int RELEASE_DATE = 4;
        int OVERVIEW = 5;
        int ORIGINAL_TITLE = 6;
    }

}
