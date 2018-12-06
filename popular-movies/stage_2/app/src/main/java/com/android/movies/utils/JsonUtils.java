package com.android.movies.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.android.movies.model.Movie;
import com.android.movies.model.Trailer;
import com.android.movies.model.Review;

public class JsonUtils {

    public static Movie getMovieInstance(JSONObject movieJsonObject) throws JSONException {
        Movie movie = new Movie(movieJsonObject.getLong("id"), movieJsonObject.getString("original_title"), movieJsonObject.getString("poster_path"),
                                movieJsonObject.getString("overview"), movieJsonObject.getDouble("vote_average"),
                                movieJsonObject.getString("release_date"));
        //System.out.println(movie);
        return movie;
    }

    public static Trailer getMovieTrailerInstance(JSONObject trailerJsonObject) throws JSONException {
        Trailer trailer = new Trailer(trailerJsonObject.getString("name"), trailerJsonObject.getString("key"));
        //System.out.println(trailer);
        return trailer;
    }

    public static Review getReviewInstance(JSONObject reviewJsonObject) throws JSONException {
        Review review = new Review(reviewJsonObject.getString("author"), reviewJsonObject.getString("content"), reviewJsonObject.getString("url"));
        //System.out.println(review);
        return review;
    }

    public static Movie parseMovieJson(String json) throws JSONException {
        Movie movie = null;
        if (json != null && !(json = json.trim()).isEmpty()) {
            movie = getMovieInstance(new JSONObject(json));
        }
        return movie;
    }

    private static JSONArray getResultsJsonArray(String json) {

        JSONArray jsonArray = null;
        if (json != null &&  !(json = json.trim()).isEmpty()) {

            try {
                JSONObject jsonObject = new JSONObject(json);
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }
        return  jsonArray;

    }

    public static ArrayList<Movie> parseMovies(JSONObject jsonObject) {

        ArrayList<Movie> movies = new ArrayList<Movie>();
        if (jsonObject != null) {

            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("results");
            } catch (JSONException je) {
                je.printStackTrace();
            }

            if (jsonArray != null && jsonArray.length() > 0) {

                for (int index = 0; index < jsonArray.length(); index++) {

                    try {
                        movies.add(JsonUtils.getMovieInstance(jsonArray.getJSONObject(index)));
                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                }

            }

        }

        return movies;

    }

    public static ArrayList<Trailer> parseMovieTrailers(String json) {

        JSONArray jsonArray = getResultsJsonArray(json);
        ArrayList<Trailer> trailers = new ArrayList<Trailer>();
        if (jsonArray != null && jsonArray.length() > 0) {

            for (int index = 0; index < jsonArray.length(); index++) {

                try {

                    JSONObject jsonObject = jsonArray.getJSONObject(index);
                    if (jsonObject.getString("type").equalsIgnoreCase("trailer") &&
                        jsonObject.getString("site").equalsIgnoreCase("youtube")) {
                        trailers.add(JsonUtils.getMovieTrailerInstance(jsonObject));
                    }

                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }

        }

        return trailers;

    }

    public static ArrayList<Review> parseReviews(String json) {

        JSONArray jsonArray = getResultsJsonArray(json);
        ArrayList<Review> reviews = new ArrayList<Review>();
        if (jsonArray != null && jsonArray.length() > 0) {

            for (int index = 0; index < jsonArray.length(); index++) {

                try {
                    reviews.add(JsonUtils.getReviewInstance(jsonArray.getJSONObject(index)));
                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }

        }

        return reviews;

    }

}