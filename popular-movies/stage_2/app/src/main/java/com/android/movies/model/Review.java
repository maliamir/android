package com.android.movies.model;

import android.os.Parcelable;
import android.os.Parcel;

public class Review implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }

    };

    private String author;
    private String content;
    private String subContent;
    private String url;

    public Review(Parcel parcel) {
        this.author = parcel.readString();
        this.content = parcel.readString();
        this.subContent = parcel.readString();
        this.url = parcel.readString();
    }

    public Review(String author, String content, String url) {

        this.author = author;
        this.content = content;
        this.url = url;

        if (content == null || (content = content.trim()).isEmpty()) {
            content = "Reviewed by " + this.author;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.author);
        parcel.writeString(this.content);
        parcel.writeString(this.subContent);
        parcel.writeString(this.url);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubContent(int orientation) {

        if (content != null && !content.isEmpty()) {

            int subContentThreshold = ((orientation == 1) ? 50 : 100);
            if ((content = content.trim()).length() > subContentThreshold) {
                this.subContent = content.substring(0, subContentThreshold) + " ...";
            } else {
                this.subContent = content;
            }
            return  this.subContent;

        } else {
            return "";
        }

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Review Details:\n");
        stringBuilder.append("\tAuthor = " + author + "\n");
        stringBuilder.append("\tContent = " + content + "\n");
        stringBuilder.append("\tURL = " + url);

        return stringBuilder.toString();

    }

}