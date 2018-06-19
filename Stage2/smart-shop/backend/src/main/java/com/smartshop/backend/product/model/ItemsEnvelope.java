package com.smartshop.backend.product.model;

import java.util.List;

public class ItemsEnvelope {

    private List<ItemInfo> itemInfoList;

    public List<ItemInfo> getItemInfoList() {
        return itemInfoList;
    }

    public void setItemInfoList(List<ItemInfo> itemInfoList) {
        this.itemInfoList = itemInfoList;
    }

}