package com.boardactive.bakit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GeofenceLocationModel {

    @SerializedName("data")
    @Expose
    private ArrayList<GeoData> data = null;
    @SerializedName("unFilteredCount")
    @Expose
    private String unFilteredCount;
    @SerializedName("app_id")
    @Expose
    private Integer appId;

    public ArrayList<GeoData> getData() {
        return data;
    }

    public void setData(ArrayList<GeoData> data) {
        this.data = data;
    }

    public String getUnFilteredCount() {
        return unFilteredCount;
    }

    public void setUnFilteredCount(String unFilteredCount) {
        this.unFilteredCount = unFilteredCount;
    }

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }



}


