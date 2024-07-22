package com.boardactive.bakit.models;

public class MeRequest {

    private String email;
    private String deviceOS;
    private String deviceOSVersion;
    private Attributes attributes;
    private String dateLastOpenedApp;


    public String getEmail() {
        return email;
    }

    public void setEmail(String id) {
        this.email = email;
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

    public void setDateLastOpenedApp(String dateLastOpenedApp) {
        this.dateLastOpenedApp = dateLastOpenedApp;
    }

    public String getDateLastOpenedApp() {
        return dateLastOpenedApp;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

}
