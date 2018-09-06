package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdDropGeom {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("coordinates")
    @Expose
    private List<Float> coordinates;

    /**
     * No args constructor for use in serialization
     */
    public AdDropGeom() {
    }

    /**
     * @param type
     * @param coordinates
     */
    public AdDropGeom(
            String type,
            List<Float> coordinates
    ) {
        super();
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> id) {
        this.coordinates = coordinates;
    }

}