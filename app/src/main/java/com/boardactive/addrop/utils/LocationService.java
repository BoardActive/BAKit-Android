package com.boardactive.addrop.utils;

import static com.boardactive.addrop.BAKitApp.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.boardactive.addrop.R;
import com.boardactive.addrop.activity.MainActivity;


public class LocationService extends Service {
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Alarm")
                .setContentText("You reached the location.")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_STICKY;
    }


}