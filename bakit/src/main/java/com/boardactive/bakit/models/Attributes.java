package com.boardactive.bakit.models;

import org.json.JSONObject;

import java.util.HashMap;

public class Attributes {
    private Stock stock;
    private HashMap custom;

    //Getters and setters
    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public HashMap getCustom() {
        return custom;
    }

    public void setCustom(HashMap custom) {
        this.custom = custom;
    }

}