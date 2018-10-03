package com.boardactive.sdk.ui.addrops;

import com.boardactive.sdk.models.AdDrops;

import java.util.List;

public interface AdDropsViewInterface {

    void showToast(String s);
    void showProgressBar();
    void hideProgressBar();
    void displayAdDrops(List<AdDrops> addrops);
    void displayError(String s);
}