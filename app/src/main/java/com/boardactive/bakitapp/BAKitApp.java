package com.boardactive.bakitapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class BAKitApp extends Application {

    public static final String TAG = BAKitApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[BAKitApp] BAKitApp onCreate()");

        /**
         * This will start Firebase
         */
        FirebaseApp.initializeApp(this.getApplicationContext());
    }

}
