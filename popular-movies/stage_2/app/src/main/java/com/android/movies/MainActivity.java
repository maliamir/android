package com.android.movies;

import java.util.ArrayList;

import java.io.IOException;

import android.os.Bundle;
import android.os.PersistableBundle;

import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.movies.model.Movie;

import com.android.movies.service.MovieService;

import com.android.movies.utils.JsonUtils;

import com.android.movies.utils.RestUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    public static final int MOVIES_LOADER_ID = 0;

    public static final String SAVED_TITLE = "SAVED_TITLE";
    public static final String SAVED_MOVIES = "SAVED_MOVIES";
    public static final String LOAD_FAVORITE_MOVIES_FLAG = "LOAD_FAVORITE_MOVIES_FLAG";

    private boolean loadFavoriteMovies = false;

    private int currentPageNumber = 1;
    private int totalNumberOfPages = 1;

    private String title;
    private String currentUrl = RestUtils.POPULAR_MOVIES_URL;

    private ArrayList<Movie> moviesInfo = new ArrayList<Movie>();

    private RecyclerView recyclerView;

    private MovieInfoRecyclerViewAdapter movieInfoRecyclerViewAdapter;

    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> loaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
            return getCreateLoader(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
            movieInfoRecyclerViewAdapter.setMovies(movies);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
            movieInfoRecyclerViewAdapter.setMovies(null);
        }

    };

    private ArrayList<Movie> loadMovies() {

        ArrayList<Movie> movies = null;
        if (this.loadFavoriteMovies) {
            System.out.println("Do load Favorite Movies. So, will be fetching Favorite Movies from the database to get the latest state in \"deliverResult()\" method.");
        } else {

            String jsonPayload = "";
            try {
                jsonPayload = RestUtils.getJsonPayload(this, this.currentUrl);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (jsonPayload != null && !jsonPayload.isEmpty()) {

                try {

                    JSONObject jsonObject = new JSONObject(jsonPayload);
                    this.currentPageNumber = jsonObject.getInt("page");
                    this.totalNumberOfPages = jsonObject.getInt("total_pages");
                    movies = JsonUtils.parseMovies(jsonObject);

                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }

        }

        return movies;

    }

    private Loader<ArrayList<Movie>> getCreateLoader(final MainActivity mainActivity) {

        return new AsyncTaskLoader<ArrayList<Movie>>(mainActivity) {

            @Override
            protected void onStartLoading() {

                if (mainActivity.moviesInfo != null && !mainActivity.moviesInfo.isEmpty()) {
                    deliverResult(mainActivity.moviesInfo);
                } else {
                    forceLoad();
                }

            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                return mainActivity.loadMovies();
            }

            public void deliverResult(ArrayList<Movie> movies) {

                if (mainActivity.loadFavoriteMovies) {
                    System.out.println("Do load Favorite Movies. So, fetching all Favorite Movies from the database to get the latest state.");
                    mainActivity.moviesInfo = (new MovieService()).getFavoriteMovies(mainActivity.getContentResolver());
                } else if (movies != null && !movies.isEmpty()) {
                    mainActivity.moviesInfo.addAll(movies);
                }

                super.deliverResult(mainActivity.moviesInfo);
                mainActivity.movieInfoRecyclerViewAdapter.setMovies(mainActivity.moviesInfo);

            }

        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestUtils.setStrictMode();

        this.recyclerView = findViewById(R.id.movies_recycler_view);

        //If Orientation is 'Portait' then 4 movies in a row other 6 movies for 'Landscape'.
        int spanCount = ((getResources().getConfiguration().orientation == 1) ? 4 : 6);

        /*
            Keeping things simple here by using hard-coded Column Span Count. It is understood that it is not feasible for devices of different size.
            It can be dynamically managed by EITHER adding GlobalLayoutListener on RecyclerView's ViewTreeObserver OR customizing GridLayoutManager by extending it.
            But as said keeping this "Stage 1" implementation simple.
        */
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false));

        this.movieInfoRecyclerViewAdapter = new MovieInfoRecyclerViewAdapter(MainActivity.this, null);
        this.recyclerView.setAdapter(this.movieInfoRecyclerViewAdapter);

        if(savedInstanceState != null) {

            this.loadFavoriteMovies = savedInstanceState.getBoolean(LOAD_FAVORITE_MOVIES_FLAG);

            this.title = savedInstanceState.getString(SAVED_TITLE);
            setTitle(this.title);

            this.moviesInfo = savedInstanceState.getParcelableArrayList(SAVED_MOVIES);
            this.movieInfoRecyclerViewAdapter.setMovies(this.moviesInfo);

        } else {
            this.title = getString(R.string.app_name);
        }

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, loaderCallbacks);

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(LOAD_FAVORITE_MOVIES_FLAG, this.loadFavoriteMovies);
        outState.putString(SAVED_TITLE, this.title);
        outState.putParcelableArrayList(SAVED_MOVIES, this.moviesInfo);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(LOAD_FAVORITE_MOVIES_FLAG, this.loadFavoriteMovies);
        outState.putString(SAVED_TITLE, this.title);
        outState.putParcelableArrayList(SAVED_MOVIES, this.moviesInfo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle loaderArgs) {
        return getCreateLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private void invalidateData() {
        moviesInfo = new ArrayList<Movie>();
        movieInfoRecyclerViewAdapter.setMovies(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int selectedMenuItemId = menuItem.getItemId(), titleId = -1;
        boolean invalidateData = true;

        loadFavoriteMovies = false;
        if (selectedMenuItemId == R.id.favorite_movies) {

            loadFavoriteMovies = true;
            titleId = R.string.favorite_movies;

        } else if (selectedMenuItemId == R.id.load_more_movies) {

            invalidateData = false;
            if (currentPageNumber >= totalNumberOfPages) {
                return true;
            } else {

                this.currentUrl = RestUtils.getPagedUrl(this.currentUrl, ++currentPageNumber);
                System.out.println("New Page URL: " + this.currentUrl);

                if (currentPageNumber >= totalNumberOfPages) {
                    menuItem.setEnabled(false);//No more pages left, so disabling the Menu Item
                } else {
                    menuItem.setEnabled(true);
                }

            }

        } else {

            if (selectedMenuItemId == R.id.popular_movies) {
                titleId = R.string.app_name;
                this.currentUrl = RestUtils.POPULAR_MOVIES_URL;
            } else if (selectedMenuItemId == R.id.top_rated_movies) {
                titleId = R.string.top_rated_movies;
                this.currentUrl = RestUtils.TOP_RATED_MOVIES_URL;
            }

        }

        if (invalidateData) {

            if (titleId != -1) {
                this.title = getString(titleId);
                setTitle(this.title);
            }

            System.out.println("Selected Menu Item Id: " + selectedMenuItemId + "; Current URL: " + this.currentUrl);
            invalidateData();

        }

        getSupportLoaderManager().getLoader(MOVIES_LOADER_ID).forceLoad();

        return super.onOptionsItemSelected(menuItem);

    }

}