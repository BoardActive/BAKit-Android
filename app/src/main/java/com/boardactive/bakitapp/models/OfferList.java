package com.boardactive.bakitapp.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfferList {

    @SerializedName("adv_name")
    @Expose
    private String adv_name;

    @SerializedName("Offer_title")
    @Expose
    private String offer_title;

    @SerializedName("offer_detail")
    @Expose
    private String offer_detail;


    /**
     * No args constructor for use in serialization
     */
    public OfferList() {
    }

    /**
     * @param adv_name
     * @param offer_title
     * @param offer_detail
     */
    public OfferList(
            String adv_name,
            String offer_title,
            String offer_detail
    ) {
        super();
        this.adv_name = adv_name;
        this.offer_title = offer_title;
        this.offer_detail = offer_detail;
    }

    public String getAdv_name() {
        return adv_name;
    }

    public void setAdv_name(String adv_name) {
        this.adv_name = adv_name;
    }

    public String getOffer_title() {
        return offer_title;
    }

    public void setOffer_title(String offer_title) {
        this.offer_title = offer_title;
    }

    public String getOffer_detail() {
        return offer_detail;
    }

    public void setOffer_detail(String offer_detail) {
        this.offer_detail = offer_detail;
    }

}

