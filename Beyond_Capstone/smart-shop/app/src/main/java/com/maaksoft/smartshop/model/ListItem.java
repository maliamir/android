package com.maaksoft.smartshop.model;

import java.io.Serializable;

public class ListItem implements Serializable {

    public enum SortType {

        HIGHEST_RAIINGS(1),
        LOWEST_PRICE(2);

        private final int value;

        SortType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    private int limit;
    private int sortType = SortType.HIGHEST_RAIINGS.getValue();

    private long listItemId;
    private long shopListId;

    private String itemName;

    public ListItem() {
    }

    public ListItem(String itemName, int limit, SortType sortType) {
        this.itemName = itemName;
        this.limit = limit;
        this.sortType = sortType.getValue();
    }

    public long getListItemId() {
        return listItemId;
    }

    public void setListItemId(long listItemId) {
        this.listItemId = listItemId;
    }

    public long getShopListId() {
        return shopListId;
    }

    public void setShopListId(long shopListId) {
        this.shopListId = shopListId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("List Item Details:\n");
        stringBuilder.append("\tItem Name = " + itemName + "\n");
        stringBuilder.append("\tLimit = " + limit + "\n");
        stringBuilder.append("\tSort Type = " + sortType + "\n");

        return stringBuilder.toString();

    }

}