package com.boardactive.bakitapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[BAKitApp] MyApp onCreate()");
        FirebaseApp.initializeApp(this.getApplicationContext());
    }

}
