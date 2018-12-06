package com.android.smartshop.db;

import android.content.Context;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.android.smartshop.dao.SettingDao;
import com.android.smartshop.dao.StoreDao;
import com.android.smartshop.dao.ShopListDao;

import com.android.smartshop.model.Setting;
import com.android.smartshop.model.Store;
import com.android.smartshop.model.ShopList;

@Database(entities = { ShopList.class, Store.class, Setting.class }, version = 4, exportSchema = false)
public abstract class SmartShopDatabase extends RoomDatabase {

    private static SmartShopDatabase smartShopDatabase;

    public abstract ShopListDao shopListDao();

    public abstract StoreDao storeDao();

    public abstract SettingDao settingDao();

    public static SmartShopDatabase getDatabase(Context context) {

        if (smartShopDatabase == null) {
            smartShopDatabase = Room.databaseBuilder(context, SmartShopDatabase.class, "smart_shop")
                                //Room.inMemoryDatabaseBuilder(context.getApplicationContext(), SmartShopDatabase.class)
                                // To simplify the exercise, allow queries on the main thread.
                                // Don't do this on a real app!
                                .allowMainThreadQueries()
                                // recreate the database if necessary
                                .fallbackToDestructiveMigration()
                                .build();
        }
        return smartShopDatabase;

    }

    public static void destroyInstance() {
        smartShopDatabase = null;
    }

}