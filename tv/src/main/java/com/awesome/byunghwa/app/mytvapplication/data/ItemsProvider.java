
package com.awesome.byunghwa.app.mytvapplication.data;

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

    private static final int POPULARITY = 0;
    private static final int POPULARITY__ID = 1;

    private static final int VOTE_AVERAGE = 2;
    private static final int VOTE_AVERAGE__ID = 3;

    private static final int FAVORITES = 4;
    private static final int FAVORITES__ID = 5;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ItemsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "popularity", POPULARITY);
        matcher.addURI(authority, "popularity/#", POPULARITY__ID);

        matcher.addURI(authority, "vote_average", VOTE_AVERAGE);
        matcher.addURI(authority, "vote_average/#", VOTE_AVERAGE__ID);

        matcher.addURI(authority, "favorites", FAVORITES);
        matcher.addURI(authority, "favorites/#", FAVORITES__ID);

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
            case POPULARITY:
                return ItemsContract.PopularityEntry.CONTENT_TYPE;
            case POPULARITY__ID:
                return ItemsContract.PopularityEntry.CONTENT_ITEM_TYPE;
            case VOTE_AVERAGE:
                return ItemsContract.VoteEntry.CONTENT_TYPE;
            case VOTE_AVERAGE__ID:
                return ItemsContract.VoteEntry.CONTENT_ITEM_TYPE;
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
            case POPULARITY:
                _id = db.insertOrThrow(ItemsContract.PopularityEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ItemsContract.PopularityEntry.buildItemUri(_id);
            case VOTE_AVERAGE: {
                _id = db.insertOrThrow(ItemsContract.VoteEntry.TABLE_NAME, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return ItemsContract.VoteEntry.buildItemUri(_id);
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
            case POPULARITY:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ItemsContract.PopularityEntry.TABLE_NAME, null, value);
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
            case VOTE_AVERAGE:
                db.beginTransaction();
                int returnCount_vote = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ItemsContract.VoteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount_vote++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount_vote;
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
            case POPULARITY: {
                return builder.table(ItemsContract.PopularityEntry.TABLE_NAME);
            }
            case POPULARITY__ID: {
                final String _id = paths.get(1);
                return builder.table(ItemsContract.PopularityEntry.TABLE_NAME).where(ItemsContract.MOVIE_ID + "=?", _id);
            }
            case VOTE_AVERAGE: {
                return builder.table(ItemsContract.VoteEntry.TABLE_NAME);
            }
            case VOTE_AVERAGE__ID: {
                final String _id = paths.get(1);
                return builder.table(ItemsContract.VoteEntry.TABLE_NAME).where(ItemsContract.MOVIE_ID + "=?", _id);
            }
            case FAVORITES: {
                return builder.table(ItemsContract.FavoritesEntry.TABLE_NAME);
            }
            case FAVORITES__ID: {
                final String _id = paths.get(1);
                return builder.table(ItemsContract.FavoritesEntry.TABLE_NAME).where(ItemsContract.MOVIE_ID + "=?", _id);
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
