package com.boardactive.bakit.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("messageId")
    @Expose
    private String messageId;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

    @SerializedName("isTestMessage")
    @Expose
    private String isTestMessage;


    /**
     * No args constructor for use in serialization
     */
    public Event() {
    }

    /**
     * @param name
     * @param messageId
     * @param firebaseNotificationId
     * @param isTestMessage
     */
    public Event(
            String name,
            String messageId,
            String firebaseNotificationId,
            String isTestMessage
    ) {
        super();
        this.name = name;
        this.messageId = messageId;
        this.firebaseNotificationId = firebaseNotificationId;
        this.isTestMessage = isTestMessage;
    }

    public String getname() { return name; }

    public void setname(String name) {
        this.name = name;
    }

    public String getmessageId() {
        return messageId;
    }

    public void setmessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getfirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setfirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public String getisTestMessage() { return isTestMessage; }

    public void setisTestMessage(String isTestMessage) {
        this.isTestMessage = isTestMessage;
    }


}
