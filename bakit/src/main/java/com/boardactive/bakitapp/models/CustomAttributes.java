package com.boardactive.bakitapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomAttributes {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("isStock")
    @Expose
    private Boolean isStock;
    @SerializedName("createdByUserId")
    @Expose
    private Integer createdByUserId;
    @SerializedName("lastUpdatedByUserId")
    @Expose
    private Integer lastUpdatedByUserId;
    @SerializedName("dateCreated")
    @Expose
    private String dateCreated;
    @SerializedName("dateLastUpdated")
    @Expose
    private String dateLastUpdated;
    @SerializedName("dateTrashed")
    @Expose
    private String dateTrashed;
    @SerializedName("isTrashed")
    @Expose
    private Boolean isTrashed;
    @SerializedName("inbox")
    @Expose
    private Inbox inbox;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsStock() {
        return isStock;
    }

    public void setIsStock(Boolean isStock) {
        this.isStock = isStock;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Integer getLastUpdatedByUserId() {
        return lastUpdatedByUserId;
    }

    public void setLastUpdatedByUserId(Integer lastUpdatedByUserId) {
        this.lastUpdatedByUserId = lastUpdatedByUserId;
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

    public String getDateTrashed() {
        return dateTrashed;
    }

    public void setDateTrashed(String dateTrashed) {
        this.dateTrashed = dateTrashed;
    }

    public Boolean getIsTrashed() {
        return isTrashed;
    }

    public void setIsTrashed(Boolean isTrashed) {
        this.isTrashed = isTrashed;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }
}
