package com.boardactive.bakitapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("baMessageId")
    @Expose
    private String baMessageId;

    @SerializedName("baNotificationId")
    @Expose
    private String baNotificationId;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("messageData")
    @Expose
    private String messageData;

    @SerializedName("isTestMessage")
    @Expose
    private String isTestMessage;

    @SerializedName("isRead")
    @Expose
    private Boolean isRead;

    @SerializedName("dateCreated")
    @Expose
    private Long dateCreated;

    @SerializedName("dateLastUpdated")
    @Expose
    private Long dateLastUpdated;

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("isAllowToDownloadNotificationImage")
    @Expose
    private Boolean isAllowToDownloadNotificationImage;

    /**
     * No args constructor for use in serialization
     */
    public MessageModel() {
    }

    /**
     * @param id
     * @param baMessageId
     * @param baNotificationId
     * @param firebaseNotificationId
     * @param title
     * @param body
     * @param imageUrl
     * @param latitude
     * @param longitude
     * @param messageData
     * @param isTestMessage
     * @param isRead
     * @param dateCreated
     * @param dateLastUpdated
     */
    public MessageModel(
            Integer id,
            String baMessageId,
            String baNotificationId,
            String firebaseNotificationId,
            String title,
            String body,
            String imageUrl,
            String latitude,
            String longitude,
            String messageData,
            String isTestMessage,
            Boolean isRead,
            Long dateCreated,
            Long dateLastUpdated,
            String action
    ) {
        super();
        this.id = id;
        this.baMessageId = baMessageId;
        this.baNotificationId = baNotificationId;
        this.firebaseNotificationId = firebaseNotificationId;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.messageData = messageData;
        this.isTestMessage = isTestMessage;
        this.isRead = isRead;
        this.dateCreated = dateCreated;
        this.dateLastUpdated = dateLastUpdated;
        this.action =action;
    }

    protected MessageModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        baMessageId = in.readString();
        baNotificationId = in.readString();
        firebaseNotificationId = in.readString();
        body = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        messageData = in.readString();
        isTestMessage = in.readString();
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        if (in.readByte() == 0) {
            dateCreated = null;
        } else {
            dateCreated = in.readLong();
        }
        if (in.readByte() == 0) {
            dateLastUpdated = null;
        } else {
            dateLastUpdated = in.readLong();
        }
        action = in.readString();
        byte tmpIsAllowToDownloadNotificationImage = in.readByte();
        isAllowToDownloadNotificationImage = tmpIsAllowToDownloadNotificationImage == 0 ? null : tmpIsAllowToDownloadNotificationImage == 1;
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaMessageId() {
        return baMessageId;
    }

    public void setBaMessageId(String messageId) {
        this.baMessageId = messageId;
    }

    public String getBaNotificationId() {
        return baNotificationId;
    }

    public void setBaNotificationId(String baNotificationId) {
        this.baNotificationId = baNotificationId;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public Boolean getAllowToDownloadNotificationImage() {
        return isAllowToDownloadNotificationImage;
    }

    public void setAllowToDownloadNotificationImage(Boolean allowToDownloadNotificationImage) {
        isAllowToDownloadNotificationImage = allowToDownloadNotificationImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
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

    public void setIsTestMessage(String isTestMessage) {
        this.isTestMessage = isTestMessage;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Long dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(baMessageId);
        parcel.writeString(baNotificationId);
        parcel.writeString(firebaseNotificationId);
        parcel.writeString(body);
        parcel.writeString(title);
        parcel.writeString(imageUrl);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(messageData);
        parcel.writeString(isTestMessage);
        parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        if (dateCreated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateCreated);
        }
        if (dateLastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateLastUpdated);
        }
        parcel.writeString(action);
        parcel.writeByte((byte) (isAllowToDownloadNotificationImage == null ? 0 : isAllowToDownloadNotificationImage ? 1 : 2));
    }
}
