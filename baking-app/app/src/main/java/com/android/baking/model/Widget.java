package com.android.baking.model;

import java.util.ArrayList;

public class Widget {

    public String recipeTitle;
    public ArrayList<Ingredient> ingredients;

    public Widget(String recipeTitle, ArrayList<Ingredient> ingredients) {
        this.recipeTitle = recipeTitle;
        this.ingredients = ingredients;
    }

}