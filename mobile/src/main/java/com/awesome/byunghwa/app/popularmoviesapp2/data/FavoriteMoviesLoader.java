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

    }

}
