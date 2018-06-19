package com.android.smartshop.widget;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.net.Uri;

import com.android.smartshop.R;

import com.android.smartshop.activity.MainActivity;

public class SmartShopWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_shop_widget_items);

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