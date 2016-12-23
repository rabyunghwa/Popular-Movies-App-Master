package com.awesome.byunghwa.app.mytvapplication.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.awesome.byunghwa.app.mytvapplication.fragment.MainFragment;

/**
 * Helper for loading a list of movies or a single movie.
 */
public class MoviesLoader extends CursorLoader {

    public static MoviesLoader newAllMoviesInstance(Context context, String sortOrder) {
        if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_POPULARITY)) {
            return new MoviesLoader(context, ItemsContract.PopularityEntry.buildDirUri());
        } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_VOTE_AVERAGE)) {
            return new MoviesLoader(context, ItemsContract.VoteEntry.buildDirUri());
        } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_FAVORITES)) {
            return new MoviesLoader(context, ItemsContract.FavoritesEntry.buildDirUri());
        }
        return null;
    }

    public static MoviesLoader newInstanceForItemId(Context context, String sortOrder, long itemId) {
        if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_POPULARITY)) {
            return new MoviesLoader(context, ItemsContract.PopularityEntry.buildItemUri(itemId));
        } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_VOTE_AVERAGE)) {
            return new MoviesLoader(context, ItemsContract.VoteEntry.buildItemUri(itemId));
        } else if (sortOrder.equals(MainFragment.VALUE_SORT_ORDER_FAVORITES)) {
            return new MoviesLoader(context, ItemsContract.FavoritesEntry.buildFavoritesUri(itemId));
        }
        return null;
    }

//    public static MoviesLoader newInstanceForFavoriteId(Context context, long itemId) {
//        return new MoviesLoader(context, ItemsContract.FavoritesEntry.buildFavoritesUri(itemId));
//    }

    private MoviesLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, null);
    }

    public interface Query {
        String[] PROJECTION = {
                ItemsContract._ID,
                ItemsContract.MOVIE_ID,
                //ItemsContract.VOTE_AVERAGE,
                ItemsContract.POSTER_THUMBNAIL,
                //ItemsContract.RELEASE_DATE,
                ItemsContract.OVERVIEW,
                ItemsContract.ORIGINAL_TITLE
        };

        String COL_NAME_ID = ItemsContract._ID;
        String COL_NAME_MOVIE_ID = ItemsContract.MOVIE_ID;
        //String COL_NAME_VOTE_AVERAGE = ItemsContract.VOTE_AVERAGE;
        String COL_NAME_POSTER_THUMBNAIL = ItemsContract.POSTER_THUMBNAIL;
        //String COL_NAME_RELEASE_DATE = ItemsContract.RELEASE_DATE;
        String COL_NAME_OVERVIEW = ItemsContract.OVERVIEW;
        String COL_NAME_ORIGINAL_TITLE = ItemsContract.ORIGINAL_TITLE;

        int _ID = 0;
        int MOVIE_ID = 1;
        //int VOTE_AVERAGE = 2;
        int POSTER_THUMBNAIL = 2;
        //int RELEASE_DATE = 4;
        int OVERVIEW = 3;
        int ORIGINAL_TITLE = 4;
    }

}
