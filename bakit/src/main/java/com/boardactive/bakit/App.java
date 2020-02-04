package com.boardactive.bakit;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

public class App extends Application {

    public static final String TAG = App.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "[BAKit] MyApp onCreate()");

        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);

    }

}
