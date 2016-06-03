package com.companyname.ceramicgod;

import java.net.URL;

/**
 * Created by David on 6/3/2016.
 */
public class DataModel {

    private int id;
    private String name;
    private float rating;
    private String date;
    private String comment;
    private float latitude;
    private float longitude;
    private String address;
    private URL img_url;

    public DataModel(int id, String name, float rating, String date, String comment, float latitude, float longitude, String address, URL img_url) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.date = date;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.img_url = img_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public URL getImg_url() {
        return img_url;
    }

    public void setImg_url(URL img_url) {
        this.img_url = img_url;
    }
}
