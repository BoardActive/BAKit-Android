package com.boardactive.bakit.models;

import java.util.Date;

public class Stock {
    private String name;
    private String email;
    private String phone;
    private String dateBorn;
    private String gender;
    private String facebookUrl;
    private String linkedInUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String avatarUrl;
    private String locationPermission;
    private String notificationPermission;

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateBorn() {
        return dateBorn;
    }

    public void setDateBorn(String dateBorn) {
        this.dateBorn = dateBorn;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getLinkedInUrl() {
        return linkedInUrl;
    }

    public void setLinkedInUrl(String linkedInUrl) {
        this.linkedInUrl = linkedInUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(String locationPermission) {
        this.locationPermission = locationPermission;
    }

    public String getNotificationPermission() {
        return notificationPermission;
    }

    public void setNotificationPermission(String notificationPermission) {
        this.notificationPermission = notificationPermission;
    }

}