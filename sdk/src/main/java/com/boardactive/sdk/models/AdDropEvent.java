package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdDropEvent {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("params")
    @Expose
    private List<AdDropEventParams> params;


    /**
     * No args constructor for use in serialization
     */
    public AdDropEvent() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     */
    public AdDropEvent(
            String name,
            List<AdDropEventParams> params
    ) {
        super();
        this.name = name;
        this.params = params;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public List<AdDropEventParams> getParams() {
        return params;
    }

    public void setParams(List<AdDropEventParams> params) {
        this.params = params;
    }

}
