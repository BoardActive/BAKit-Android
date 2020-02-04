package com.boardactive.bakit.models;

import org.json.JSONObject;

public class Attributes {
    private Stock stock;
    private Custom custom;

    //Getters and setters
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Custom getCustom() {
        return custom;
    }

    public void setCustom(Custom custom) {
        this.custom = custom;
    }

}