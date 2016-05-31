package com.companyname.ceramicgod;

/**
 * Created by David on 5/31/2016.
 */
public class Review {

    private String name;
    private int rating;
    private String date;
    private String commments;
    private float latitude;
    private float longitude;
    private String picture;

    public Review(String name, int rating, String date, String commments, float latitude, float longitude, String picture) {
        this.name = name;
        this.rating = rating;
        this.date = date;
        this.commments = commments;
        this.latitude = latitude;
        this.longitude = longitude;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCommments() {
        return commments;
    }

    public void setCommments(String commments) {
        this.commments = commments;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
