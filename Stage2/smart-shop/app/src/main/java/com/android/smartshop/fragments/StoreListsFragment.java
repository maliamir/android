package com.android.smartshop.fragments;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;

import java.io.IOException;

import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.smartshop.model.Store;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.utils.RESTCommunication;

import com.android.smartshop.BuildConfig;
import com.android.smartshop.R;

public class StoreListsFragment extends Fragment {

    private RelativeLayout storeLoadingRelativeLayout;

    private TableLayout storeListsTbl;
    private TableLayout favoriteStoreListsTbl;

    private Set<Integer> storesSet = new HashSet<Integer>();

    private SmartShopService smartShopService;

    public static StoreListsFragment newInstance() {
        return new StoreListsFragment();
    }

    private void deleteStore(View view) {

        Store store = (Store)view.getTag();
        System.out.println("Store to be deleted: " + store);
        if (store != null) {

            long storeId = store.getStoreId();
            if (storeId > 0) {

                smartShopService.deleteStore(getContext(), storeId);

                this.favoriteStoreListsTbl.removeView(((TableRow)view.getParent()));
                this.favoriteStoreListsTbl.requestLayout();

                if (this.favoriteStoreListsTbl.getChildCount() <= 1) {
                    this.favoriteStoreListsTbl.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), ("Store \"" + store.getName() + "\" has been removed successfully as Favorite Store."), Toast.LENGTH_LONG).show();

            }

        }

    }

    private void cleanTable() {

        int childCount = this.storeListsTbl.getChildCount();

        //Remove all rows except the first one which is a header row
        if (childCount > 1) {
            this.storeListsTbl.removeViews(1, (childCount - 1));
        }

    }

    private void addStoreToTable(Store store, TableLayout table) {

        final Context context = getContext();
        final TableRow tableRow = (TableRow) LayoutInflater.from(context).inflate(R.layout.table_row_for_store_lists, null);
        tableRow.setBackgroundResource(R.drawable.cell_shape);

        int count = table.getChildCount();
        String spaces = "        ";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(spaces).append(store.getStreetAddress()).append("\n");
        stringBuilder.append(spaces).append(store.getCity()).append(", ");
        stringBuilder.append(store.getState()).append(", ");
        stringBuilder.append(store.getZipCode());

        ((TextView)tableRow.findViewById(R.id.store_name_attr)).setText("    " + (count++) + ". " + store.getName());
        ((TextView)tableRow.findViewById(R.id.store_address_details_attr)).setText(stringBuilder.toString());

        table.addView(tableRow);
        table.requestLayout();

        ImageButton storeActionBtn = tableRow.findViewById(R.id.storeActionBtn);
        storeActionBtn.setTag(store);

        if (table.getId() == R.id.store_lists_tbl) {

            storeActionBtn.setContentDescription(getString(R.string.favorite));
            storeActionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Store selectedStore = (Store)view.getTag();
                    if (storesSet.contains(selectedStore.getWalmartStoreId())) {
                        Toast.makeText(context, ("\"" + selectedStore.getName() + "\" already added as Favorite."), Toast.LENGTH_LONG).show();
                    } else {

                        smartShopService.addStore(context, selectedStore);
                        selectedStore = smartShopService.findStoreByWalmartStoreId(context, selectedStore.getWalmartStoreId());
                        view.setTag(selectedStore);

                        addStoreToTable(selectedStore, favoriteStoreListsTbl);
                        storesSet.add(selectedStore.getWalmartStoreId());

                        favoriteStoreListsTbl.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), ("Store \"" + selectedStore.getName() + "\" has been added successfully as Favorite Store."), Toast.LENGTH_LONG).show();

                    }

                }

            });

        } else {

            storeActionBtn.setContentDescription(getString(R.string.unfavorite));
            storesSet.add(store.getWalmartStoreId());

            storeActionBtn.setImageDrawable(getResources().getDrawable(R.drawable.delete));
            storeActionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Store selectedStore = (Store)view.getTag();
                    deleteStore(view);
                    storesSet.remove(selectedStore.getWalmartStoreId());

                }

            });

        }

    }

    private void addStoresToTable(List<Store> stores, TableLayout table) {

        System.out.println("Stores size: " + stores.size());
        if (stores != null && stores.size() > 0) {

            for (Store store : stores) {
                addStoreToTable(store, table);
            }


        }

        if (table.getChildCount() <= 1) {
            table.setVisibility(View.GONE);
        } else {
            table.setVisibility(View.VISIBLE);
        }

    }

    private void searchStores(String zipCode){

        RESTCommunication.setStrictMode();
        if (RESTCommunication.isConnected(getContext())) {

            storeLoadingRelativeLayout.setVisibility(View.VISIBLE);

            this.cleanTable();

            String jsonPayload = null;
            try {
                jsonPayload = RESTCommunication.getJsonPayload((BuildConfig.WALMART_STORES_API_URL + BuildConfig.WALMART_API_KEY + "&zip=" + zipCode));
            } catch (IOException ioe) {
                Toast.makeText(getContext(), ("Unable to search Stores. Please try again."), Toast.LENGTH_LONG).show();
            }

            boolean noStoresFound = false;
            if (jsonPayload != null && !(jsonPayload = jsonPayload.trim()).isEmpty()) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(jsonPayload);
                } catch (JSONException je) {
                    Toast.makeText(getContext(), ("Unable to search Stores. Please try again."), Toast.LENGTH_LONG).show();
                    je.printStackTrace();
                }

                if (jsonArray != null && jsonArray.length() > 0) {

                    List<Store> stores = new LinkedList<Store>();
                    for (int index = 0, storesCount = 0; (storesCount < 5 && index < jsonArray.length()); index++) {//Only first 5 Stores and if ends the JSONArray

                        try {

                            JSONObject jsonObject = (JSONObject) jsonArray.get(index);
                            int walmartStoreId = Integer.parseInt(jsonObject.getString("no"));
                            if (!storesSet.contains(walmartStoreId)) {

                                stores.add(new Store(walmartStoreId, jsonObject.getString("name"), jsonObject.getString("streetAddress"),
                                        jsonObject.getString("city"), jsonObject.getString("stateProvCode"), jsonObject.getString("zip")));
                                storesCount++;

                            }

                        } catch (JSONException je) {
                            je.printStackTrace();
                        }

                    }

                    this.addStoresToTable(stores, this.storeListsTbl);

                } else {
                    noStoresFound = true;
                }

            } else {
                noStoresFound = true;
            }

            if (noStoresFound) {
                Toast.makeText(getContext(), ("No Stores found against Zip Code \"" + zipCode + "\". Please try another Zip Code."), Toast.LENGTH_LONG).show();
            }

            this.storeLoadingRelativeLayout.setVisibility(View.GONE);

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.store_lists, container,false);

        final TextView zipCodeEt = view.findViewById(R.id.zip_code_et);
        Button searchStoresBtn = view.findViewById(R.id.search_stores_btn);
        this.storeLoadingRelativeLayout = view.findViewById(R.id.loading_stores_info_rl);
        this.storeListsTbl = view.findViewById(R.id.store_lists_tbl);
        this.favoriteStoreListsTbl = view.findViewById(R.id.favorite_store_lists_tbl);

        searchStoresBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

              String zipCode = zipCodeEt.getText().toString().trim();
              if (zipCode.isEmpty()) {
                  Toast.makeText(getContext(), ("Please provide a valid 5 digits Zip Code."), Toast.LENGTH_LONG).show();
              } else if (zipCode.length() != 5) {
                  Toast.makeText(getContext(), ("Invalid Zip Code \"" + zipCode + "\". Zip Code MUST BE 5 digits number. Please try again."), Toast.LENGTH_LONG).show();
              } else {
                  searchStores(zipCode);
              }

            }

        });

        this.smartShopService = new SmartShopService();
        List<Store> stores = this.smartShopService.getStores(getContext());
        this.addStoresToTable(stores, this.favoriteStoreListsTbl);

        return view;

    }

}