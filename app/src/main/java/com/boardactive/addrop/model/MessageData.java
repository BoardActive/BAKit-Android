package com.boardactive.addrop.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageData implements Serializable, Parcelable {

    @SerializedName("urlTwitter")
    @Expose
    private String urlTwitter;

    @SerializedName("storeAddress")
    @Expose
    private String storeAddress;

    @SerializedName("urlLandingPage")
    @Expose
    private String urlLandingPage;

    @SerializedName("promoDateEnds")
    @Expose
    private String promoDateEnds;

    @SerializedName("urlQRCode")
    @Expose
    private String urlQRCode;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("urlYoutube")
    @Expose
    private String urlYoutube;

    @SerializedName("urlLinkedIn")
    @Expose
    private String urlLinkedIn;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("promoDateStarts")
    @Expose
    private String promoDateStarts;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @SerializedName("urlFacebook")
    @Expose
    private String urlFacebook;

    public final static Creator<MessageData> CREATOR = new Creator<MessageData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MessageData createFromParcel(Parcel in) {
            return new MessageData(in);
        }

        public MessageData[] newArray(int size) {
            return (new MessageData[size]);
        }

    };
    private final static long serialVersionUID = 3777358514685945208L;

    protected MessageData(Parcel in) {
        this.urlTwitter = ((String) in.readValue((String.class.getClassLoader())));
        this.storeAddress = ((String) in.readValue((String.class.getClassLoader())));
        this.urlLandingPage = ((String) in.readValue((String.class.getClassLoader())));
        this.promoDateEnds = ((String) in.readValue((String.class.getClassLoader())));
        this.urlQRCode = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.urlYoutube = ((String) in.readValue((String.class.getClassLoader())));
        this.urlLinkedIn = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.promoDateStarts = ((String) in.readValue((String.class.getClassLoader())));
        this.phoneNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.urlFacebook = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public MessageData() {
    }

    /**
     * @param urlLinkedIn
     * @param title
     * @param promoDateStarts
     * @param phoneNumber
     * @param urlQRCode
     * @param email
     * @param urlYoutube
     * @param urlTwitter
     * @param urlFacebook
     * @param urlLandingPage
     * @param storeAddress
     * @param promoDateEnds
     */
    public MessageData(String urlTwitter, String storeAddress, String urlLandingPage, String promoDateEnds, String urlQRCode, String title, String urlYoutube, String urlLinkedIn, String email, String promoDateStarts, String phoneNumber, String urlFacebook) {
        super();
        this.urlTwitter = urlTwitter;
        this.storeAddress = storeAddress;
        this.urlLandingPage = urlLandingPage;
        this.promoDateEnds = promoDateEnds;
        this.urlQRCode = urlQRCode;
        this.title = title;
        this.urlYoutube = urlYoutube;
        this.urlLinkedIn = urlLinkedIn;
        this.email = email;
        this.promoDateStarts = promoDateStarts;
        this.phoneNumber = phoneNumber;
        this.urlFacebook = urlFacebook;
    }

    public String getUrlTwitter() {
        return urlTwitter;
    }

    public void setUrlTwitter(String urlTwitter) {
        this.urlTwitter = urlTwitter;
    }

    public MessageData withUrlTwitter(String urlTwitter) {
        this.urlTwitter = urlTwitter;
        return this;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public MessageData withStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
        return this;
    }

    public String getUrlLandingPage() {
        return urlLandingPage;
    }

    public void setUrlLandingPage(String urlLandingPage) {
        this.urlLandingPage = urlLandingPage;
    }

    public MessageData withUrlLandingPage(String urlLandingPage) {
        this.urlLandingPage = urlLandingPage;
        return this;
    }

    public String getPromoDateEnds() {
        return promoDateEnds;
    }

    public void setPromoDateEnds(String promoDateEnds) {
        this.promoDateEnds = promoDateEnds;
    }

    public MessageData withPromoDateEnds(String promoDateEnds) {
        this.promoDateEnds = promoDateEnds;
        return this;
    }

    public String getUrlQRCode() {
        return urlQRCode;
    }

    public void setUrlQRCode(String urlQRCode) {
        this.urlQRCode = urlQRCode;
    }

    public MessageData withUrlQRCode(String urlQRCode) {
        this.urlQRCode = urlQRCode;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MessageData withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrlYoutube() {
        return urlYoutube;
    }

    public void setUrlYoutube(String urlYoutube) {
        this.urlYoutube = urlYoutube;
    }

    public MessageData withUrlYoutube(String urlYoutube) {
        this.urlYoutube = urlYoutube;
        return this;
    }

    public String getUrlLinkedIn() {
        return urlLinkedIn;
    }

    public void setUrlLinkedIn(String urlLinkedIn) {
        this.urlLinkedIn = urlLinkedIn;
    }

    public MessageData withUrlLinkedIn(String urlLinkedIn) {
        this.urlLinkedIn = urlLinkedIn;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MessageData withEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPromoDateStarts() {
        return promoDateStarts;
    }

    public void setPromoDateStarts(String promoDateStarts) {
        this.promoDateStarts = promoDateStarts;
    }

    public MessageData withPromoDateStarts(String promoDateStarts) {
        this.promoDateStarts = promoDateStarts;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MessageData withPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getUrlFacebook() {
        return urlFacebook;
    }

    public void setUrlFacebook(String urlFacebook) {
        this.urlFacebook = urlFacebook;
    }

    public MessageData withUrlFacebook(String urlFacebook) {
        this.urlFacebook = urlFacebook;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(urlTwitter);
        dest.writeValue(storeAddress);
        dest.writeValue(urlLandingPage);
        dest.writeValue(promoDateEnds);
        dest.writeValue(urlQRCode);
        dest.writeValue(title);
        dest.writeValue(urlYoutube);
        dest.writeValue(urlLinkedIn);
        dest.writeValue(email);
        dest.writeValue(promoDateStarts);
        dest.writeValue(phoneNumber);
        dest.writeValue(urlFacebook);
    }

    public int describeContents() {
        return 0;
    }

}