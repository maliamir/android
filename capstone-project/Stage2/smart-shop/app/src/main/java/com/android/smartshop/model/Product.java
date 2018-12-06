package com.android.smartshop.model;

import java.text.ParseException;
import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private String name;
    private String aisleLocation;
    private String currencyUnit = "USD";

    private Double price = 0.0;
    private Double ratings;

    private String imageUrl;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Product createFromParcel(Parcel parcel) {
            return new Product(parcel);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }

    };

    public Product(Parcel parcel){
        this.name = parcel.readString();
        this.aisleLocation =  parcel.readString();
        this.currencyUnit = parcel.readString();
        this.price =  parcel.readDouble();
        this.ratings = parcel.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.name);
        parcel.writeString(this.aisleLocation);
        parcel.writeString(this.currencyUnit);

        if (this.price != null) {
            parcel.writeDouble(this.price);
        }

        if (this.ratings != ratings) {
            parcel.writeDouble(this.ratings);
        }

    }

    public Product(String name, String aisleLocation, Double price, Double ratings, String imageUrl) {

        this.name = name;
        this.aisleLocation = aisleLocation;
        this.price = price;

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

    public String getAisleLocation() {
        return aisleLocation;
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
        stringBuilder.append("\tAisle Location = " + aisleLocation + "\n");
        stringBuilder.append("\tPrice = " + currencyUnit + " " + price + "\n");
        stringBuilder.append("\tRatings = " + ratings + "\n");
        stringBuilder.append("\tImage URL = " + imageUrl + "\n");

        return stringBuilder.toString();

    }

}