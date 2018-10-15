package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdDropEventParams {

    @SerializedName("promotion_id")
    @Expose
    private String promotion_id;

    @SerializedName("advertisement_id")
    @Expose
    private String advertisement_id;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

    /**
     * No args constructor for use in serialization
     */
    public AdDropEventParams() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     */
    public AdDropEventParams(
            String promotion_id,
            String advertisement_id,
            String firebaseNotificationId
    ) {
        super();
        this.promotion_id = promotion_id;
        this.advertisement_id = advertisement_id;
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public String getPromotion_id() { return promotion_id; }

    public void setPromotion_id(String promotion_id) {
        this.promotion_id = promotion_id;
    }

    public String getAdvertisement_id() {
        return advertisement_id;
    }

    public void setAdvertisement_id(String advertisement_id) {
        this.advertisement_id = advertisement_id;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

}
