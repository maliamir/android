package com.maaksoft.smartshop.activity;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import android.support.v4.app.LoaderManager;

import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;

import android.support.v7.app.AppCompatActivity;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.net.Uri;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.maaksoft.smartshop.model.Product;
import com.maaksoft.smartshop.model.Store;
import com.maaksoft.smartshop.model.ShopList;
import com.maaksoft.smartshop.model.ListItem;
import com.maaksoft.smartshop.model.ItemInfo;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.utils.JsonProcessor;
import com.maaksoft.smartshop.utils.RESTCommunication;

import com.maaksoft.smartshop.BuildConfig;
import com.maaksoft.smartshop.R;

import com.maaksoft.smartshop.adapter.ItemInfoRecyclerViewAdapter;

public class ProductsSearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>> {

    public static final int ITEM_INFOS_LOADER_ID = 0;

    public static final String SHOP_LIST_ID = "SHOP_LIST_ID";

    private Integer walmartStoreId;

    private String title;
    private String itemsRequestPayload;

    private LinkedList<ItemInfo> itemInfos = new LinkedList<ItemInfo>();

    @BindView(R.id.no_products_found_message_tv)
    TextView noProductsFoundMessageTv;

    @BindView(R.id.favorite_store_lists)
    Spinner favoriteStoresList;

    @BindView(R.id.loading_products_info_rl)
    RelativeLayout productsLoadingRelativeLayout;

    @BindView(R.id.item_infos_view_container)
    //RecyclerView itemsInfoViewContainer;
    //ListView itemsInfoViewContainer;
    LinearLayout itemsInfoViewContainer;

    private ItemInfoList itemInfoList;
    private ItemInfoRecyclerViewAdapter itemInfoRecyclerViewAdapter;

