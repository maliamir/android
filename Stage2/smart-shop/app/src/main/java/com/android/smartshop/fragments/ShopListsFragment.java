package com.android.smartshop.fragments;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.smartshop.model.Store;
import com.android.smartshop.model.ShopList;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.R;

import com.android.smartshop.activity.MainActivity;
import com.android.smartshop.activity.ProductsSearchActivity;

public class ShopListsFragment extends Fragment {

    private TableLayout shopListsTbl;

    public static ShopListsFragment newInstance() {
        return new ShopListsFragment();
    }

    private void deleteShopList(View view) {

        Long shopListId = (Long) view.getTag();
        if (shopListId != null && shopListId > 0) {

            new SmartShopService().deleteShopList(getContext(), shopListId);

            this.shopListsTbl.removeView(((TableRow) view.getParent()));
            this.shopListsTbl.requestLayout();

            if (this.shopListsTbl.getChildCount() <= 1) {
                this.shopListsTbl.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shop_lists_listing, container, false);
        this.shopListsTbl = view.findViewById(R.id.shop_lists_tbl);

        final Context context = getContext();
        SmartShopService smartShopService = new SmartShopService();

        boolean showMessage = false;
        TextView messageTv = view.findViewById(R.id.message_tv);

        List<ShopList> shopLists = smartShopService.getShopLists(context);
        if (shopLists != null && shopLists.size() > 0) {

            this.shopListsTbl.setVisibility(View.VISIBLE);
            for (int index = 0; index < shopLists.size(); index++) {

                final ShopList shopList = shopLists.get(index);
                final TableRow tableRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.table_row_for_shop_lists, null);
                tableRow.setBackgroundResource(R.drawable.cell_shape);
                ((TextView) tableRow.findViewById(R.id.list_name_attr)).setText(((index + 1) + ".  " + shopList.getName()));
                tableRow.setTag(shopList.getShopListId());
                this.shopListsTbl.addView(tableRow);
                this.shopListsTbl.requestLayout();

                ImageButton playListBtn = tableRow.findViewById(R.id.playListBtn);
                playListBtn.setTag(shopList.getShopListId());
                playListBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, ProductsSearchActivity.class);
                        intent.putExtra(ProductsSearchActivity.SHOP_LIST_ID, (Long) view.getTag());
                        context.startActivity(intent);

                    }

                });
                if (showMessage) {
                    playListBtn.setEnabled(false);
                    playListBtn.setImageDrawable(getResources().getDrawable(R.drawable.unplay));
                }

                ImageButton editListBtn = tableRow.findViewById(R.id.editListBtn);
                editListBtn.setTag(shopList.getShopListId());
                editListBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Bundle arguments = new Bundle();
                        arguments.putLong(ShopListDetailFragment.SHOP_LIST_ID, (Long) view.getTag());
                        ShopListDetailFragment shopListDetailFragement = ShopListDetailFragment.newInstance();
                        shopListDetailFragement.setArguments(arguments);
                        FragmentTransaction fragmentTransaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, shopListDetailFragement);
                        fragmentTransaction.commit();

                    }

                });

                ImageButton deleteListBtn = tableRow.findViewById(R.id.deleteListBtn);
                deleteListBtn.setTag(shopList.getShopListId());
                deleteListBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        deleteShopList(view);
                    }

                });

            }

        } else {
            showMessage = true;
        }

        if (!showMessage) {

            List<Store> stores = new SmartShopService().getStores(context);
            if (stores == null || stores.isEmpty()) {
                showMessage = true;
                messageTv.setText(R.string.no_stores_list_message);
            }

        }

        if (showMessage) {
            messageTv.setVisibility(View.VISIBLE);
        }

        return view;

    }

}