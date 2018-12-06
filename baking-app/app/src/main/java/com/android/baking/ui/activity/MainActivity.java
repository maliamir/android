package com.android.baking.ui.activity;

import java.util.List;
import java.util.ArrayList;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.os.Parcelable;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import android.view.View;
import static android.view.View.GONE;

import android.widget.ProgressBar;
import android.widget.Toast;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import android.support.test.espresso.idling.CountingIdlingResource;

import butterknife.BindView;
import butterknife.ButterKnife;

import retrofit2.Response;

import com.android.baking.service.ApiCaller;
import com.android.baking.service.RequestCompletionListener;

import com.android.baking.R;

import com.android.baking.model.Recipe;

import com.android.baking.ui.RecyclerListener;

import com.android.baking.ui.adapter.RecipeAdapter;

public class MainActivity extends AppCompatActivity implements RequestCompletionListener {

    int position;

    ArrayList<Recipe> recipes;

    @BindView(R.id.recipe_rv)
    RecyclerView recipesRecyclerView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private CountingIdlingResource idlingResource = new CountingIdlingResource("Loading_Data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);

        if(savedInstanceState == null) {
            idlingResource.increment();
            ApiCaller.loadRecipes(this);
        }

        recipesRecyclerView.addOnItemTouchListener(new RecyclerListener(MainActivity.this, recipesRecyclerView, new RecyclerListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Intent details = new Intent(MainActivity.this, RecipeActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("steps", (ArrayList<? extends Parcelable>) recipes.get(position).getSteps());
                bundle.putParcelableArrayList("ingredients", (ArrayList<? extends Parcelable>) recipes.get(position).getIngredients());
                bundle.putString("recipe_name",recipes.get(position).getName());
                details.putExtra("bundle",bundle);

                startActivity(details);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

        }));

        //Call isNetworkAvailable class
        if (!ApiCaller.isNetworkAvailable(MainActivity.this)) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Internet Connection Required")
                    .setCancelable(false)
                    .setPositiveButton("Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // Restart the Activity
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

    }

    @Override
    public void onFailure(String message) {
        progressBar.setVisibility(GONE);
        Toast.makeText(MainActivity.this, "No Internet Connection !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Response<List<Recipe>> response) {
        recipes = (ArrayList<Recipe>)response.body();
        progressBar.setVisibility(GONE);
        renderRecyclerView();
        idlingResource.decrement();
    }


    public void reload(View view) {
        progressBar.setVisibility(View.VISIBLE);
        ApiCaller.loadRecipes(this);
    }


    public void renderRecyclerView() {

        int width = getWindowManager().getDefaultDisplay().getMode().getPhysicalWidth();
        if (width > 1500) {
            recipesRecyclerView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        } else {
            recipesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        }

        recipesRecyclerView.setAdapter(new RecipeAdapter(MainActivity.this, recipes));
        recipesRecyclerView.getLayoutManager().scrollToPosition(position);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("recipes",recipes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipes=savedInstanceState.getParcelableArrayList("recipes");
        position=savedInstanceState.getInt("position");
        renderRecyclerView();
        progressBar.setVisibility(GONE);
    }

    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource() {
        return idlingResource;
    }

}