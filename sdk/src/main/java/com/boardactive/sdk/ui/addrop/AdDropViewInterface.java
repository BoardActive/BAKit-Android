package com.boardactive.sdk.ui.addrop;

import com.boardactive.sdk.models.AdDrop;

public interface AdDropViewInterface {

    void showToast(String s);
    void showProgressBar();
    void hideProgressBar();
    void displayAdDrop(AdDrop addrop);
    void displayError(String s);

}
