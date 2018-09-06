package com.boardactive.sdk.ui.addrops;

import java.util.List;

import com.boardactive.sdk.models.AdDrops;

public interface AdDropsViewInterface {

    void showToast(String s);
    void showProgressBar();
    void hideProgressBar();
    void displayAdDrops(List<AdDrops> addrops);
    void displayError(String s);
}