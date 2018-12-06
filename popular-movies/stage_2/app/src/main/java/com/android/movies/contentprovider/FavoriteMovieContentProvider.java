package com.android.movies.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.text.TextUtils;

import android.net.Uri;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;

import com.android.movies.db.FavoriteMovieTable;
import com.android.movies.db.FavoriteMovieDatabaseHelper;

public class FavoriteMovieContentProvider extends ContentProvider {

    private static final int FAVORITE_MOVIES = 10;
    private static final int FAVORITE_MOVIE = 20;

    private static final String AUTHORITY = "com.android.movies.contentprovider";
    private static final String BASE_PATH = "favorite_movies";

    private FavoriteMovieDatabaseHelper favoriteMovieDatabaseHelper;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, FAVORITE_MOVIES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", FAVORITE_MOVIE);
    }

    private void checkColumns(String[] projection) {

        String[] requiredColumns = { FavoriteMovieTable.COLUMN_MOVIE_ID, FavoriteMovieTable.COLUMN_TITLE, FavoriteMovieTable.COLUMN_POSTER_URL };
        if (projection != null) {

            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(requiredColumns));
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }

        }

    }

    @Override
    public boolean onCreate() {
        favoriteMovieDatabaseHelper = new FavoriteMovieDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);
        queryBuilder.setTables(FavoriteMovieTable.TABLE_FAVORITE_MOVIE);

        int uriType = uriMatcher.match(uri);
        switch (uriType) {

            case FAVORITE_MOVIES:
                break;

            case FAVORITE_MOVIE:
                queryBuilder.appendWhere(FavoriteMovieTable.COLUMN_MOVIE_ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        SQLiteDatabase db = favoriteMovieDatabaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = favoriteMovieDatabaseHelper.getWritableDatabase();

        long id = 0;
        switch (uriType) {

            case FAVORITE_MOVIES:
                id = sqlDB.insert(FavoriteMovieTable.TABLE_FAVORITE_MOVIE, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = uriMatcher.match(uri);
        SQLiteDatabase sqlDB = favoriteMovieDatabaseHelper.getWritableDatabase();

        int rowsDeleted = 0;
        switch (uriType) {

            case FAVORITE_MOVIES:
                rowsDeleted = sqlDB.delete(FavoriteMovieTable.TABLE_FAVORITE_MOVIE, selection, selectionArgs);
                break;

            case FAVORITE_MOVIE:

                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(FavoriteMovieTable.TABLE_FAVORITE_MOVIE,FavoriteMovieTable.COLUMN_MOVIE_ID + "=" + id,null);
                } else {
                    rowsDeleted = sqlDB.delete(FavoriteMovieTable.TABLE_FAVORITE_MOVIE,FavoriteMovieTable.COLUMN_MOVIE_ID + "=" + id + " and " +
                                               selection, selectionArgs);
                }

                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Update no require for Favorite Movie as user can either mark(add) or remove(delete) Movie as Favorite
        return -1;
    }

}