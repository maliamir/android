package com.android.baking.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.baking.R;
import com.android.baking.model.Ingredient;

import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.RecyclerHolder> {

    Context context;

    private ArrayList<Ingredient> ingredients;


    public IngredientAdapter(Context context , ArrayList<Ingredient> ingredients) {
        this.context = context;
        this.ingredients=ingredients;
    }

    @Override
    public IngredientAdapter.RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( parent.getContext()).inflate(R.layout.ingredients_list_item, parent, false);
        return new IngredientAdapter.RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {

        holder.name.setText(ingredients.get(position).getIngredient());
        holder.quantity.setText(""+ingredients.get(position).getQuantity());
        holder.measure.setText(ingredients.get(position).getMeasure());

    }


    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView name,quantity,measure;

        RecyclerHolder(View itemView) {
            super(itemView);
            name=(TextView) itemView.findViewById(R.id.ingredient_name);
            quantity=(TextView) itemView.findViewById(R.id.quantity);
            measure=(TextView) itemView.findViewById(R.id. measure);
        }
    }
}
