package com.android.smartshop.widget;

import java.util.List;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.TextView;

import android.appwidget.AppWidgetManager;

import com.android.smartshop.model.Store;
import com.android.smartshop.model.ShopList;

import com.android.smartshop.service.SmartShopService;

import com.android.smartshop.R;

import com.android.smartshop.activity.MainActivity;

public class WidgetActivity extends Activity {

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle bundle) {

        super.onCreate(bundle);
        setResult(RESULT_CANCELED);

        setContentView(R.layout.smart_shop_widget_items);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        } else {

            SmartShopService smartShopService = new SmartShopService();
            List<Store> stores = smartShopService.getStores(this);
            List<ShopList> shopLists = smartShopService.getShopLists(this);

            final Intent detailsIntent = new Intent(this, MainActivity.class);

            View.OnClickListener widgetStoresTvOnClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    detailsIntent.putExtra(MainActivity.FRAGMENT_INDEX, 0);
                    startActivity(detailsIntent);
                }

            };
            View.OnClickListener widgetShopListsOnClickListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    detailsIntent.putExtra(MainActivity.FRAGMENT_INDEX, 1);
                    startActivity(detailsIntent);
                }

            };

            TextView widgetStoresTv = findViewById(R.id.widget_stores);
            widgetStoresTv.setText(("" + stores.size() + " Store(s)"));
            widgetStoresTv.setOnClickListener(widgetStoresTvOnClickListener);
            findViewById(R.id.widget_stores_iv).setOnClickListener(widgetStoresTvOnClickListener);

            TextView widgetShopListsTv = findViewById(R.id.widget_shop_lists);
            widgetShopListsTv.setText(("" + shopLists.size() + " Shop List(s)"));
            widgetShopListsTv.setOnClickListener(widgetShopListsOnClickListener);
            findViewById(R.id.widget_shop_lists_iv).setOnClickListener(widgetShopListsOnClickListener);

        }

    }

}