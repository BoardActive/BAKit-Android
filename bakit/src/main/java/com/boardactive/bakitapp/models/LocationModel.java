package com.boardactive.bakitapp.models;

public class LocationModel {
    private double latitude;
    private double longitude;
    private String locationId;
    private String lastNotificationDate;
    private float radius;

    //Getters and setters
    /**
     * No args constructor for use in serialization
     */
    public LocationModel() {
    }

    /**
     * @param latitude
     * @param longitude
     * @param locationId
     */
    public LocationModel(
            double latitude,
            double longitude,
            String locationId,
            String lastNotificationDate,
            float radius

    ) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
        this.lastNotificationDate = lastNotificationDate;
        this.radius = radius;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setLastNotificationDate(String date) {
        this.lastNotificationDate = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLastNotificationDate() {
        return lastNotificationDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
