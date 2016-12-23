package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.awesome.byunghwa.app.popularmoviesapp2.util.LogUtil;


public class ItemsContract {

	public static final String CONTENT_AUTHORITY = "com.awesome.byunghwa.app.popularmoviesapp2";
	public static final Uri BASE_URI = Uri.parse("content://com.awesome.byunghwa.app.popularmoviesapp2");

	public static final String PATH_POPULAR_MOVIES = "populars";
	public static final String PATH_TOP_RATED_MOVIES = "top_rated";
	public static final String PATH_MY_FAVORITE_MOVIES = "favorites";

	private ItemsContract() {
	}

	public static final class PopularEntry implements BaseColumns {

		public static final String TABLE_NAME = PATH_POPULAR_MOVIES;

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_POPULAR_MOVIES).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIES;
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_POPULAR_MOVIES;

		public static final String _ID = "_id";
		/** Type: TEXT */
		public static final String MOVIE_ID = "movie_id";
		/** Type: TEXT */
		public static final String ORIGINAL_TITLE = "original_title";
		/** Type: TEXT NOT NULL */
		public static final String POSTER_THUMBNAIL = "poster_path";
		/** Type: TEXT NOT NULL */
		public static final String OVERVIEW = "overview";
		/** Type: TEXT NOT NULL */
		public static final String VOTE_AVERAGE = "vote_average";
		/** Type: TEXT NOT NULL */
		public static final String RELEASE_DATE = "release_date";
		/** Type: TEXT NOT NULL */

		public static final String DEFAULT_SORT = VOTE_AVERAGE + " DESC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long movie_id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, movie_id);
			LogUtil.log_i("Items Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/items/214052
			return uri;
		}

//        /** Read item ID item detail URI. */
//        public static long getItemId(Uri itemUri) {
//            return Long.parseLong(itemUri.getPathSegments().get(1));
//        }
	}

	public static final class HighestRatedEntry implements BaseColumns {

		public static final String TABLE_NAME = PATH_TOP_RATED_MOVIES;

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_TOP_RATED_MOVIES).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIES;
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_RATED_MOVIES;

		public static final String _ID = "_id";
		/** Type: TEXT */
		public static final String MOVIE_ID = "movie_id";
		/** Type: TEXT */
		public static final String ORIGINAL_TITLE = "original_title";
		/** Type: TEXT NOT NULL */
		public static final String POSTER_THUMBNAIL = "poster_path";
		/** Type: TEXT NOT NULL */
		public static final String OVERVIEW = "overview";
		/** Type: TEXT NOT NULL */
		public static final String VOTE_AVERAGE = "vote_average";
		/** Type: TEXT NOT NULL */
		public static final String RELEASE_DATE = "release_date";
		/** Type: TEXT NOT NULL */

		public static final String DEFAULT_SORT = VOTE_AVERAGE + " DESC";

		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long movie_id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, movie_id);
			LogUtil.log_i("Items Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/items/214052
			return uri;
		}

//        /** Read item ID item detail URI. */
//        public static long getItemId(Uri itemUri) {
//            return Long.parseLong(itemUri.getPathSegments().get(1));
//        }
	}

	/* Inner class that defines the table contents of the weather table */
	public static final class FavoritesEntry implements BaseColumns {

		public static final String TABLE_NAME = PATH_MY_FAVORITE_MOVIES;

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_MY_FAVORITE_MOVIES).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_FAVORITE_MOVIES;
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MY_FAVORITE_MOVIES;

		public static final String _ID = "_id";
		/** Type: TEXT NOT NULL */
		public static final String MOVIE_ID = "movie_id";
		/** Type: TEXT NOT NULL */
		public static final String ORIGINAL_TITLE = "original_title";
		/** Type: TEXT NOT NULL */
		public static final String POSTER_THUMBNAIL = "poster_path";
		/** Type: TEXT NOT NULL */
		public static final String OVERVIEW = "overview";
		/** Type: TEXT NOT NULL */
		public static final String VOTE_AVERAGE = "vote_average";
		/** Type: TEXT NOT NULL */
		public static final String RELEASE_DATE = "release_date";
		/** Type: TEXT NOT NULL */

		public static final String DEFAULT_SORT = VOTE_AVERAGE + " DESC";

		/** Matches: /favorites/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		public static Uri buildFavoritesUri(long id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
			LogUtil.log_i("Favorites Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/favorites/314365
			return uri;
		}
	}
}
