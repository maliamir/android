package com.maaksoft.smartshop.widget;

import java.util.List;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.net.Uri;

import com.maaksoft.smartshop.model.Store;
import com.maaksoft.smartshop.model.ShopList;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.R;

import com.maaksoft.smartshop.activity.MainActivity;

public class SmartShopWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_shop_widget_items);

        SmartShopService smartShopService = new SmartShopService();
        List<Store> stores = smartShopService.getStores(context);
        List<ShopList> shopLists = smartShopService.getShopLists(context);

        remoteViews.setTextViewText(R.id.widget_stores, ("" + stores.size() + " " + context.getString(R.string.stores_info)));
        remoteViews.setTextViewText(R.id.widget_shop_lists, ("" + shopLists.size() + " " + context.getString(R.string.shop_lists_info)));

        Intent detailsIntent1 = new Intent(context, MainActivity.class);
        detailsIntent1.putExtra(MainActivity.FRAGMENT_INDEX, 0);
        remoteViews.setOnClickFillInIntent(R.id.widget_stores, detailsIntent1);

        Intent detailsIntent2 = new Intent(context, MainActivity.class);
        detailsIntent2.putExtra(MainActivity.FRAGMENT_INDEX, 1);
        remoteViews.setOnClickFillInIntent(R.id.widget_shop_lists, detailsIntent2);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setPendingIntentTemplate(R.id.widget_container, appPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        // When the user deletes the baking_widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {

        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first baking_widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last baking_widget is disabled
    }

}