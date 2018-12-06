package com.android.baking.service;

import java.util.List;

import android.content.Context;

import android.net.ConnectivityManager;

import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import retrofit2.converter.gson.GsonConverterFactory;

import com.android.baking.model.Recipe;

public class ApiCaller {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static boolean isNetworkAvailable(Context context) {

        //Using ConnectivityManager to check for ApiCaller Connection
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (connectivityManager.getActiveNetworkInfo() != null);

    }

    public static void loadRecipes(final RequestCompletionListener requestFinishedListener) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RecipeRetroFitter recipeRetroFitter = retrofit.create(RecipeRetroFitter.class);

        Call<List<Recipe>> call = recipeRetroFitter.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {

            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                requestFinishedListener.onResponse(response);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable throwable) {
                requestFinishedListener.onFailure(throwable.getMessage());
            }

        });
    }

}