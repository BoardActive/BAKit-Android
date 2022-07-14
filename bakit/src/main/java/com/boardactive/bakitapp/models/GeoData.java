package com.boardactive.bakitapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GeoData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("radius")
    @Expose
    private Integer radius;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("coordinates")
    @Expose
    private ArrayList<Coordinate> coordinates = null;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("dateLastUpdated")
    @Expose
    private String dateLastUpdated;
    @SerializedName("dateTrashed")
    @Expose
    private Object dateTrashed;
    @SerializedName("isTrashed")
    @Expose
    private Boolean isTrashed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Coordinate> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(String dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Object getDateTrashed() {
        return dateTrashed;
    }

    public void setDateTrashed(Object dateTrashed) {
        this.dateTrashed = dateTrashed;
    }

    public Boolean getIsTrashed() {
        return isTrashed;
    }

    public void setIsTrashed(Boolean isTrashed) {
        this.isTrashed = isTrashed;
    }
}
