package com.android.smartshop.activity;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import android.os.Bundle;

import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.android.smartshop.model.Store;
import com.android.smartshop.model.ShopList;
import com.android.smartshop.model.ListItem;
import com.android.smartshop.model.ItemInfo;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.utils.JsonProcessor;
import com.android.smartshop.utils.RESTCommunication;

import com.android.smartshop.adapter.ItemInfoRecyclerViewAdapter;

import com.android.smartshop.BuildConfig;
import com.android.smartshop.R;

public class ProductsSearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>> {

    public static final int ITEM_INFOS_LOADER_ID = 0;

    public static final String SHOP_LIST_ID = "SHOP_LIST_ID";

    private Integer walmartStoreId;

    private String title;
    private String itemsRequestPayload;

    private LinkedList<ItemInfo> itemInfos = new LinkedList<ItemInfo>();

    @BindView(R.id.favorite_store_lists)
    Spinner favoriteStoresList;

    @BindView(R.id.loading_products_info_rl)
    RelativeLayout productsLoadingRelativeLayout;

    @BindView(R.id.item_infos_recycler_view)
    RecyclerView recyclerView;

    private ItemInfoRecyclerViewAdapter itemInfoRecyclerViewAdapter;

    private LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>> loaderCallbacks = new LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>>() {

        @Override
        public Loader<LinkedList<ItemInfo>> onCreateLoader(int id, Bundle args) {
            return getCreateLoader(ProductsSearchActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<LinkedList<ItemInfo>> loader, LinkedList<ItemInfo> movies) {
            itemInfoRecyclerViewAdapter.setItemInfos(movies);
        }

        @Override
        public void onLoaderReset(Loader<LinkedList<ItemInfo>> loader) {
            itemInfoRecyclerViewAdapter.setItemInfos(null);
        }

    };

    private LinkedList<ItemInfo> loadProducts() {

        String jsonPayload = "";
        LinkedList<ItemInfo> itemInfos = null;
        try {
            Uri uri = Uri.parse(BuildConfig.GOOGLE_APP_ENGINE_PRODUCTS_SEARCH_URL).buildUpon().appendPath(this.walmartStoreId.toString()).build();
            jsonPayload = RESTCommunication.postPayload(uri, this.itemsRequestPayload);
        } catch (IOException ioe) {
            Toast.makeText(this, ("Unable to search Products by each Item. Please try again."), Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
        }

        if (jsonPayload != null && !(jsonPayload = jsonPayload.trim()).isEmpty()) {

            System.out.println("JSON Payload:\n" + jsonPayload);
            itemInfos = JsonProcessor.loadItemInfos(jsonPayload);
            for (ItemInfo itemInfo: itemInfos) {
                System.out.println(itemInfo);
            }

        }

        return itemInfos;

    }

    private void invalidateData() {
        itemInfos = new LinkedList<ItemInfo>();
        this.itemInfoRecyclerViewAdapter.setItemInfos(null);
    }

    private Loader<LinkedList<ItemInfo>> getCreateLoader(final ProductsSearchActivity mainActivity) {

        return new AsyncTaskLoader<LinkedList<ItemInfo>>(mainActivity) {

            @Override
            protected void onStartLoading() {

                if (mainActivity.itemInfos != null && !mainActivity.itemInfos.isEmpty()) {
                    deliverResult(mainActivity.itemInfos);
                } else {
                    forceLoad();
                }

            }

            @Override
            public LinkedList<ItemInfo> loadInBackground() {
                return mainActivity.loadProducts();
            }

            public void deliverResult(LinkedList<ItemInfo> itemInfos) {

                mainActivity.itemInfos = itemInfos;
                super.deliverResult(mainActivity.itemInfos);
                mainActivity.itemInfoRecyclerViewAdapter.setItemInfos(mainActivity.itemInfos);
                productsLoadingRelativeLayout.setVisibility(View.GONE);

            }

        };

    }

    class FavoriteStoreList extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] storeInfos;
        private final Integer[] walmartStoresIds;

        public FavoriteStoreList(Activity context, String[] storeInfos, Integer[] walmartStoresIds) {
            super(context, R.layout.stores_spinner_list_item, storeInfos);
            super.setDropDownViewResource(R.layout.stores_spinner_list_item);
            this.context = context;
            this.storeInfos = storeInfos;
            this.walmartStoresIds = walmartStoresIds;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            TextView favoriteStoreNameTv = (TextView)context.getLayoutInflater().inflate(R.layout.stores_spinner_list_item, null, true);
            favoriteStoreNameTv.setTag(walmartStoresIds[position]);

            return favoriteStoreNameTv;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_search);

        ButterKnife.bind(this);

        List<Store> stores = new SmartShopService().getStores(this);
        if (stores != null && stores.size() > 0) {

            final String[] storeInfos = new String[stores.size()];
            Integer[] walmartStoreIds = new Integer[stores.size()];
            for (int index = 0; index < stores.size(); index++) {

                Store store = stores.get(index);
                storeInfos[index] = (store.getName() + ", " + store.getZipCode());
                walmartStoreIds[index] = store.getWalmartStoreId();

            }
            walmartStoreId = walmartStoreIds[0];

            favoriteStoresList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ProductsSearchActivity productsSearchActivity = ProductsSearchActivity.this;
                    TextView walmartStoreNameTv = ProductsSearchActivity.this.findViewById(R.id.walmart_store_name_tv);
                    walmartStoreNameTv.setText(storeInfos[position]);
                    walmartStoreId = (Integer)view.getTag();
                    System.out.println("Selected Walmart Store No.: " + walmartStoreId);

                    invalidateData();
                    productsSearchActivity.getSupportLoaderManager().getLoader(ITEM_INFOS_LOADER_ID).forceLoad();
                    productsLoadingRelativeLayout.setVisibility(View.VISIBLE);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }

            });

            favoriteStoresList.setAdapter(new FavoriteStoreList(this, storeInfos, walmartStoreIds));
            favoriteStoresList.setSelection(0);

        }

        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.itemInfoRecyclerViewAdapter = new ItemInfoRecyclerViewAdapter(ProductsSearchActivity.this, null);
        this.recyclerView.setAdapter(this.itemInfoRecyclerViewAdapter);

        this.title = getString(R.string.app_name);

        long shopListId = -1;
        SmartShopService smartShopService = new SmartShopService();
        if (getIntent() != null) {
            shopListId = getIntent().getLongExtra(SHOP_LIST_ID, -1);
        }

        if (shopListId > 0) {

            ShopList shopList = smartShopService.getShopListById(this, shopListId);
            if (shopList != null) {

                List<ListItem> listItems = shopList.getListItems();
                if (listItems != null && listItems.size() > 0) {

                    JSONArray jsonArray = new JSONArray();
                    JSONObject mainJsonObject = new JSONObject();
                    for (ListItem listItem : listItems) {

                        try {

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("itemName", listItem.getItemName());
                            jsonObject.put("limit", listItem.getLimit());
                            jsonObject.put("sortBy", ((listItem.getSortType() == 1) ? "highest_ratings": "lowest_price"));
                            jsonArray.put(jsonObject);

                        } catch (JSONException je) {
                        }

                    }

                    try {
                        mainJsonObject.put("itemInfoList", jsonArray);
                        this.itemsRequestPayload = mainJsonObject.toString();
                        System.out.println("itemsRequestPayload:\n" + this.itemsRequestPayload);
                    } catch (JSONException je) {
                    }

                }

            }

        }

        RESTCommunication.setStrictMode();
        if (RESTCommunication.isConnected(this)) {
            getSupportLoaderManager().initLoader(ITEM_INFOS_LOADER_ID, null, loaderCallbacks);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(ITEM_INFOS_LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public Loader<LinkedList<ItemInfo>> onCreateLoader(int id, final Bundle loaderArgs) {
        return getCreateLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<LinkedList<ItemInfo>> loader, LinkedList<ItemInfo> movies) {
    }

    @Override
    public void onLoaderReset(Loader<LinkedList<ItemInfo>> loader) {
    }

}