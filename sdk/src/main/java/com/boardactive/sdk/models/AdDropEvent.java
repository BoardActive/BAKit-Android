package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdDropEvent {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("params")
    @Expose
    private AdDropEventParams params;


    /**
     * No args constructor for use in serialization
     */
    public AdDropEvent() {
    }

    /**
     * @param lat
     * @param lng
     * @param deviceTimeString
     * @param params
     */
    public AdDropEvent(
            String name,
            AdDropEventParams params
    ) {
        super();
        this.name = name;
        this.params = params;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public AdDropEventParams getParams() {
        return params;
    }

    public void setParams(AdDropEventParams params) {
        this.params = params;
    }

}
