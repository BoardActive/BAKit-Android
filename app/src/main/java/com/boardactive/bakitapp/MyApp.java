package com.boardactive.bakitapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.boardactive.bakit.App;

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[BAKitApp] MyApp onCreate()");

        MyAppLifecycleObserver appLifecycleObserver = new MyAppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);


    }

}
