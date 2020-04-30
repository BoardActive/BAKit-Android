package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class LocationWorker extends Worker {

    public static final String TAG = LocationWorker.class.getName();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 50 * 1000;

    //updates the location after defined displacement interval in meters
    private static final float SMALLEST_DISPLACEMENT = 5.0F;

    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;
    Context context;

    public LocationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        if(!PermissionExceptionHandler.with(context).wantToStartWorker()){
            return Result.failure();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        createLocationRequest();

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
        WorkManager.getInstance().getWorkInfosByTag("OneTimeLocation").cancel(true);
        return Result.success();
    }

    // This method sets the attributes to fetch location updates.
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
