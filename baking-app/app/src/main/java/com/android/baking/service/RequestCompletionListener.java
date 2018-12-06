package com.android.baking.service;

import java.util.List;

import retrofit2.Response;

import com.android.baking.model.Recipe;

public interface RequestCompletionListener {

    void onFailure(String message);
    void onResponse(Response<List<Recipe>> response);

}
