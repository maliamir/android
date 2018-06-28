package com.maaksoft.smartshop.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "shop_list_contacts", indices = { @Index(value = "shopListContactId") })
public class ShopListContact {

    @PrimaryKey(autoGenerate = true)
    private long shopListContactId;

    private long shopListId;
    private long contactId;

    public ShopListContact() {
    }

    @Ignore
    public ShopListContact(long shopListId, long contactId) {
        this.shopListId = shopListId;
        this.contactId = contactId;
    }

    public long getShopListContactId() {
        return shopListContactId;
    }

    public void setShopListContactId(long shopListContactId) {
        this.shopListContactId = shopListContactId;
    }

    public long getShopListId() {
        return shopListId;
    }

    public void setShopListId(long shopListId) {
        this.shopListId = shopListId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Shop List Contacts Details:\n");
        stringBuilder.append("\tShopList Id = " + shopListId + "\n");
        stringBuilder.append("\tContact Id = " + contactId + "\n");

        return stringBuilder.toString();

    }

}