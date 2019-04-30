package com.boardactive.bakit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Location implements Serializable {
    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("deviceTime")
    @Expose
    private String deviceTime;

    /**
     * No args constructor for use in serialization
     */
    public Location() {
    }

    /**
     * @param latitude
     * @param longitude
     * @param deviceTime
     */
    public Location(
            String latitude,
            String longitude,
            String deviceTime
    ) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceTime = deviceTime;
    }

    public String getlatitude() { return latitude; }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getlongitude() {
        return longitude;
    }

    public void setlongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getdeviceTime() {
        return deviceTime;
    }

    public void setdeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }
}
