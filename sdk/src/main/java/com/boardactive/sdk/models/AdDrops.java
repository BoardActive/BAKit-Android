package com.boardactive.sdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AdDrops {

    @SerializedName("promotion_id")
    @Expose
    private Integer promotion_id;

    @SerializedName("advertisement_id")
    @Expose
    private Integer advertisement_id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("category")
    @Expose
    private String category;

    @SerializedName("promo_code")
    @Expose
    private String promo_code;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("start_at")
    @Expose
    private String start_at;

    @SerializedName("expire_at")
    @Expose
    private String expire_at;

    @SerializedName("promotion_link_id")
    @Expose
    private Integer promotion_link_id = null;

    @SerializedName("promotion_link_url")
    @Expose
    private String promotion_link_url;

    @SerializedName("isBookmarked")
    @Expose
    private Boolean isBookmarked;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("deleted_at")
    @Expose
    private String deleted_at;

    @SerializedName("time_start")
    @Expose
    private String time_start;

    @SerializedName("time_end")
    @Expose
    private String time_end;

    @SerializedName("locations")
    @Expose
    private List<AdDropLocations> locations;
    /**
     * No args constructor for use in serialization
     *
     */
    public AdDrops() {
    }

    /**
     *
     * @param promotion_id
     * @param advertisement_id
     * @param title
     * @param category
     * @param promo_code
     * @param image_url
     * @param description
     * @param start_at
     * @param expire_at
     * @param promotion_link_id
     * @param promotion_link_url
     * @param isBookmarked
     * @param created_at
     * @param updated_at
     * @param deleted_at
     * @param time_start
     * @param time_end
     * @param locations
     */
    public AdDrops(
            Integer promotion_id,
            Integer advertisement_id,
            String title,
            String category,
            String promo_code,
            String image_url,
            String description,
            String start_at,
            String expire_at,
            Integer promotion_link_id,
            String promotion_link_url,
            Boolean isBookmarked,
            String created_at,
            String updated_at,
            String deleted_at,
            String time_start,
            String time_end,
            List<AdDropLocations> locations
    ) {
        super();
        this.promotion_id = promotion_id;
        this.advertisement_id = advertisement_id;
        this.title = title;
        this.category = category;
        this.promo_code = promo_code;
        this.image_url = image_url;
        this.description = description;
        this.start_at = start_at;
        this.expire_at = expire_at;
        this.promotion_link_id = promotion_link_id;
        this.promotion_link_url = promotion_link_url;
        this.isBookmarked = isBookmarked;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.deleted_at = deleted_at;
        this.time_start = time_start;
        this.time_end = time_end;
        this.locations = locations;
    }

    public Integer getPromotion_id() {
        return promotion_id;
    }

    public void setPromotion_id(Integer promotion_id) {
        this.promotion_id = promotion_id;
    }

    public Integer getAdvertisement_id() {
        return advertisement_id;
    }

    public void setAdvertisement_id(Integer id) {
        this.advertisement_id = advertisement_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(String expire_at) {
        this.expire_at = expire_at;
    }

    public Integer getPromotion_link_id() {
        return promotion_link_id;
    }

    public void setPromotion_link_id(Integer promotion_link_id) {
        this.promotion_link_id = promotion_link_id;
    }

    public String getPromotion_link_url() {
        return promotion_link_url;
    }

    public void setPromotion_link_url(String promotion_link_url) {
        this.promotion_link_url = promotion_link_url;
    }

    public Boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setIsBookmarked(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;

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

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public List<AdDropLocations> getLocations() {
        return locations;
    }

    public void setLocations(List<AdDropLocations> locations) {
        this.locations = locations;
    }

}