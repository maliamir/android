package com.android.smartshop.service;

import java.util.List;

import android.content.Context;

import com.android.smartshop.db.SmartShopDatabase;

import com.android.smartshop.model.Store;
import com.android.smartshop.model.ShopList;
import com.android.smartshop.model.Setting;

public class SmartShopService {

    public void addShopList(Context context, ShopList shopList) {
        SmartShopDatabase.getDatabase(context).shopListDao().addShopList(shopList);
    }

    public List<ShopList> getShopLists(Context context) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopLists();
    }

    public ShopList getShopListById(Context context, long shopListId) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopListById(shopListId);
    }

    public ShopList getShopListByName(Context context, String name) {
        return SmartShopDatabase.getDatabase(context).shopListDao().findShopListByName(name);
    }

    public void deleteShopList(Context context, long shopListId) {
        SmartShopDatabase.getDatabase(context).shopListDao().deleteShopList(shopListId);
    }

    public void addStore(Context context, Store store) {
        SmartShopDatabase.getDatabase(context).storeDao().addStore(store);
    }

    public List<Store> getStores(Context context) {
        return SmartShopDatabase.getDatabase(context).storeDao().findStores();
    }

    public Store findStoreByWalmartStoreId(Context context, int walmartStoreId) {
        return SmartShopDatabase.getDatabase(context).storeDao().findStoreByWalmartStoreId(walmartStoreId);
    }

    public void deleteStore(Context context, long storeId) {
        SmartShopDatabase.getDatabase(context).storeDao().deleteStore(storeId);
    }

    public void addSetting(Context context, Setting setting) {
        SmartShopDatabase.getDatabase(context).settingDao().addSetting(setting);
    }

    public List<Setting> getSettings(Context context) {
        return SmartShopDatabase.getDatabase(context).settingDao().findSettings();
    }

    public Setting getSettingById(Context context, long settingId) {
        return SmartShopDatabase.getDatabase(context).settingDao().findSettingById(settingId);
    }

    public void deleteSetting(Context context, long settingId) {
        SmartShopDatabase.getDatabase(context).settingDao().deleteSetting(settingId);
    }

}