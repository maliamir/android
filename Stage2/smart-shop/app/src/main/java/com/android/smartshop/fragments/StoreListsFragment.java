package com.android.smartshop.fragments;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.LinkedList;

import java.io.IOException;

import android.Manifest;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.smartshop.model.Store;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.utils.RESTCommunication;

import com.android.smartshop.BuildConfig;
import com.android.smartshop.R;

public class StoreListsFragment extends Fragment {

    private static final int PERMISSION_REQUEST_ACCESS_LOCATIONS = 1;

    private Set<String> nearbyZipCodes;
    private Set<Integer> storesSet = new HashSet<Integer>();

    private RelativeLayout storeLoadingRelativeLayout;

    private TableLayout storeListsTbl;
    private TableLayout favoriteStoreListsTbl;

    private TextView zipCodeEt;

    private GoogleApiClient googleApiClient;

    private SmartShopService smartShopService;

    public static StoreListsFragment newInstance() {
        return new StoreListsFragment();
    }

    private void loadNearbyZipCodes() {

        PendingResult<PlaceLikelihoodBuffer> pendingResult = Places.PlaceDetectionApi.getCurrentPlace(googleApiClient, null);
        pendingResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {

                nearbyZipCodes = new HashSet<String>();

                Status status = likelyPlaces.getStatus();
                System.out.println("Likely Places Status Code: " + status.getStatusCode() + "; Message: " + status.getStatusMessage());

                int likelyPlacesCount = likelyPlaces.getCount();
                System.out.println(likelyPlacesCount + " Likely Places FOUND.");

                for (int index = 0; index < likelyPlacesCount; index++) {

                    Place place = likelyPlaces.get(index).getPlace();
                    String placeAddress = place.getAddress().toString();
                    //System.out.println((index + 1) + ". Places Address: " + placeAddress);

                    String[] addresInfo = placeAddress.split(",");
                    String zipCodeAndStateInfo = addresInfo[(addresInfo.length - 2)].trim();
                    //System.out.println("\tZip Code and State Info: " + zipCodeAndStateInfo);

                    String zipCode = zipCodeAndStateInfo.split(" ")[1];
                    //System.out.println("\tZip Code: " + zipCode);

                    nearbyZipCodes.add(zipCode);

                }

                likelyPlaces.release();
                System.out.println("Nearby Zip Codes: " + nearbyZipCodes);

                Iterator<String> iterator = null;
                if (!nearbyZipCodes.isEmpty() && (iterator = nearbyZipCodes.iterator()).hasNext()) {

                    String nearestZipCode = iterator.next();
                    System.out.println("Nearest Zip Code: " + nearestZipCode);
                    zipCodeEt.setText(nearestZipCode);

                    Toast.makeText(getContext(), nearestZipCode + " " + getString(R.string.zip_code_by_current_location_message), Toast.LENGTH_LONG).show();

                    searchStores(nearestZipCode);

                }

            }

        });

    }

    private void connectToGooglePlacesApi() {

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        loadNearbyZipCodes();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }

                }).build();
        googleApiClient.connect();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        System.out.println("onRequestPermissionsResult called - requestCode: " + requestCode);
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATIONS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.connectToGooglePlacesApi();
        }

    }

    private void deleteStore(View view) {

        Store store = (Store) view.getTag();
        System.out.println("Store to be deleted: " + store);
        if (store != null) {

            long storeId = store.getStoreId();
            if (storeId > 0) {

                smartShopService.deleteStore(getContext(), storeId);

                this.favoriteStoreListsTbl.removeView(((TableRow) view.getParent()));
                this.favoriteStoreListsTbl.requestLayout();

                if (this.favoriteStoreListsTbl.getChildCount() <= 1) {
                    this.favoriteStoreListsTbl.setVisibility(View.GONE);
                }

                Toast.makeText(getContext(), (getString(R.string.store) + " \"" + store.getName() + "\" " + getString(R.string.favorite_removed_message)), Toast.LENGTH_LONG).show();

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

        ((TextView) tableRow.findViewById(R.id.store_name_attr)).setText("    " + (count++) + ". " + store.getName());
        ((TextView) tableRow.findViewById(R.id.store_address_details_attr)).setText(stringBuilder.toString());

        table.addView(tableRow);
        table.requestLayout();

        ImageButton storeActionBtn = tableRow.findViewById(R.id.storeActionBtn);
        storeActionBtn.setTag(store);

        if (table.getId() == R.id.store_lists_tbl) {

            storeActionBtn.setContentDescription(getString(R.string.favorite));
            storeActionBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Store selectedStore = (Store) view.getTag();
                    if (storesSet.contains(selectedStore.getWalmartStoreId())) {
                        Toast.makeText(context, ("\"" + selectedStore.getName() + "\" " + getString(R.string.already_added_as_favorite_message)), Toast.LENGTH_LONG).show();
                    } else {

                        smartShopService.addStore(context, selectedStore);
                        selectedStore = smartShopService.findStoreByWalmartStoreId(context, selectedStore.getWalmartStoreId());
                        view.setTag(selectedStore);

                        addStoreToTable(selectedStore, favoriteStoreListsTbl);
                        storesSet.add(selectedStore.getWalmartStoreId());

                        favoriteStoreListsTbl.setVisibility(View.VISIBLE);

                        Toast.makeText(getContext(), (getString(R.string.store) + " \"" + selectedStore.getName() + "\" " + getString(R.string.favorite_added_message)),
                                Toast.LENGTH_LONG).show();

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

                    Store selectedStore = (Store) view.getTag();
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

    private void searchStores(String zipCode) {

        RESTCommunication.setStrictMode();
        if (RESTCommunication.isConnected(getContext())) {

            storeLoadingRelativeLayout.setVisibility(View.VISIBLE);

            this.cleanTable();

            String jsonPayload = null;
            String noStoresFoundMessage = getString(R.string.no_stores_found_message);
            try {
                jsonPayload = RESTCommunication.getJsonPayload((BuildConfig.WALMART_STORES_API_URL + BuildConfig.WALMART_API_KEY + "&zip=" + zipCode));
            } catch (IOException ioe) {
                Toast.makeText(getContext(), (noStoresFoundMessage), Toast.LENGTH_LONG).show();
            }

            boolean noStoresFound = false;
            if (jsonPayload != null && !(jsonPayload = jsonPayload.trim()).isEmpty()) {

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(jsonPayload);
                } catch (JSONException je) {
                    Toast.makeText(getContext(), (noStoresFoundMessage), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), (getString(R.string.no_stores_found_against_zip_code_message) + " \"" + zipCode + "\". " +
                        getString(R.string.enter_zip_code_message)), Toast.LENGTH_LONG).show();
            }

            this.storeLoadingRelativeLayout.setVisibility(View.GONE);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.store_lists, container, false);

        this.zipCodeEt = view.findViewById(R.id.zip_code_et);
        Button searchStoresBtn = view.findViewById(R.id.search_stores_btn);
        this.storeLoadingRelativeLayout = view.findViewById(R.id.loading_stores_info_rl);
        this.storeListsTbl = view.findViewById(R.id.store_lists_tbl);
        this.favoriteStoreListsTbl = view.findViewById(R.id.favorite_store_lists_tbl);

        try {
            this.connectToGooglePlacesApi();
        } catch (Throwable throwable) {

            throwable.printStackTrace();
            System.out.println(throwable.getClass() + " has occurred. Now, attempting to request allowing access to the Location.");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_ACCESS_LOCATIONS);

        }

        searchStoresBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String zipCode = zipCodeEt.getText().toString().trim();
                if (zipCode.isEmpty()) {
                    Toast.makeText(getContext(), (getString(R.string.valid_zip_code_message)), Toast.LENGTH_LONG).show();
                } else if (zipCode.length() != 5) {
                    Toast.makeText(getContext(), (getString(R.string.invalid_zip_code) + " \"" + zipCode + "\". " + getString(R.string.retry_zip_code_message)), Toast.LENGTH_LONG).show();
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