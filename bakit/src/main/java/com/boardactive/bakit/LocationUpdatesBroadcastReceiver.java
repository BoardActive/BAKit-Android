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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;

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

    private BoardActive mBoardActive;
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent != null) {
            mBoardActive = new BoardActive(context.getApplicationContext());
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                getLocationUpdates(context,intent,"PROCESS_UPDATES");
            }
        }
    }


    @SuppressLint("MissingPermission")
    public void getLocationUpdates(final Context context, final Intent intent, String broadcastevent)  {

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            List<Location> locations = result.getLocations();

            if (locations.size() > 0) {
                Location firstLocation = locations.get(0);
                updateLocation(context, firstLocation);
            }
        }else{
            // An additional check to start worker by checking the location permissions in case locations are not being retreived.
            if (PermissionExceptionHandler.with(context).wantToStartWorker()) {
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
    private void updateLocation(Context context, Location firstLocation) {
        DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
        String date = df.format(Calendar.getInstance().getTime());

        createNotification(firstLocation, date);

        mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "[BAKit] onResponse" + value.toString());
            }
        }, firstLocation.getLatitude(), firstLocation.getLongitude(), date);
    }



    private void createNotification(Location firstLocation, String date) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BAKit",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Notification builder =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context,
                    "BAKit")
                    .setContentTitle("New Location Update")
                    .setContentText("You are at " + getAddress(firstLocation, context))
                    .setSubText(date)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new Notification.BigTextStyle().bigText("You are at " + getAddress(firstLocation, context) + "\n" + firstLocation.getLatitude() + "\n" + firstLocation.getLongitude()))
                    .setChannelId("BAKit")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build();
            notificationManager.notify(1001, builder);
        }else{
            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setContentTitle("New Location Update")
                    .setContentText("You are at " + getAddress(firstLocation, context))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("You are at " + getAddress(firstLocation, context) + "\n" + firstLocation.getLatitude() + "\n" + firstLocation.getLongitude()));

            NotificationManagerCompat notificationManager1 = NotificationManagerCompat.from(context);

            // notificationId is a unique int for each notification that you must define
            notificationManager1.notify(1001, builder1.build());
        }

    }

    // This method returns the address from location object.
    public static String getAddress(Location location, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;
        Address address = null;
        String addressFragments = "";
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            address = addresses.get(0);
        } catch (IOException ioException) {
            Log.e(TAG, "error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size() == 0) {
            Log.i(TAG, "ERORR");
            addressFragments = "NO ADDRESS FOUND";
        } else {
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments = addressFragments + String.valueOf(address.getAddressLine(i));
            }
        }
        return addressFragments;
    }
}
