package com.smartshop.backend.product.model;

import java.text.ParseException;
import java.text.DecimalFormat;

public class Product {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private String name;
    private String location;
    private String currencyUnit = "USD";

    private Double price = 0.0;
    private Double ratings;

    private String imageUrl;

    public Product(String name, String aisle, Integer section, Long price, Double ratings, String imageUrl) {

        this.name = name;
        this.location = (aisle + " Section " + section);

        if (price > 0) {
            this.price = (((double) price) / 100);
        }

        try {
            this.ratings = DECIMAL_FORMAT.parse(DECIMAL_FORMAT.format(ratings)).doubleValue();
        } catch (ParseException pe) {
            pe.printStackTrace();
            this.ratings = ratings;
        }

        this.imageUrl = imageUrl;

    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public Double getPrice() {
        return price;
    }

    public Double getRatings() {
        return ratings;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }
    
    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Product Details:\n");
        stringBuilder.append("\tName = " + name + "\n");
        stringBuilder.append("\tPrice = " + currencyUnit + " " + price + "\n");
        stringBuilder.append("\tRatings = " + ratings + "\n");
        stringBuilder.append("\tLocation = " + location + "\n");
        stringBuilder.append("\tImage URL = " + imageUrl + "\n");

        return stringBuilder.toString();

    }

}