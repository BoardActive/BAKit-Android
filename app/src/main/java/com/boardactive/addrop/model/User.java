package com.boardactive.addrop.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class User {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("appId")
    @Expose
    private String appId;

    @SerializedName("appVersion")
    @Expose
    private String appVersion;

    @SerializedName("deviceOS")
    @Expose
    private String deviceOS;

    @SerializedName("deviceOSVersion")
    @Expose
    private String deviceOSVersion;

    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    @SerializedName("attributes")
    @Expose
    private JSONObject attributes;

    @SerializedName("guid")
    @Expose
    private String guid;

    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;

    @SerializedName("dateLastUpdated")
    @Expose
    private String dateLastUpdated;

    @SerializedName("dateLaseOpenedApp")
    @Expose
    private String dateLaseOpenedApp;

    /**
     * No args constructor for use in serialization
     */
    public User() {
    }

    /**
     * @param id
     * @param appId
     * @param appVersion
     * @param deviceOS
     * @param deviceOSVersion
     * @param deviceToken
     * @param attributes
     * @param guid
     * @param dateCreated
     * @param dateLastUpdated
     * @param dateLaseOpenedApp
     */
    public User(
            String id,
            String appId,
            String appVersion,
            String deviceOS,
            String deviceOSVersion,
            String deviceToken,
            JSONObject attributes,
            String guid,
            String dateCreated,
            String dateLastUpdated,
            String dateLaseOpenedApp
    ) {
        super();
        this.id = id;
        this.appId = appId;
        this.appVersion = appVersion;
        this.deviceOS = deviceOS;
        this.deviceOSVersion = deviceOSVersion;
        this.deviceToken = deviceToken;
        this.attributes = attributes;
        this.guid = guid;
        this.dateCreated = dateCreated;
        this.dateLastUpdated = dateLastUpdated;
        this.dateLaseOpenedApp = dateLaseOpenedApp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }

    public void setDeviceOSVersion(String deviceOSVersion) {
        this.deviceOSVersion = deviceOSVersion;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public JSONObject getAttributes() {
        return attributes;
    }

    public void setAttributes(JSONObject attributes) {
        this.attributes = attributes;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public String getDateLaseOpenedApp() {
        return dateLaseOpenedApp;
    }

    public void setDateLaseOpenedApp(String dateLaseOpenedApp) {
        this.dateLaseOpenedApp = dateLaseOpenedApp;
    }

}


