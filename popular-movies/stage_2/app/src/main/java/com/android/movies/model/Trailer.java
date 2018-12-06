package com.android.movies.model;

import android.os.Parcelable;
import android.os.Parcel;

public class Trailer implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Trailer createFromParcel(Parcel parcel) {
            return new Trailer(parcel);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }

    };

    private String name;
    private String key;
    private String site;

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Trailer(Parcel parcel) {
        this.name = parcel.readString();
        this.key = parcel.readString();
        this.site = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.name);
        parcel.writeString(this.key);
        parcel.writeString(this.site);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Movie Trailer Details:\n");
        stringBuilder.append("\tName = " + name + "\n");
        stringBuilder.append("\tKey = " + key + "\n");
        stringBuilder.append("\tSite = " + site);

        return stringBuilder.toString();

    }

}