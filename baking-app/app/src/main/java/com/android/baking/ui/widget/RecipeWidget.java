package com.android.baking.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.android.baking.R;
import com.android.baking.db.Database;
import com.android.baking.ui.activity.MainActivity;

public class RecipeWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Database database=new Database(context);
        String title=database.getRecipeTitle(appWidgetId);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);
        views.setTextViewText(R.id.recipe_title, title);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widgetListView, intent);


        // Set the PlantDetailActivity intent to launch when clicked
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetListView, appPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
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