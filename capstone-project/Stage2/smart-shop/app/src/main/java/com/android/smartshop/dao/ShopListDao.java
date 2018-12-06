package com.android.smartshop.dao;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.android.smartshop.model.ShopList;

@Dao
public interface ShopListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addShopList(ShopList shopList);

    @Query("SELECT * FROM shop_lists")
    List<ShopList> findShopLists();

    @Query("SELECT * FROM shop_lists WHERE shopListId=:shopListId")
    ShopList findShopListById(long shopListId);

    @Query("SELECT * FROM shop_lists WHERE name=:name ORDER BY shopListId DESC LIMIT 1")
    ShopList findShopListByName(String name);

    @Query("DELETE FROM shop_lists WHERE shopListId=:shopListId")
    void deleteShopList(long shopListId);

}