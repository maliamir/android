package com.maaksoft.smartshop.model;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemInfo implements Parcelable {

    private String itemName;

    private List <Product> products;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Product createFromParcel(Parcel parcel) {
            return new Product(parcel);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }

    };

    public ItemInfo(Parcel parcel){
        this.itemName = parcel.readString();
        //this.products =  parcel.readList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(this.itemName);
        parcel.writeList(this.products);

    }

    public ItemInfo(String itemName, List <Product> products) {
        this.itemName = itemName;
        this.products = products;
    }

    public String getItemName() {
        return itemName;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Item Details:\n");
        stringBuilder.append("\tItem Name = " + itemName + "\n");
        stringBuilder.append("\tFound Products = " + products + "\n");

        return stringBuilder.toString();

    }

}