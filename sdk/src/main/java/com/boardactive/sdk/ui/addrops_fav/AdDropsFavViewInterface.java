package com.boardactive.sdk.ui.addrops_fav;

import com.boardactive.sdk.models.AdDrops;

import java.util.List;

public interface AdDropsFavViewInterface {

    void showToast(String s);
    void showProgressBar();
    void hideProgressBar();
    void displayAdDrops(List<AdDrops> addrops);
    void displayError(String s);

}
