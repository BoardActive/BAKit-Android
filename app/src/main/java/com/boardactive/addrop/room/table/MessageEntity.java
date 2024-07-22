package com.boardactive.addrop.room.table;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.boardactive.bakit.models.MessageModel;

import java.io.Serializable;

/*
 * To save notification
 */

@Entity(tableName = "message")
public class MessageEntity implements Serializable {

    @PrimaryKey
    private Integer id;

    @ColumnInfo(name = "baMessageId")
    private String baMessageId;

    @ColumnInfo(name = "baNotificationId")
    private String baNotificationId;

    @ColumnInfo(name = "firebaseNotificationId")
    private String firebaseNotificationId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "body")
    private String body;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @ColumnInfo(name = "latitude")
    private String latitude;

    @ColumnInfo(name = "longitude")
    private String longitude;

    @ColumnInfo(name = "messageData")
    private String messageData;

    @ColumnInfo(name = "isTestMessage")
    private String isTestMessage;

    @ColumnInfo(name = "isRead")
    private Boolean isRead;

    @ColumnInfo(name = "dateCreated")
    private Long dateCreated;

    @ColumnInfo(name = "dateLastUpdated")
    private Long dateLastUpdated;


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setBaMessageId(String baMessageId) {
        this.baMessageId = baMessageId;
    }

    public String getBaMessageId() {
        return baMessageId;
    }

    public void setBaNotificationId(String baNotificationId) {
        this.baNotificationId = baNotificationId;
    }

    public String getBaNotificationId() {
        return baNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return title;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setIsTestMessage(String isTestMessage) {
        this.isTestMessage = isTestMessage;
    }

    public String getIsTestMessage() {
        return isTestMessage;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateLastUpdated(Long dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public Long getDateLastUpdated() {
        return dateLastUpdated;
    }

    public static MessageEntity entity(MessageModel message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setBaMessageId(message.getBaMessageId());
        entity.setBaMessageId(message.getBaMessageId());
        entity.setFirebaseNotificationId(message.getFirebaseNotificationId());
        entity.setTitle(message.getTitle());
        entity.setBody(message.getBody());
        entity.setImageUrl(message.getImageUrl());
        entity.setLatitude(message.getLatitude());
        entity.setLongitude(message.getLongitude());
        entity.setIsTestMessage(message.getIsTestMessage());
        entity.setIsRead(message.getIsRead());
        entity.setDateCreated(message.getDateCreated());
        entity.setDateLastUpdated(message.getDateLastUpdated());
        return entity;
    }
}
