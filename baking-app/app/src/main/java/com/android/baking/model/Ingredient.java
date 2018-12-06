package com.android.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ingredient implements Parcelable {

    @SerializedName("quantity")
    @Expose
    private float quantity;

    @SerializedName("measure")
    @Expose
    private String measure;

    @SerializedName("ingredient")
    @Expose
    private String ingredient;

    public Ingredient() {
    }

    protected Ingredient(Parcel parcel) {
        quantity = parcel.readFloat();
        measure = parcel.readString();
        ingredient = parcel.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {

        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }

    };

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeFloat(quantity);
        parcel.writeString(measure);
        parcel.writeString(ingredient);
    }

}