package com.android.smartshop.adapter;

import java.util.List;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import com.android.smartshop.model.Product;

import com.android.smartshop.R;

public class FoundProductRecyclerViewAdapter extends RecyclerView.Adapter<FoundProductRecyclerViewAdapter.FoundProductAdapterViewHolder> {

    private static LayoutInflater layoutInflater;

    private Context context;

    private List<Product> products;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ((products != null) ? products.size() : 0);
    }

    public FoundProductRecyclerViewAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class FoundProductAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextView;
        TextView aisleLocationTextView;
        TextView priceTextView;
        TextView ratingsTextView;

        public FoundProductAdapterViewHolder(View view) {
            super(view);
            this.productImageView = view.findViewById(R.id.product_image_iv);
            this.productNameTextView = view.findViewById(R.id.product_name_tv);
            this.aisleLocationTextView = view.findViewById(R.id.aisle_location_tv);
            this.priceTextView = view.findViewById(R.id.price_tv);
            this.ratingsTextView = view.findViewById(R.id.ratings_tv);
        }

    }

    @Override
    public FoundProductAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_detail, viewGroup, false);
        return new FoundProductAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoundProductAdapterViewHolder itemInfoAdapterViewHolder, final int position) {

        final Product product = this.products.get(position);
        Picasso.with(this.context).load(product.getImageUrl()).into(itemInfoAdapterViewHolder.productImageView);

        itemInfoAdapterViewHolder.productNameTextView.setText(product.getName());
        itemInfoAdapterViewHolder.aisleLocationTextView.setText(product.getAisleLocation());
        itemInfoAdapterViewHolder.priceTextView.setText(("$" + product.getPrice()));
        itemInfoAdapterViewHolder.ratingsTextView.setText(product.getRatings().toString());

    }

}