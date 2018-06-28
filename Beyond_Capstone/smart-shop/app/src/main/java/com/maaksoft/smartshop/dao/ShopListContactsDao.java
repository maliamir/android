package com.maaksoft.smartshop.dao;

import java.util.List;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.maaksoft.smartshop.model.ShopListContact;

@Dao
public interface ShopListContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addShopListContact(ShopListContact Contact);

    @Query("SELECT contactId FROM shop_list_contacts WHERE shopListId=:shopListId")
    List<Long> findContactIdsByShopList(long shopListId);

    @Query("SELECT DISTINCT contactId FROM shop_list_contacts WHERE shopListId=:shopListId AND contactId=:contactId")
    List<Long> findShopListContacts(long shopListId, long contactId);

    @Query("DELETE FROM shop_list_contacts WHERE shopListId=:shopListId")
    void deleteContactsByShopList(long shopListId);

}