    private LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>> loaderCallbacks = new LoaderManager.LoaderCallbacks<LinkedList<ItemInfo>>() {

        @Override
        public Loader<LinkedList<ItemInfo>> onCreateLoader(int id, Bundle args) {
            return getCreateLoader(ProductsSearchActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<LinkedList<ItemInfo>> loader, LinkedList<ItemInfo> itemInfos) {
            //itemInfoRecyclerViewAdapter.setItemInfos(itemInfos);
        }

        @Override
        public void onLoaderReset(Loader<LinkedList<ItemInfo>> loader) {
            //itemInfoRecyclerViewAdapter.setItemInfos(null);
        }

    };

    private LinkedList<ItemInfo> loadProducts() {

        String jsonPayload = "";
        LinkedList<ItemInfo> itemInfos = null;
        try {
            Uri uri = Uri.parse(BuildConfig.GOOGLE_APP_ENGINE_PRODUCTS_SEARCH_URL).buildUpon().appendPath(this.walmartStoreId.toString()).build();
            jsonPayload = RESTCommunication.postPayload(uri, this.itemsRequestPayload);
        } catch (IOException ioe) {
            Toast.makeText(this, getString(R.string.unable_to_search_products_message), Toast.LENGTH_LONG).show();
            ioe.printStackTrace();
        }

        if (jsonPayload != null && !(jsonPayload = jsonPayload.trim()).isEmpty()) {

            //System.out.println("JSON Payload:\n" + jsonPayload);
            itemInfos = JsonProcessor.loadItemInfos(jsonPayload);
            for (ItemInfo itemInfo: itemInfos) {
                System.out.println(itemInfo);
            }

        }

        return itemInfos;

    }

    private void invalidateData() {

        itemInfos = new LinkedList<ItemInfo>();

        if (itemInfoList != null) {
            itemInfoList.setItemInfos(null);
        }

        //this.itemInfoRecyclerViewAdapter.setItemInfos(null);

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

                if (itemInfos != null && itemInfos.size() > 0) {

                    itemsInfoViewContainer.removeAllViews();
                    String[] itemNames = new String[itemInfos.size()];
                    for (int index = 0; index < itemInfos.size(); index++) {

                        ItemInfo itemInfo = itemInfos.get(index);
                        itemNames[index] = itemInfo.getItemName();

                        itemsInfoViewContainer.addView(loadView(index, itemInfo));


                    }

                    //itemInfoList = new ItemInfoList(mainActivity, itemNames, itemInfos);
                    //mainActivity.itemsInfoViewContainer.setAdapter(itemInfoList);

                } else {
                    noProductsFoundMessageTv.setVisibility(View.VISIBLE);
                }

                //mainActivity.itemInfoRecyclerViewAdapter.setItemInfos(mainActivity.itemNames);
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

    private String initCap(String string) {
        return (("" + string.charAt(0)).toUpperCase() + string.substring(1));
    }

    public View loadView(int position, ItemInfo itemInfo) {

        Context context = this;
        View rowView = getLayoutInflater().inflate(R.layout.item_infos_list, null, true);

        if (itemInfo != null) {

            ((TextView) rowView.findViewById(R.id.item_name_tv)).setText(((position + 1) + ". " + initCap(itemInfo.getItemName())));

            List<Product> products = itemInfo.getProducts();
            if (products != null && products.size() > 0) {
                System.out.println("Item Name: " + itemInfo.getItemName() + "; Products found: " + products.size());

                String[] productNames = new String[products.size()];
                //ListView foundProductsViewContainer = rowView.findViewById(R.id.found_products_view_container);
                //RecyclerView foundProductsViewContainer = rowView.findViewById(R.id.found_products_view_container);
                LinearLayout foundProductsViewContainer = rowView.findViewById(R.id.found_products_layout_container);
                for (int index = 0; index < products.size(); index++) {

                    Product product = products.get(index);
                    productNames[index] = product.getName();

                    View productDetailsView = LayoutInflater.from(context).inflate(R.layout.products_detail, null, false);
                    ImageView productImageView = productDetailsView.findViewById(R.id.product_image_iv);
                    TextView productNameTextView = productDetailsView.findViewById(R.id.product_name_tv);
                    TextView aisleLocationTextView = productDetailsView.findViewById(R.id.aisle_location_tv);
                    TextView priceTextView = productDetailsView.findViewById(R.id.price_tv);
                    TextView ratingsTextView = productDetailsView.findViewById(R.id.ratings_tv);
                    Picasso.with(context).load(product.getImageUrl()).into(productImageView);

                    productNameTextView.setText(product.getName());
                    aisleLocationTextView.setText(product.getAisleLocation());
                    priceTextView.setText(("$" + product.getPrice()));
                    ratingsTextView.setText(product.getRatings().toString());

                    foundProductsViewContainer.addView(productDetailsView);

                }

                /*
                ProductList productList = new ProductList(context, productNames, products);
                //foundProductsViewContainer.setAdapter(productList);
                foundProductsViewContainer.setLayoutManager(new LinearLayoutManager(this.context));
                FoundProductRecyclerViewAdapter foundProductRecyclerViewAdapter = new FoundProductRecyclerViewAdapter(this.context, itemInfo.getProducts());
                foundProductsViewContainer.setAdapter(foundProductRecyclerViewAdapter);
                */
            }

        } else {
            noProductsFoundMessageTv.setVisibility(View.VISIBLE);
        }

        return rowView;

    }

    class ItemInfoList extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] itemNames;
        private List<ItemInfo> itemInfos;

        public ItemInfoList(Activity context, String[] itemNames, List<ItemInfo> itemInfos) {
            super(context, R.layout.item_infos_list, itemNames);
            this.context = context;
            this.itemNames = itemNames;
            this.itemInfos = itemInfos;
        }

        public void setItemInfos(List<ItemInfo> itemInfos) {
            this.itemInfos = itemInfos;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.item_infos_list, null, true);

            if (this.itemInfos != null && position < this.itemInfos.size()) {

                ItemInfo itemInfo = this.itemInfos.get(position);
                ((TextView) rowView.findViewById(R.id.item_name_tv)).setText(((position + 1) + ". " + initCap(itemInfo.getItemName())));

                List<Product> products = itemInfo.getProducts();
                if (products != null && products.size() > 0) {
                    System.out.println("Item Name: " + itemInfo.getItemName() + "; Products found: " + products.size());

                    String[] productNames = new String[products.size()];
                    //ListView foundProductsViewContainer = rowView.findViewById(R.id.found_products_view_container);
                    //RecyclerView foundProductsViewContainer = rowView.findViewById(R.id.found_products_view_container);
                    LinearLayout foundProductsViewContainer = rowView.findViewById(R.id.found_products_layout_container);
                    for (int index = 0; index < products.size(); index++) {

                        Product product = products.get(index);
                        productNames[index] = product.getName();

                        View productDetailsView = LayoutInflater.from(context).inflate(R.layout.products_detail, parent, false);
                        ImageView productImageView = productDetailsView.findViewById(R.id.product_image_iv);
                        TextView productNameTextView = productDetailsView.findViewById(R.id.product_name_tv);
                        TextView aisleLocationTextView = productDetailsView.findViewById(R.id.aisle_location_tv);
                        TextView priceTextView = productDetailsView.findViewById(R.id.price_tv);
                        TextView ratingsTextView = productDetailsView.findViewById(R.id.ratings_tv);
                        Picasso.with(this.context).load(product.getImageUrl()).into(productImageView);

                        productNameTextView.setText(product.getName());
                        aisleLocationTextView.setText(product.getAisleLocation());
                        priceTextView.setText(("$" + product.getPrice()));
                        ratingsTextView.setText(product.getRatings().toString());

                        foundProductsViewContainer.addView(productDetailsView);

                    }

                /*
                ProductList productList = new ProductList(context, productNames, products);
                //foundProductsViewContainer.setAdapter(productList);
                foundProductsViewContainer.setLayoutManager(new LinearLayoutManager(this.context));
                FoundProductRecyclerViewAdapter foundProductRecyclerViewAdapter = new FoundProductRecyclerViewAdapter(this.context, itemInfo.getProducts());
                foundProductsViewContainer.setAdapter(foundProductRecyclerViewAdapter);
                */
                }

            } else {
                noProductsFoundMessageTv.setVisibility(View.VISIBLE);
            }

            return rowView;

        }

    }

    class ProductList extends ArrayAdapter<String> {

        private final Activity context;
        private final String[] productNames;
        private final List<Product> products;

        public ProductList(Activity context, String[] productNames, List<Product> products) {
            super(context, R.layout.products_detail, productNames);
            this.context = context;
            this.productNames = productNames;
            this.products = products;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.products_detail, null, true);

            ImageView productImageView = rowView.findViewById(R.id.product_image_iv);
            TextView productNameTextView = rowView.findViewById(R.id.product_name_tv);
            TextView aisleLocationTextView = rowView.findViewById(R.id.aisle_location_tv);
            TextView priceTextView = rowView.findViewById(R.id.price_tv);
            TextView ratingsTextView = rowView.findViewById(R.id.ratings_tv);

            Product product = this.products.get(position);
            Picasso.with(this.context).load(product.getImageUrl()).into(productImageView);

            productNameTextView.setText(product.getName());
            aisleLocationTextView.setText(product.getAisleLocation());
            priceTextView.setText(("$" + product.getPrice()));
            ratingsTextView.setText(product.getRatings().toString());

            return rowView;

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_search);

        ButterKnife.bind(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int height = point.y;
        System.out.println("height: " + height);
        ViewGroup.LayoutParams layoutParams = itemsInfoViewContainer.getLayoutParams();
        layoutParams.height = height;
        itemsInfoViewContainer.setLayoutParams(layoutParams);

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

                    noProductsFoundMessageTv.setVisibility(View.GONE);

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

        /*
        this.itemsInfoViewContainer.setLayoutManager(new LinearLayoutManager(this));
        this.itemInfoRecyclerViewAdapter = new ItemInfoRecyclerViewAdapter(ProductsSearchActivity.this, null);
        this.itemsInfoViewContainer.setAdapter(this.itemInfoRecyclerViewAdapter);
        */

        this.title = getString(R.string.products_search);
        setTitle(this.title);

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
                        //System.out.println("itemsRequestPayload:\n" + this.itemsRequestPayload);
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