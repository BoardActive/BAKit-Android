package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdDropLatLng {
    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("deviceTimeString")
    @Expose
    private String deviceTimeString;

    /**
     * No args constructor for use in serialization
     */
    public AdDropLatLng() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     */
    public AdDropLatLng(
            String lat,
            String lng,
            String deviceTimeString
    ) {
        super();
        this.lat = lat;
        this.lng = lng;
        this.deviceTimeString = deviceTimeString;
    }

    public String getLat() { return lat; }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDeviceTimeString() {
        return deviceTimeString;
    }

    public void setDeviceTimeString(String deviceTimeString) {
        this.deviceTimeString = deviceTimeString;
    }
}