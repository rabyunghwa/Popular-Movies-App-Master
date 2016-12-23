package com.awesome.byunghwa.app.popularmoviesapp2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "popularmoviesapp2.db";
    private static final int DATABASE_VERSION = 1;

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // popular movies table
        db.execSQL("CREATE TABLE " + ItemsContract.PopularEntry.TABLE_NAME + " ("
                + ItemsContract.PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.PopularEntry.MOVIE_ID + " LONG NOT NULL UNIQUE DEFAULT 0,"
                + ItemsContract.PopularEntry.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.PopularEntry.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.PopularEntry.POSTER_THUMBNAIL + " TEXT NOT NULL,"
                + ItemsContract.PopularEntry.VOTE_AVERAGE + " TEXT NOT NULL,"
                + ItemsContract.PopularEntry.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");

        // top rated movies
        db.execSQL("CREATE TABLE " + ItemsContract.HighestRatedEntry.TABLE_NAME + " ("
                + ItemsContract.HighestRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.HighestRatedEntry.MOVIE_ID + " LONG NOT NULL UNIQUE DEFAULT 0,"
                + ItemsContract.HighestRatedEntry.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.HighestRatedEntry.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.HighestRatedEntry.POSTER_THUMBNAIL + " TEXT NOT NULL,"
                + ItemsContract.HighestRatedEntry.VOTE_AVERAGE + " TEXT NOT NULL,"
                + ItemsContract.HighestRatedEntry.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");

        // favorites table
        db.execSQL("CREATE TABLE " + ItemsContract.FavoritesEntry.TABLE_NAME + " ("
                + ItemsContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.FavoritesEntry.MOVIE_ID + " LONG NOT NULL UNIQUE DEFAULT 0,"
                + ItemsContract.FavoritesEntry.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.FavoritesEntry.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.FavoritesEntry.POSTER_THUMBNAIL + " TEXT NOT NULL,"
                + ItemsContract.FavoritesEntry.VOTE_AVERAGE + " TEXT NOT NULL,"
                + ItemsContract.FavoritesEntry.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.PopularEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.HighestRatedEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
