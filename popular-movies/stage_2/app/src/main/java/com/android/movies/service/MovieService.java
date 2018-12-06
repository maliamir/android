package com.android.movies.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.ContentResolver;

import android.net.Uri;

import android.database.Cursor;

import com.android.movies.model.Movie;

import com.android.movies.db.FavoriteMovieTable;

import com.android.movies.contentprovider.FavoriteMovieContentProvider;

/*
  NOTE: Since, "Android Room" is used for Simple DAO operations as specified in specs for "Stage 2" i.e. just to add/remove Movies as Favorites and list Favorite Movies,
        instead of implementing ContentProvider implemented a simple Service class to perform actions like add/remove Movie as Favorite, find if Move is Favorite or not
        and list Favorite Movies
*/
public class MovieService {

    public boolean isFavoriteMovie(ContentResolver contentResolver, Long movieId) {

        Cursor cursor = contentResolver.query(Uri.parse(FavoriteMovieContentProvider.CONTENT_URI + "/" + movieId), new String[] { FavoriteMovieTable.COLUMN_MOVIE_ID },
                                             null, null, null);
        return (cursor.getCount() > 0);

    }

    public void markMovieAsFavorite(ContentResolver contentResolver, Movie movie) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieTable.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoriteMovieTable.COLUMN_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoriteMovieTable.COLUMN_POSTER_URL, movie.getMoviePosterImageUrl());

        contentResolver.insert(FavoriteMovieContentProvider.CONTENT_URI , contentValues);

    }

    public void removeMovieAsFavorite(ContentResolver contentResolver, Long movieId) {
        contentResolver.delete(Uri.parse(FavoriteMovieContentProvider.CONTENT_URI + "/" + movieId), null, null);
    }

    public ArrayList<Movie> getFavoriteMovies(ContentResolver contentResolver) {

        ArrayList<Movie> movieList = new ArrayList<Movie>();
        Cursor cursor = contentResolver.query(FavoriteMovieContentProvider.CONTENT_URI,
                                              new String[] { FavoriteMovieTable.COLUMN_MOVIE_ID, FavoriteMovieTable.COLUMN_TITLE, FavoriteMovieTable.COLUMN_POSTER_URL },
                                             null,null, null);
        while (cursor.moveToNext()) {

            long movieId = cursor.getLong(cursor.getColumnIndexOrThrow(FavoriteMovieTable.COLUMN_MOVIE_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieTable.COLUMN_TITLE));
            String posterUrl = cursor.getString(cursor.getColumnIndexOrThrow(FavoriteMovieTable.COLUMN_POSTER_URL));

            movieList.add(new Movie(movieId, title, posterUrl, null, null, null));

        }

        return movieList;

    }

}