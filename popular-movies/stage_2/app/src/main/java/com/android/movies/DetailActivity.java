package com.android.movies;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.PersistableBundle;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.BindView;

import com.android.movies.model.Movie;
import com.android.movies.model.Trailer;
import com.android.movies.model.Review;

import com.android.movies.service.MovieService;

import com.android.movies.utils.JsonUtils;

import com.android.movies.utils.RestUtils;

public class DetailActivity extends AppCompatActivity {

    private static final long DEFAULT_MOVIE_ID = -1;

    public static final String SAVED_MOVIE = "SAVED_MOVIE";
    public static final String SAVED_MOVIE_TRAILERS = "SAVED_MOVIE_TRAILERS";
    public static final String SAVED_MOVIE_REVIEWS = "SAVED_MOVIE_REVIEWS";

    public static final String MOVIE_ID = "movie_id";

    @BindView(R.id.poster_iv) ImageView moviePosterIv;
    @BindView(R.id.favorite_iv) ImageView favoriteIv;
    @BindView(R.id.movie_original_title) TextView originalTitleTv;
    @BindView(R.id.release_date_tv) TextView releaseDateTv;
    @BindView(R.id.movie_average_vote_tv) TextView averageVoteTv;
    @BindView(R.id.plot_synopsis_tv) TextView plotSynopsisTv;

    private Movie movie;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_LONG).show();
    }

    private void populateUI(final Movie movie) {

        if (movie != null) {

            String movieOriginalTitle = movie.getOriginalTitle();
            String posterPath = RestUtils.MOVIE_IMAGES_BASE_URL + movie.getMoviePosterImageUrl();
            System.out.println("Movie Original Title: " + movieOriginalTitle + "; Poster Path: " + posterPath);
            setTitle(movieOriginalTitle);

            originalTitleTv.setText(movie.getOriginalTitle());
            releaseDateTv.setText(movie.getReleaseDate());
            averageVoteTv.setText(movie.getAverageVote().toString());
            plotSynopsisTv.setText(movie.getPlotSynopsis());

            Picasso.with(this).load(posterPath).into(moviePosterIv);

            final MovieService movieService = new MovieService();
            if (movieService.isFavoriteMovie(getContentResolver(), movie.getId())) {
                favoriteIv.setImageResource(R.drawable.favorite);
                favoriteIv.setTag(R.string.favorite);
                System.out.println("Movie Original Title: " + movieOriginalTitle + " is a FAVORITE movie.");
            } else {
                favoriteIv.setImageResource(R.drawable.unfavorite);
                favoriteIv.setTag(R.string.unfavorite);
                System.out.println("Movie Original Title: " + movieOriginalTitle + " is NOT a FAVORITE movie.");
            }

            favoriteIv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Integer tag = (Integer) favoriteIv.getTag();
                    String message;

                    if (tag == R.string.unfavorite) {

                        favoriteIv.setImageResource(R.drawable.favorite);
                        favoriteIv.setTag(R.string.favorite);
                        //favoriteIv.setTooltipText(getString(R.string.favorite));
                        message = getString(R.string.mark_favorite);

                        try {
                            movieService.markMovieAsFavorite(getContentResolver(), movie);
                        } catch (Exception e) {
                            System.out.println("Non-critical issue occurred while inserting Movie as Favorite which has already been added as Favorite.");
                        }

                    } else {

                        favoriteIv.setImageResource(R.drawable.unfavorite);
                        favoriteIv.setTag(R.string.unfavorite);
                        //favoriteIv.setTooltipText(getString(R.string.unfavorite));
                        message = getString(R.string.remove_unfavorite);

                        movieService.removeMovieAsFavorite(getContentResolver(), movie.getId());

                    }

                    System.out.println("Message: " + message);
                    Toast.makeText(DetailActivity.this, message, Toast.LENGTH_LONG).show();

                }

            });

            if (this.trailers != null) {
                TrailerInfoListAdapter trailerInfoListAdapter = new TrailerInfoListAdapter(this, this.trailers.toArray(new Trailer[this.trailers.size()]));
                ListView trailersListView = findViewById(R.id.trailers_list_view);
                trailersListView.setAdapter(trailerInfoListAdapter);
            }

            if (this.reviews != null) {
                ReviewInfoListAdapter reviewInfoListAdapter = new ReviewInfoListAdapter(this, this.reviews.toArray(new Review[this.reviews.size()]));
                ListView reviewsListView = findViewById(R.id.reviews_list_view);
                reviewsListView.setAdapter(reviewInfoListAdapter);
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        RestUtils.setStrictMode();

        if(savedInstanceState == null) {

            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
                return;
            } else {

                long movieId = intent.getLongExtra(MOVIE_ID, DEFAULT_MOVIE_ID);
                if (movieId == DEFAULT_MOVIE_ID) { //MOVIE_ID not found in intent
                    closeOnError();
                    return;
                } else {

                    String baseMovieUrlString = RestUtils.BASE_MOVIE_URL + movieId;
                    String movieUrlString = baseMovieUrlString + RestUtils.API_KEY;
                    try {

                        this.movie = JsonUtils.parseMovieJson(RestUtils.getJsonPayload(this, movieUrlString));

                        String trailersUrlString = RestUtils.getMovieTrailersUrl(baseMovieUrlString);
                        this.trailers = JsonUtils.parseMovieTrailers(RestUtils.getJsonPayload(this, trailersUrlString));

                        String reviewsUrlString = RestUtils.getMovieReviewsUrl(baseMovieUrlString);
                        this.reviews = JsonUtils.parseReviews(RestUtils.getJsonPayload(this, reviewsUrlString));

                    } catch (Exception e) {
                        e.printStackTrace();
                        closeOnError();
                        return;
                    }

                }

            }

        } else {

            this.movie = savedInstanceState.getParcelable(SAVED_MOVIE);
            this.trailers = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_TRAILERS);
            this.reviews = savedInstanceState.getParcelableArrayList(SAVED_MOVIE_REVIEWS);

        }

        this.populateUI(this.movie);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        outState.putParcelable(SAVED_MOVIE, this.movie);
        outState.putParcelableArrayList(SAVED_MOVIE_TRAILERS, this.trailers);
        outState.putParcelableArrayList(SAVED_MOVIE_REVIEWS, this.reviews);
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(SAVED_MOVIE, this.movie);
        outState.putParcelableArrayList(SAVED_MOVIE_TRAILERS, this.trailers);
        outState.putParcelableArrayList(SAVED_MOVIE_REVIEWS, this.reviews);
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return true;

    }

}