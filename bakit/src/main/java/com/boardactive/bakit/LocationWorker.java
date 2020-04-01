package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

public class LocationWorker extends Worker {

    public static final String TAG = LocationWorker.class.getName();
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;



    public static final long UPDATE_INTERVAL = 50 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 5.0F;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;
    Context context;


    public LocationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
        // TODO(developer): add long running task here.

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        createLocationRequest();
        buildLocationSettingsRequest();

        try {
            changeStatusAfterGetLastLocation("1","Manual");

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return Result.success();
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        //mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }



    @SuppressLint("MissingPermission")
    private void changeStatusAfterGetLastLocation(final String value, final String changeby) {
        if(value == "1"){

            Log.d("","Location Updates started");

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());

        }
    }


    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
