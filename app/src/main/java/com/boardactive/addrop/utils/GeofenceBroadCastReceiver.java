package com.boardactive.addrop.utils;

import static com.boardactive.addrop.BAKitApp.CHANNEL_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.boardactive.bakit.BoardActive;
import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.boardactive.addrop.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public  class GeofenceBroadCastReceiver extends BroadcastReceiver {
    private String audioFile;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("on Receiver","on receive");

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("TAG", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e("on enter","on enter");
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            setGeofenceDate(context,date);
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String locId = triggeringGeofences.get(0).getRequestId();
            sendNotification(locId, context);

            Intent serviceIntent = new Intent(context, LocationService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }*/
        } else {
            // Log the error.
            Log.e("TAG", "Error");
        }
    }
    public void setGeofenceDate(Context context,String geofenceDate) {
        SharedPreferenceHelper.putString(context, BoardActive.BAKIT_GEOFENCE_DATE, geofenceDate);
    }
    private void sendNotification(String locId, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Location Reached")
                .setContentText("you reached " + locId)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}