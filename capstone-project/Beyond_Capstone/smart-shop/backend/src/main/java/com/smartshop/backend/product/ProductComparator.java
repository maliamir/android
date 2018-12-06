package com.smartshop.backend.product;

import java.util.Comparator;

import com.smartshop.backend.product.model.Product;

public class ProductComparator {

    public static String LOWEST_PRICE_COMPARATOR_TYPE = "lowest_price";
    public static String HIGHST_RATINGS_COMPARATOR_TYPE = "highest_ratings";

    public static Comparator<Product> getComparator(final String productComparatorType) {

        return new Comparator<Product>() {

            public int compare(Product product1, Product product2) {

                if(productComparatorType == null || productComparatorType.trim().isEmpty() ||
                   productComparatorType.trim().equalsIgnoreCase(LOWEST_PRICE_COMPARATOR_TYPE)) {
                    return Double.compare(product1.getPrice(), product2.getPrice());
                } else {
                    return Double.compare(product2.getRatings(), product1.getRatings());
                }

            }

        };

    }

}
