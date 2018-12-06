package com.android.baking.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

import com.android.baking.model.Recipe;

public interface RecipeRetroFitter {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();

}