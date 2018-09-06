package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdDropBookmarkResponse {

    @SerializedName("status")
    @Expose
    private Integer status;

    /**
     * No args constructor for use in serialization
     *
     */
    public AdDropBookmarkResponse() {
    }

    /**
     *
     * @param status
     */
    public AdDropBookmarkResponse(
            Integer status
    ) {
        super();
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}