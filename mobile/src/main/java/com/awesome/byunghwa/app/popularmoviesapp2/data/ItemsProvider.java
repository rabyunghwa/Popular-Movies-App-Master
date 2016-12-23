
package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ItemsProvider extends ContentProvider {
	private SQLiteOpenHelper mOpenHelper;

	private static final int POPULARS = 0;
	private static final int POPULARS__ID = 1;

	private static final int TOP_RATED = 2;
	private static final int TOP_RATED__ID = 3;

	private static final int FAVORITES = 4;
	private static final int FAVORITES__ID = 5;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = ItemsContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, ItemsContract.PopularEntry.TABLE_NAME, POPULARS);
		matcher.addURI(authority, ItemsContract.PopularEntry.TABLE_NAME + "/#", POPULARS__ID);

		matcher.addURI(authority, ItemsContract.HighestRatedEntry.TABLE_NAME, TOP_RATED);
		matcher.addURI(authority, ItemsContract.HighestRatedEntry.TABLE_NAME + "/#", TOP_RATED__ID);

		matcher.addURI(authority, ItemsContract.FavoritesEntry.TABLE_NAME, FAVORITES);
		matcher.addURI(authority, ItemsContract.FavoritesEntry.TABLE_NAME + "/#", FAVORITES__ID);
		return matcher;
	}

	@Override
	public boolean onCreate() {
        mOpenHelper = new ItemsDbHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case POPULARS:
				return ItemsContract.PopularEntry.CONTENT_TYPE;
			case POPULARS__ID:
				return ItemsContract.PopularEntry.CONTENT_ITEM_TYPE;
			case TOP_RATED:
				return ItemsContract.HighestRatedEntry.CONTENT_TYPE;
			case TOP_RATED__ID:
				return ItemsContract.HighestRatedEntry.CONTENT_ITEM_TYPE;
			case FAVORITES:
				return ItemsContract.FavoritesEntry.CONTENT_TYPE;
			case FAVORITES__ID:
				return ItemsContract.FavoritesEntry.CONTENT_ITEM_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
		Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		final long _id;
		switch (match) {
			case POPULARS: {
				_id = db.insertOrThrow(ItemsContract.PopularEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
				return ItemsContract.PopularEntry.buildItemUri(_id);
			}
			case TOP_RATED: {
				_id = db.insertOrThrow(ItemsContract.HighestRatedEntry.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return ItemsContract.HighestRatedEntry.buildItemUri(_id);
			}
			case FAVORITES:
				_id = db.insertOrThrow(ItemsContract.FavoritesEntry.TABLE_NAME, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return ItemsContract.FavoritesEntry.buildFavoritesUri(_id);
			default: {
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
	}

	// Note: Putting a bunch of inserts into a single transaction is much faster than inserting them individually
	@Override
	public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
			case POPULARS:
				db.beginTransaction();
				int returnCount = 0;
				try {
					for (ContentValues value : values) {
						long _id = db.insert(ItemsContract.PopularEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCount++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCount;
			case TOP_RATED:
				db.beginTransaction();
				int returnCountTopRated = 0;
				try {
					for (ContentValues value : values) {
						long _id = db.insert(ItemsContract.HighestRatedEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCountTopRated++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCountTopRated;
			case FAVORITES:
				db.beginTransaction();
				int returnCountFavorite = 0;
				try {
					for (ContentValues value : values) {
						long _id = db.insert(ItemsContract.FavoritesEntry.TABLE_NAME, null, value);
						if (_id != -1) {
							returnCountFavorite++;
						}
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				getContext().getContentResolver().notifyChange(uri, null);
				return returnCountFavorite;
			default:
				return super.bulkInsert(uri, values);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
		return builder.where(selection, selectionArgs).update(db, values);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
		return builder.where(selection, selectionArgs).delete(db);
	}

	private SelectionBuilder buildSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = sUriMatcher.match(uri);
		return buildSelection(uri, match, builder);
	}

	private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
		final List<String> paths = uri.getPathSegments();
		switch (match) {
			case POPULARS: {
				return builder.table(ItemsContract.PopularEntry.TABLE_NAME);
			}
			case POPULARS__ID: {
				final String _id = paths.get(1);
				return builder.table(ItemsContract.PopularEntry.TABLE_NAME).where(ItemsContract.PopularEntry.MOVIE_ID + "=?", _id);
			}
			case TOP_RATED: {
				return builder.table(ItemsContract.HighestRatedEntry.TABLE_NAME);
			}
			case TOP_RATED__ID: {
				final String _id = paths.get(1);
				return builder.table(ItemsContract.HighestRatedEntry.TABLE_NAME).where(ItemsContract.HighestRatedEntry.MOVIE_ID + "=?", _id);
			}
			case FAVORITES: {
				return builder.table(ItemsContract.FavoritesEntry.TABLE_NAME);
			}
			case FAVORITES__ID: {
				final String _id = paths.get(1);
				return builder.table(ItemsContract.FavoritesEntry.TABLE_NAME).where(ItemsContract.FavoritesEntry.MOVIE_ID + "=?", _id);
			}
			default: {
				throw new UnsupportedOperationException("Unknown uri: " + uri);
			}
		}
	}

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }
}
