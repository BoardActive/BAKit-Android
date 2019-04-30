package com.boardactive.bakit.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("appId")
    @Expose
    private Integer appId;

    @SerializedName("appVersion")
    @Expose
    private String appVersion;

    @SerializedName("webUserId")
    @Expose
    private Integer webUserId;

    @SerializedName("deviceOS")
    @Expose
    private String deviceOS;

    @SerializedName("deviceOSVersion")
    @Expose
    private String deviceOSVersion;

    @SerializedName("deviceToken")
    @Expose
    private String deviceToken;

    @SerializedName("guid")
    @Expose
    private String guid;

    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;

    @SerializedName("dateLastUpdated")
    @Expose
    private String dateLastUpdated;

    @SerializedName("inbox")
    @Expose
    private JsonObject inbox;

    @SerializedName("email")
    @Expose
    private String email;

    /**
     * No args constructor for use in serialization
     */
    public User() {
    }

    /**
     * @param id
     * @param appId
     * @param appVersion
     * @param webUserId
     * @param deviceOS
     * @param deviceOSVersion
     * @param deviceToken
     * @param guid
     * @param dateCreated
     * @param dateLastUpdated
     * @param inbox
     * @param email
     */

    public User(
            Integer id,
            Integer appId,
            String appVersion,
            Integer webUserId,
            String deviceOS,
            String deviceOSVersion,
            String deviceToken,
            String guid,
            String dateCreated,
            String dateLastUpdated,
            JsonObject inbox,
            String email
    ) {
        super();
        this.id = id;
        this.appId = appId;
        this.appVersion = appVersion;
        this.webUserId = webUserId;
        this.deviceOS = deviceOS;
        this.deviceOSVersion = deviceOSVersion;
        this.deviceToken = deviceToken;
        this.guid = guid;
        this.dateCreated = dateCreated;
        this.dateLastUpdated = dateLastUpdated;
        this.inbox = inbox;
        this.email = email;
    }

    public Integer getid() { return id; }

    public void setid(Integer id) {
        this.id = id;
    }

    public Integer getappId() { return appId; }

    public void setappId(Integer appId) {
        this.appId = appId;
    }

    public String getappVersion() { return appVersion; }

    public void setappVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Integer getwebUserId() { return webUserId; }

    public void setwebUserId(Integer webUserId) {
        this.webUserId = webUserId;
    }

    public String getdeviceOS() { return deviceOS; }

    public void setdeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getdeviceOSVersion() { return deviceOSVersion; }

    public void setdeviceOSVersion(String deviceOSVersion) {
        this.deviceOSVersion = deviceOSVersion;
    }

    public String getdeviceToken() { return deviceToken; }

    public void setdeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getguid() { return guid; }

    public void setguid(String guid) {
        this.guid = guid;
    }

    public String getdateCreated() { return dateCreated; }

    public void setdateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getdateLastUpdated() { return dateLastUpdated; }

    public void setdateLastUpdated(String dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public JsonObject getinbox() { return inbox; }

    public void setinbox(JsonObject inbox) {
        this.inbox = inbox;
    }

    public String getemail() { return email; }

    public void setemail(String email) {
        this.email = email;
    }

}
