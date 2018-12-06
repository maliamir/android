package com.android.movies.db;

import android.util.Log;

import android.database.sqlite.SQLiteDatabase;

public class FavoriteMovieTable {

    //Database table
    public static final String TABLE_FAVORITE_MOVIE = "favorite_movie";

    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSTER_URL = "poster_url";

    //Database creation SQL statement
    private static final String TABLE_CREATION_SQL = "create table "
                                                        + TABLE_FAVORITE_MOVIE
                                                        + "("
                                                        + COLUMN_MOVIE_ID + " integer primary key autoincrement, "
                                                        + COLUMN_TITLE + " text not null, "
                                                        + COLUMN_POSTER_URL + " text not null"
                                                        + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATION_SQL);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(FavoriteMovieTable.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion +
              ", which will destroy all old data.");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE_MOVIE);
        onCreate(database);
    }

}