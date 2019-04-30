package com.boardactive.bakit.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Response implements Serializable {

    @SerializedName("message")
    public String message;

}
