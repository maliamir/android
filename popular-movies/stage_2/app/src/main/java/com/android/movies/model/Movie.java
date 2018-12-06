package com.android.movies.model;

import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private static final String oldPattern = "yyyy-MM-dd";
    private static final String newPattern = "MM/dd/yyyy";

    private static final SimpleDateFormat oldDateFormat = new SimpleDateFormat(oldPattern);
    private static final SimpleDateFormat newDateFormat = new SimpleDateFormat(newPattern);

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };

    private Long id;

    private Double averageVote;

    private String originalTitle;
    private String moviePosterImageUrl;
    private String plotSynopsis;
    private String releaseDate;

    public Movie(Parcel parcel){
        this.id = parcel.readLong();
        this.originalTitle = parcel.readString();
        this.moviePosterImageUrl =  parcel.readString();
        this.plotSynopsis = parcel.readString();
        this.averageVote =  parcel.readDouble();
        this.releaseDate = parcel.readString();
    }

    public Movie(Long id, String originalTitle, String moviePosterImageUrl, String plotSynopsis, Double averageVote, String releaseDate) {

        this.id = id;
        this.originalTitle = originalTitle;
        this.moviePosterImageUrl = moviePosterImageUrl;
        this.plotSynopsis = plotSynopsis;
        this.averageVote = averageVote;
        this.releaseDate = releaseDate;

        if (this.releaseDate != null && (this.releaseDate = this.releaseDate.trim()).length() == 10) {

            try {
                Date date = oldDateFormat.parse(this.releaseDate);
                this.releaseDate = newDateFormat.format(date);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }

        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeLong(this.id);
        parcel.writeString(this.originalTitle);
        parcel.writeString(this.moviePosterImageUrl);
        parcel.writeString(this.plotSynopsis);

        if (this.averageVote != null) {
            parcel.writeDouble(this.averageVote);
        }

        parcel.writeString(this.releaseDate);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMoviePosterImageUrl() {
        return moviePosterImageUrl;
    }

    public void setMoviePosterImageUrl(String moviePosterImageUrl) { this.moviePosterImageUrl = moviePosterImageUrl; }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public void setAverageVote(Double averageVote) {
        this.averageVote = averageVote;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPlotSynopsis() { return plotSynopsis; }

    public Double getAverageVote() {
        return averageVote;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Movie Details:\n");
        stringBuilder.append("\tOriginal Title = " + originalTitle + "\n");
        stringBuilder.append("\tRelease Date = " + releaseDate + "\n");
        stringBuilder.append("\tMovie Poster Image URL = " + moviePosterImageUrl + "\n");
        stringBuilder.append("\tAverage Vote = " + averageVote + "\n");
        stringBuilder.append("\tPlot Synopsis = " + plotSynopsis);

        return stringBuilder.toString();

    }

}