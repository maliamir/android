package com.smartshop.backend.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;

import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

import com.google.api.server.spi.response.CollectionResponse;

import com.smartshop.backend.product.model.ItemInfo;
import com.smartshop.backend.product.model.ItemsEnvelope;
import com.smartshop.backend.product.model.Product;

import com.smartshop.backend.product.ProductComparator;
import com.smartshop.backend.product.ProductsResolver;

@Api(
        name = "products",
        version = "v1",
        description = "API to fetch matching Products using Walmart REST API.",
        namespace = @ApiNamespace(ownerDomain = "backend.smartshop.com", ownerName = "backend.smartshop.com")

)
public class ProductsEndpoint {

    private String getStackTrace(Throwable throwable) {

        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();

    }

    @ApiMethod(name = "search", description = "Fetches matching Products using Walmart REST API and sort them either by lowest price or highest ratings.",
               httpMethod = HttpMethod.POST)
    public ItemsSearchResponse search(@Named("storeId") String storeId, ItemsEnvelope itemsEnvelope) throws IOException {

        List<ItemInfo> itemInfoList = itemsEnvelope.getItemInfoList();
        List<ProductsSearchResponse> itemsSearchResponses = new LinkedList<ProductsSearchResponse>();
        if (itemInfoList != null && itemInfoList.size() > 0) {

            for (ItemInfo itemInfo : itemInfoList) {

                List<Product> productsList = ProductsResolver.getProducts(storeId, itemInfo.getItemName());
                if (itemInfo.getLimit() > 0 && itemInfo.getLimit() < productsList.size()) {
                    productsList = productsList.subList(0, itemInfo.getLimit());
                }

                String sortBy = itemInfo.getSortBy();
                sortBy = ((sortBy == null || (sortBy = sortBy.trim()).isEmpty()) ? ProductComparator.LOWEST_PRICE_COMPARATOR_TYPE : sortBy);
                sortBy = ((!sortBy.equalsIgnoreCase(ProductComparator.LOWEST_PRICE_COMPARATOR_TYPE) && !sortBy.equalsIgnoreCase(ProductComparator.HIGHST_RATINGS_COMPARATOR_TYPE))
                          ? ("Defaulted to: " + ProductComparator.LOWEST_PRICE_COMPARATOR_TYPE) : sortBy);

                Collections.sort(productsList, ProductComparator.getComparator(sortBy));
                itemsSearchResponses.add(new ProductsSearchResponse(itemInfo.getItemName(), sortBy,
                                         CollectionResponse.<Product> builder().setItems(productsList).build()));

            }

        }

        return new ItemsSearchResponse(CollectionResponse.<ProductsSearchResponse> builder().setItems(itemsSearchResponses).build());

    }

}