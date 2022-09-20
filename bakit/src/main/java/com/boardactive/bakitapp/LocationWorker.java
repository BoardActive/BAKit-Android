package com.boardactive.bakitapp;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class LocationWorker extends Worker {

    public static final String TAG = LocationWorker.class.getName();
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 5 * 1000;

    //updates the location after defined displacement interval in meters
    private static final float SMALLEST_DISPLACEMENT = 10;

    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 6;
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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//      mLocationRequest.setFastestInterval(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
