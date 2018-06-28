package com.maaksoft.smartshop.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "stores", indices = { @Index(value = "storeId") })
public class Store {

    @PrimaryKey(autoGenerate = true)
    private long storeId;

    private int walmartStoreId;

    private String name;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    public Store() {
    }

    @Ignore
    public Store(int walmartStoreId, String name, String streetAddress, String city, String state, String zipCode) {
        this.walmartStoreId = walmartStoreId;
        this.name = name;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public long getStoreId() {
        return storeId;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public int getWalmartStoreId() {
        return walmartStoreId;
    }

    public void setWalmartStoreId(int walmartStoreId) {
        this.walmartStoreId = walmartStoreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Store Details:\n");
        stringBuilder.append("\tName = " + name + "; Id = " + storeId + "\n");
        stringBuilder.append("\tWalmart Store Id = " + walmartStoreId + "\n");
        stringBuilder.append("\tStreet Address = " + streetAddress + "\n");
        stringBuilder.append("\tCity = " + city + "\n");
        stringBuilder.append("\tState = " + state + "\n");
        stringBuilder.append("\tZip Code = " + zipCode + "\n");

        return stringBuilder.toString();

    }

}