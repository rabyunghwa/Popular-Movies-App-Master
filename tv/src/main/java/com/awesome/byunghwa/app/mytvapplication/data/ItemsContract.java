package com.awesome.byunghwa.app.mytvapplication.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.awesome.byunghwa.app.mytvapplication.util.LogUtil;


public class ItemsContract {
	public static final String CONTENT_AUTHORITY = "com.awesome.byunghwa.app.mytvapplication";
	public static final Uri BASE_URI = Uri.parse("content://com.awesome.byunghwa.app.mytvapplication");

	public static final String PATH_POPULARITY = "popularity";
	public static final String PATH_VOTE_AVERAGE = "vote_average";
	public static final String PATH_FAVORITES = "favorites";

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
	//public static final String RELEASE_DATE = "release_date";
	/** Type: TEXT NOT NULL */

	private ItemsContract() {
	}

	public static final class PopularityEntry implements BaseColumns {

		public static final String TABLE_NAME = "popularity";

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_POPULARITY).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.awesome.byunghwa.app.mytvapplication.popularity";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.awesome.byunghwa.app.mytvapplication.popularity";


		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long movie_id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, movie_id);
			LogUtil.log_i("Popularity Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/items/214052
			return uri;
		}

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
	}

	public static final class VoteEntry implements BaseColumns {

		public static final String TABLE_NAME = "vote_average";

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_VOTE_AVERAGE).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.awesome.byunghwa.app.mytvapplication.vote_average";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.awesome.byunghwa.app.mytvapplication.vote_average";


		/** Matches: /items/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		/** Matches: /items/[_id]/ */
		public static Uri buildItemUri(long movie_id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, movie_id);
			LogUtil.log_i("Vote average Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/items/214052
			return uri;
		}

		/** Read item ID item detail URI. */
		public static long getItemId(Uri itemUri) {
			return Long.parseLong(itemUri.getPathSegments().get(1));
		}
	}

	/* Inner class that defines the table contents of the weather table */
	public static final class FavoritesEntry implements BaseColumns {

		public static final String TABLE_NAME = "favorites";

		public static final Uri CONTENT_URI =
				BASE_URI.buildUpon().appendPath(PATH_FAVORITES).build();

		public static final String CONTENT_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;
		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITES;

		//public static final String DEFAULT_SORT = VOTE_AVERAGE + " DESC";

		/** Matches: /favorites/ */
		public static Uri buildDirUri() {
			return BASE_URI.buildUpon().appendPath(TABLE_NAME).build();
		}

		public static Uri buildFavoritesUri(long id) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, id);
			LogUtil.log_i("Favorites Contract URI", "uri: " + uri); // Items Contract URI﹕ uri: content://com.awesome.byunghwa.app.popularmoviesapp2/items/214052
			return uri;
		}
	}
}
