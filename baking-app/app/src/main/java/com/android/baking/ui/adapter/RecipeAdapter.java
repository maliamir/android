package com.android.baking.ui.adapter;

import java.util.ArrayList;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.baking.R;
import com.android.baking.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecyclerHolder> {

    private ArrayList<Recipe> recipes;

    private Context context;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder recyclerHolder, final int position) {

        String recipeName = recipes.get(position).getName();
        recyclerHolder.recipeName.setText(recipeName);

        String recipeImage = recipes.get(position).getImage();
        if (recipeImage.isEmpty()) {

            switch (recipeName) {

                case "Nutella Pie":
                    recyclerHolder.recipeImage.setImageResource(R.drawable.nutella_pie);
                    break;

                case "Brownies":
                    recyclerHolder.recipeImage.setImageResource(R.drawable.brownies);
                    break;

                case "Yellow Cake":
                    recyclerHolder.recipeImage.setImageResource(R.drawable.yellow_cake);
                    break;

                case "Cheesecake":
                    recyclerHolder.recipeImage.setImageResource(R.drawable.cheesecake);
                    break;

            }

        } else {
            recyclerHolder.recipeImage.setImageResource(R.drawable.recipe_step_placeholder);
        }

    }

    @Override
    public int getItemCount() {

        if (recipes == null) {
            return 0;
        }
        return recipes.size();

    }

    class RecyclerHolder extends RecyclerView.ViewHolder {

        TextView recipeName;
        ImageView recipeImage;

        RecyclerHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeImage = itemView.findViewById(R.id.image);
        }

    }

}