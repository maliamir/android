package com.android.movies.utils;

import java.util.Scanner;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.StrictMode;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.util.Log;

import android.widget.Toast;

import com.android.movies.BuildConfig;

/**
 * Utility class to communicate with REST APIs.
 */
public final class RestUtils {

    private static final String TAG = RestUtils.class.getSimpleName();

    //Using API_KEY from gradle.properties. ALWAYS use your specific API_KEY and set in gradle.properties file.
    public static final String API_KEY = "?api_key=" + BuildConfig.API_KEY;

    public static final String MOVIE_IMAGES_BASE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String POPULAR_MOVIES_URL = BASE_MOVIE_URL + "popular" + API_KEY;
    public static final String TOP_RATED_MOVIES_URL = BASE_MOVIE_URL + "top_rated" + API_KEY;

    public static final String YOUTUBE_APP = "vnd.youtube:";
    public static final String TRAILER_VIEWER_BASE_URL = "http://www.youtube.com/watch?v=";

    public static String getPagedUrl(String url, int pageNumber) {

        if (url.indexOf("&") > 0) {
            url = url.substring(0, url.lastIndexOf("&"));
        }
        return (url + "&page=" + pageNumber);

    }

    public static void setStrictMode() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    public static String getMovieTrailersUrl(String baseMovieUrlString) {

        String trailersUrlString = baseMovieUrlString + "/videos" + API_KEY;
        System.out.println("Trailers URL String: " + trailersUrlString);
        return trailersUrlString;

    }

    public static String getMovieReviewsUrl(String baseMovieUrlString) {

        String reviewsUrlString = baseMovieUrlString + "/reviews" + API_KEY;
        System.out.println("Reviews URL String: " + reviewsUrlString);
        return reviewsUrlString;

    }

    public static String getJsonPayload(Context context, String urlString) throws IOException {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());

        String jsonPayload = "";
        if (isConnected) {

            URL url = null;
            Uri builtUri = Uri.parse(urlString).buildUpon().build();
            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Built URI: " + url);

            if (url != null) {

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    Scanner scanner = new Scanner(urlConnection.getInputStream());
                    scanner.useDelimiter("\\A");
                    if (scanner.hasNext()) {
                        jsonPayload = scanner.next();
                    }

                } finally {
                    urlConnection.disconnect();
                }

            }

        } else {
            Toast.makeText(context, "Not Connected to the Internet. Please connect to the Internet then try again, Thank You",
                           Toast.LENGTH_LONG).show();
        }

        return jsonPayload;

    }

}