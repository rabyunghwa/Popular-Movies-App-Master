package com.awesome.byunghwa.app.mytvapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "popularmoviesapp.db";
    private static final int DATABASE_VERSION = 1;

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //popularity table
        db.execSQL("CREATE TABLE " + ItemsContract.PopularityEntry.TABLE_NAME + " ("
                + ItemsContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.MOVIE_ID + " LONG NOT NULL DEFAULT 0,"
                + ItemsContract.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.POSTER_THUMBNAIL + " TEXT NOT NULL"
                //+ ItemsContract.VOTE_AVERAGE + " TEXT NOT NULL"
                //+ ItemsContract.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");

        //vote average table
        db.execSQL("CREATE TABLE " + ItemsContract.VoteEntry.TABLE_NAME + " ("
                + ItemsContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.MOVIE_ID + " LONG NOT NULL DEFAULT 0,"
                + ItemsContract.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.POSTER_THUMBNAIL + " TEXT NOT NULL"
                //+ ItemsContract.VOTE_AVERAGE + " TEXT NOT NULL"
                //+ ItemsContract.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");

        // favorites table
        db.execSQL("CREATE TABLE " + ItemsContract.FavoritesEntry.TABLE_NAME + " ("
                + ItemsContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.MOVIE_ID + " LONG NOT NULL DEFAULT 0,"
                + ItemsContract.ORIGINAL_TITLE + " TEXT NOT NULL,"
                + ItemsContract.OVERVIEW + " TEXT NOT NULL,"
                + ItemsContract.POSTER_THUMBNAIL + " TEXT NOT NULL"
                //+ ItemsContract.VOTE_AVERAGE + " TEXT"
                //+ ItemsContract.RELEASE_DATE + " INTEGER NOT NULL DEFAULT 0"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.PopularityEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.VoteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ItemsContract.FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
