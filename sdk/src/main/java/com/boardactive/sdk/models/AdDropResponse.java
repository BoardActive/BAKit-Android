package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdDropResponse {


    @SerializedName("status")
    @Expose
    private Integer status;

    @SerializedName("data")
    @Expose
    private List<AdDrop> data;

    /**
     * No args constructor for use in serialization
     *
     */
    public AdDropResponse() {
    }

    /**
     *
     * @param status
     * @param data
     */
    public AdDropResponse(
            Integer status,
            List<AdDrop> data
    ) {
        super();
        this.status = status;
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<AdDrop> getData() {
        return data;
    }

    public void setData(List<AdDrop> data) {
        this.data = data;
    }

}