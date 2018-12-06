package com.maaksoft.smartshop.widget;

import java.util.List;

import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import android.appwidget.AppWidgetManager;

import com.maaksoft.smartshop.model.Store;
import com.maaksoft.smartshop.model.ShopList;

import com.maaksoft.smartshop.service.SmartShopService;

import com.maaksoft.smartshop.R;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

    class ListProvider implements RemoteViewsFactory  {

        int appWidgetId;

        List<Store> stores;
        List<ShopList> shopLists;

        Context context;

        SmartShopService smartShopService;

        ListProvider(Context context, Intent intent) {
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
            this.context = context;
            this.smartShopService = new SmartShopService();
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public void onDataSetChanged() {
            this.stores = this.smartShopService.getStores(this.context);
            this.shopLists = this.smartShopService.getShopLists(this.context);
        }

        @Override
        public int getCount() {
            return shopLists.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.smart_shop_widget_items);

            remoteViews.setTextViewText(R.id.widget_stores, ("" + this.stores.size() + " " + context.getString(R.string.stores_info)));
            remoteViews.setTextViewText(R.id.widget_shop_lists, ("" + this.shopLists.size() + " " + context.getString(R.string.shop_lists_info)));

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