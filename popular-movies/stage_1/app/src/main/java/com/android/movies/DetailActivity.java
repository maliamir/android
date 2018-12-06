package com.android.movies;

import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;

import com.android.movies.model.Movie;

import com.android.movies.utils.JsonUtils;

import com.android.movies.utils.RestUtils;

public class DetailActivity extends AppCompatActivity {

    private static final int DEFAULT_MOVIE_ID = -1;

    public static final String MOVIE_ID = "movie_id";

    @BindView(R.id.image_iv) ImageView moviePostIv;
    @BindView(R.id.movie_original_title) TextView originalTitleTv;
    @BindView(R.id.release_date_tv) TextView releaseDateTv;
    @BindView(R.id.movie_average_vote_tv) TextView averageVoteTv;
    @BindView(R.id.plot_synopsis_tv) TextView plotSynopsisTv;

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void populateUI(Movie movie) {

        if (movie != null) {

            String movieOriginalTitle = movie.getOriginalTitle();
            String posterPath = RestUtils.MOVIE_IMAGES_BASE_URL + movie.getMoviePosterImageUrl();
            System.out.println("Movie Original Title: " + movieOriginalTitle + "; Poster Path: " + posterPath);

            setTitle(movieOriginalTitle);

            Picasso.with(this).load(posterPath).into(moviePostIv);
            originalTitleTv.setText(movie.getOriginalTitle());
            releaseDateTv.setText(movie.getReleaseDate());
            averageVoteTv.setText(movie.getAverageVote().toString());
            plotSynopsisTv.setText(movie.getPlotSynopsis());

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        RestUtils.setStrictMode();

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        } else {

            int movieId = intent.getIntExtra(MOVIE_ID, DEFAULT_MOVIE_ID);
            if (movieId == DEFAULT_MOVIE_ID) { //MOVIE_ID not found in intent
                closeOnError();
            } else {

                String urlString = RestUtils.BASE_MOVIE_URL + movieId + RestUtils.API_KEY;
                System.out.println("URL String: " + urlString);
                try {

                    Movie movie = JsonUtils.parseMovieJson(RestUtils.getJsonPayload(this, urlString));
                    populateUI(movie);

                } catch (Exception e) {
                    e.printStackTrace();
                    closeOnError();
                    return;
                }

            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;

    }

}