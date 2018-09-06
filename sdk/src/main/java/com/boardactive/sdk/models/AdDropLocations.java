package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdDropLocations {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("address_one")
    @Expose
    private String address_one;

    @SerializedName("address_two")
    @Expose
    private String address_two;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("zip_code")
    @Expose
    private String zip_code;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;

    @SerializedName("geom")
    @Expose
    private AdDropGeom geom;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address_id")
    @Expose
    private Integer address_id;

    @SerializedName("advertiser_id")
    @Expose
    private Integer advertiser_id;

    @SerializedName("contact_id")
    @Expose
    private Integer contact_id;

    /**
     * No args constructor for use in serialization
     */
    public AdDropLocations() {
    }

    /**
     * @param id
     * @param address_one
     * @param address_two
     * @param city
     * @param state
     * @param zip_code
     * @param latitude
     * @param longitude
     * @param created_at
     * @param updated_at
     * @param deleted_at
     * @param geom
     * @param country
     * @param name
     * @param address_id
     * @param advertiser_id
     * @param contact_id
     */
    public AdDropLocations(
            Integer id,
            String address_one,
            String address_two,
            String city,
            String state,
            String zip_code,
            String latitude,
            String longitude,
            String created_at,
            String updated_at,
            String deleted_at,
            AdDropGeom geom,
            String country,
            String name,
            Integer address_id,
            Integer advertiser_id,
            Integer contact_id
    ) {
        super();
        this.id = id;
        this.address_one = address_one;
        this.address_two = address_two;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.latitude = latitude;
        this.longitude = longitude;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.geom = geom;
        this.country = country;
        this.name = name;
        this.address_id = address_id;
        this.advertiser_id = advertiser_id;
        this.contact_id = contact_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddress_one() {
        return address_one;
    }

    public void setAddress_one(String address_one) {
        this.address_one = address_one;
    }

    public String getAddress_two() {
        return address_two;
    }

    public void setAddress_two(String address_two) {
        this.address_two = address_two;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public AdDropGeom getGeom() {
        return geom;
    }

    public void setGeom(AdDropGeom geom) {
        this.geom = geom;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAddress_id() {
        return address_id;
    }

    public void setAddress_id(Integer address_id) {
        this.address_id = address_id;
    }

    public Integer getAdvertiser_id() {
        return advertiser_id;
    }

    public void setAdvertiser_id(Integer advertiser_id) {
        this.advertiser_id = advertiser_id;
    }

    public Integer getContact_id() {
        return contact_id;
    }

    public void setContact_id(Integer contact_id) {
        this.contact_id = contact_id;
    }

}
