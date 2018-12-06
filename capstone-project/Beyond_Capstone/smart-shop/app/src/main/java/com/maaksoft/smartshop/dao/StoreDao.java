package com.maaksoft.smartshop.dao;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.maaksoft.smartshop.model.Store;

@Dao
public interface StoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addStore(Store store);

    @Query("SELECT * FROM stores")
    List<Store> findStores();

    @Query("SELECT * FROM stores WHERE storeId=:storeId")
    Store findStoreById(long storeId);

    @Query("SELECT * FROM stores WHERE walmartStoreId=:walmartStoreId")
    Store findStoreByWalmartStoreId(int walmartStoreId);

    @Query("DELETE FROM stores WHERE storeId=:storeId")
    void deleteStore(long storeId);

}