package com.maaksoft.smartshop.model;

import java.util.List;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

@Entity(tableName = "shop_lists", indices = { @Index(value = "shopListId") })
@TypeConverters(ListItemConverter.class)
public class ShopList {

    @PrimaryKey(autoGenerate = true)
    private long shopListId;

    private String name;

    private List<ListItem> listItems;

    public ShopList() {
    }

    @Ignore
    public ShopList(String name, List<ListItem> listItems) {
        this.name = name;
        this.listItems = listItems;
    }

    public long getShopListId() {
        return shopListId;
    }

    public void setShopListId(long shopListId) {
        this.shopListId = shopListId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ListItem> getListItems() {
        return listItems;
    }

    public void setListItems(List<ListItem> listItems) {
        this.listItems = listItems;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Shop List Details:\n");
        stringBuilder.append("\tList Name = " + name + "; Id = " + shopListId + "\n");
        stringBuilder.append("\t" + listItems.size() + " List Items:\n" + listItems + "\n");

        return stringBuilder.toString();

    }

}