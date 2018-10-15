package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdDropRegister {
    @SerializedName("firebaseProjectId")
    @Expose
    private String firebaseProjectId;

    @SerializedName("firebaseClientEmail")
    @Expose
    private String firebaseClientEmail;

    @SerializedName("bundleIdentifier")
    @Expose
    private String bundleIdentifier;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("advertiser_id")
    @Expose
    private Integer advertiser_id;

    @SerializedName("firebasePrivateKey")
    @Expose
    private String firebasePrivateKey;

    /**
     * No args constructor for use in serialization
     */
    public AdDropRegister() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     */
    public AdDropRegister(
            String firebaseProjectId,
            String firebaseClientEmail,
            String bundleIdentifier,
            String name,
            Integer advertiser_id,
            String firebasePrivateKey
    ) {
        super();
        this.firebaseProjectId = firebaseProjectId;
        this.firebaseClientEmail = firebaseClientEmail;
        this.bundleIdentifier = bundleIdentifier;
        this.name = name;
        this.advertiser_id = advertiser_id;
        this.firebasePrivateKey = firebasePrivateKey;
    }

    public String getFirebaseProejctId() { return firebaseProjectId; }

    public void setFirebaseProjectId(String firebaseProejctId) {
        this.firebaseProjectId = firebaseProejctId;
    }

    public String getFirebaseClientEmail() {
        return firebaseClientEmail;
    }

    public void setFirebaseClientEmail(String firebaseClientEmail) {
        this.firebaseClientEmail = firebaseClientEmail;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier(String bundleIdentifier) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAdvertiser_id() {
        return advertiser_id;
    }

    public void setAdvertiser_id(Integer advertiser_id) {
        this.advertiser_id = advertiser_id;
    }

    public String getFirebasePrivateKey() {
        return firebasePrivateKey;
    }

    public void setFirebasePrivateKey(String firebasePrivateKey) {
        this.firebasePrivateKey = firebasePrivateKey;
    }
}
