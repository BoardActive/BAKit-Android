package com.boardactive.sdk;

import java.util.List;

/**
 * BoardActive 2018.08.05
 */

public class AdDrop {
    public Integer promotion_id;
    public Integer advertisement_id;
    public String title;
    public String category;
    public String promo_code;
    public String image_url;
    public String description;
    public String start_at;
    public String expire_at;
    public Integer promotion_link_id;
    public String promotion_link_url;
    public String created_at;
    public String updated_at;
    public String deleted_at;
    public String time_start;
    public String time_end;

    List<locations> locations;

    private class locations {
        public Integer id;
        public String created_at;
        public String updated_at;
        public String address_one;
        public String address_two;
        public String city;
        public String state;
        public String zip_code;
        public String country;
        public String latitude;
        public String longitude;
    }
}
