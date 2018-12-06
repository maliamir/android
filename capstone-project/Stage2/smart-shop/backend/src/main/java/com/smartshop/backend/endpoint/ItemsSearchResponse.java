package com.smartshop.backend.endpoint;

import com.google.api.server.spi.response.CollectionResponse;

public class ItemsSearchResponse {

    private int itemsCount;

    private CollectionResponse<ProductsSearchResponse> itemsSearched;

    public ItemsSearchResponse(CollectionResponse<ProductsSearchResponse> itemsSearched) {
        this.itemsSearched = itemsSearched;
        this.itemsCount = this.itemsSearched.getItems().size();
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public CollectionResponse<ProductsSearchResponse> getItemsSearched() {
        return itemsSearched;
    }

}