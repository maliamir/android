package com.maaksoft.smartshop.adapter;

import java.util.LinkedList;

import android.content.Context;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.TextView;

import com.maaksoft.smartshop.model.ItemInfo;

import com.maaksoft.smartshop.R;

public class ItemInfoRecyclerViewAdapter extends RecyclerView.Adapter<ItemInfoRecyclerViewAdapter.ItemInfoAdapterViewHolder> {

    private static LayoutInflater layoutInflater;

    private Context context;

    private LinkedList<ItemInfo> itemInfos;

    public LinkedList<ItemInfo> getItemInfos() {
        return itemInfos;
    }

    public void setItemInfos(LinkedList<ItemInfo> itemInfos) {
        this.itemInfos = itemInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ((itemInfos != null) ? itemInfos.size() : 0);
    }

    public ItemInfoRecyclerViewAdapter(Context context, LinkedList<ItemInfo> movies) {
        this.context = context;
        this.itemInfos = movies;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ItemInfoAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView;
        RecyclerView foundProductsRecyclerView;

        public ItemInfoAdapterViewHolder(View view) {

            super(view);
            itemNameTextView = view.findViewById(R.id.item_name_tv);
            //foundProductsRecyclerView = view.findViewById(R.id.found_products_view_container);
            foundProductsRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        }

    }

    @Override
    public ItemInfoAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_infos_list, viewGroup, false);
        return new ItemInfoAdapterViewHolder(view);
    }

    private String initCap(String string) {
        return (("" + string.charAt(0)).toUpperCase() + string.substring(1));
    }

    @Override
    public void onBindViewHolder(ItemInfoAdapterViewHolder itemInfoAdapterViewHolder, final int position) {

        if (itemInfos != null && position < itemInfos.size()) {

            final ItemInfo itemInfo = itemInfos.get(position);
            itemInfoAdapterViewHolder.itemNameTextView.setText(((position + 1) + ". " + this.initCap(itemInfo.getItemName())));

            FoundProductRecyclerViewAdapter foundProductRecyclerViewAdapter = new FoundProductRecyclerViewAdapter(this.context, null);
            foundProductRecyclerViewAdapter.setProducts(itemInfo.getProducts());
            itemInfoAdapterViewHolder.foundProductsRecyclerView.setAdapter(foundProductRecyclerViewAdapter);

        }

    }

}