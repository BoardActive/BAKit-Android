package com.boardactive.sdk;

import android.support.annotation.NonNull;

/**
 * BoardActive 2018.08.05
 */
public class EndPointUrl {
    private String url;

    public EndPointUrl(@NonNull String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
