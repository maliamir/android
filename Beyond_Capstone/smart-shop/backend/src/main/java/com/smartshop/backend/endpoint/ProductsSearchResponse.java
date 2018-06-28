package com.smartshop.backend.endpoint;

import com.google.api.server.spi.response.CollectionResponse;

import com.smartshop.backend.product.model.Product;

public class ProductsSearchResponse {

    private int productsCount;

    private String itemName;
    private String sortedBy;

    private CollectionResponse<Product> productsFound;

    public ProductsSearchResponse(String itemName, String sortedBy, CollectionResponse<Product> productsFound) {
        this.itemName = itemName;
        this.sortedBy = sortedBy;
        this.productsFound = productsFound;
        this.productsCount = this.productsFound.getItems().size();
    }

    public String getItemName() {
        return itemName;
    }

    public String getSortedBy() {
        return sortedBy;
    }

    public int getProductsCount() {
        return productsCount;
    }

    public CollectionResponse<Product> getProductsFound() {
        return productsFound;
    }

}