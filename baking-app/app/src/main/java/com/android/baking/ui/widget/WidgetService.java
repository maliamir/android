package com.android.baking.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.baking.R;
import com.android.baking.db.Database;
import com.android.baking.model.Ingredient;

import java.util.ArrayList;

public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

    class ListProvider implements RemoteViewsFactory  {

        int appWidgetId;
        ArrayList<Ingredient> ingredients;

        Context context;

        ListProvider(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {

            ingredients = new ArrayList();

            Database database = new Database(context);
            ingredients = database.getIngredients(appWidgetId);

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.baking_widget_item);

            remoteViews.setTextViewText(R.id.widget_recipe_name, ingredients.get(position).getIngredient());
            remoteViews.setTextViewText(R.id.widget_recipe_measure, ingredients.get(position).getQuantity() + " " + ingredients.get(position).getMeasure());

            return remoteViews;

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
