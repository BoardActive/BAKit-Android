package com.boardactive.addrop;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class BAKitApp extends Application {

    public static final String TAG = BAKitApp.class.getName();
    public static final String CHANNEL_ID = "channel 1";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[BAKitApp] BAKitApp onCreate()");
        createNotificationChannel();
        /**
         * This will start Firebase
         */
        FirebaseApp.initializeApp(this.getApplicationContext());
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

    }
}
