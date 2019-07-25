package com.boardactive.addrop.room.table;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.boardactive.addrop.model.Message;

import java.io.Serializable;

/*
 * To save notification
 */

@Entity(tableName = "message")
public class MessageEntity implements Serializable {

    @PrimaryKey
    public Integer id;

    @ColumnInfo(name = "messageId")
    public String messageId;

    @ColumnInfo(name = "notificationId")
    public String notificationId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "imageUrl")
    public String imageUrl;

    @ColumnInfo(name = "latitude")
    public String latitude;

    @ColumnInfo(name = "longitude")
    public String longitude;

    @ColumnInfo(name = "messageData")
    public String messageData;

    @ColumnInfo(name = "isTestMessage")
    public String isTestMessage;

    @ColumnInfo(name = "isRead")
    public Boolean isRead;

    @ColumnInfo(name = "dateCreated")
    public Long dateCreated;

    @ColumnInfo(name = "dateLastUpdated")
    public Long dateLastUpdated;


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationId() {
        return notificationId;
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

    public static MessageEntity entity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setMessageId(message.getMessageId());
        entity.setNotificationId(message.getNotificationId());
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
