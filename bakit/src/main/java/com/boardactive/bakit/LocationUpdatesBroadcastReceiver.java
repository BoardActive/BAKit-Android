package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;

import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.google.android.gms.location.LocationResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "PROCESS_UPDATES";
    public static final String TAG = LocationUpdatesBroadcastReceiver.class.getName();
    public final static String LAST_DATA_UPDATED_TIME = "last_data_updated_time";
    private static final String IS_FOREGROUND = "isforeground";
    private BoardActive mBoardActive;
    private Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent != null) {
            mBoardActive = new BoardActive(context.getApplicationContext());
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                getLocationUpdates(context, intent);
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void getLocationUpdates(final Context context, final Intent intent) {

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            List<Location> locations = result.getLocations();

            if (locations.size() > 0) {
                Location firstLocation = locations.get(0);
                updateLocation(context, firstLocation);
            }
        } else {
            // An additional check to start worker by checking the location permissions in case locations are not being retreived.
            if (PermissionExceptionHandler.with(context).wantToStartWorker()) {
                if (!SharedPreferenceHelper.getBoolean(context, IS_FOREGROUND, false))
                    startWorker();
            } else {
                WorkManager.getInstance(context).cancelAllWork();
            }
        }
    }

    // This method starts the worker if locations are not being retreived.
    private void startWorker() {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                2 * 60 * 1000, pendingIntent);
    }

    // This method sends the data to server and displays a local notification of the location
    private void updateLocation(final Context context, final Location firstLocation) {
        DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
        final String date = df.format(Calendar.getInstance().getTime());

        //sends data to server every minute.
        if (SharedPreferenceHelper.getLong(context, LAST_DATA_UPDATED_TIME, 0) == 0 || System.currentTimeMillis() - SharedPreferenceHelper.getLong(context, LAST_DATA_UPDATED_TIME, 0) >= 60000) {
            mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                @Override
                public void onResponse(Object value) {
                    Log.d(TAG, "[BAKit] onResponse" + value.toString());
                    SharedPreferenceHelper.putLong(context, LAST_DATA_UPDATED_TIME, System.currentTimeMillis());
                }
            }, firstLocation.getLatitude(), firstLocation.getLongitude(), date);
        }
    }
}
