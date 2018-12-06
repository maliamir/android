package com.smartshop.backend.product.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemInfo {

    private int limit;

    private String itemName;
    private String sortBy;

    @JsonCreator
    public ItemInfo(@JsonProperty("itemName") String itemName, @JsonProperty("sortBy") String sortBy, @JsonProperty("limit") int limit) {
        this.itemName = itemName;
        this.sortBy = sortBy;
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

}
