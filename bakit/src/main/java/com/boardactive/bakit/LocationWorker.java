package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class LocationWorker extends Worker {

    public static final String TAG = LocationWorker.class.getName();
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 1000;

    //updates the location after defined displacement interval in meters
    private static final float SMALLEST_DISPLACEMENT = 10;

    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 6;
    Context context;

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationupdatesforegroundservice";


    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "LocationUpdates";


    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;


    private NotificationManager mNotificationManager;
    /**
     * The current location.
     */
    public Location mLocation;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;
    private BoardActive mBoardActive;
    public double latitude;
    public double longitude;

    public LocationWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
        mBoardActive = new BoardActive(context);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        if (!PermissionExceptionHandler.with(context).wantToStartWorker()) {
            return Result.failure();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d(TAG, "location");


            }
        };
        createLocationRequest();
        try {
            mFusedLocationClient
                    .getLastLocation().
                    addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
//                                mLocation = location;
//                                Log.d(TAG, "Location : " + mLocation);
//                                Log.e("latworker",""+mLocation.getLatitude());
//                                Log.e("longworker",""+mLocation.getLongitude());
//                                // Create the NotificationChannel, but only on API 26+ because
//                                // the NotificationChannel class is new and not in the support library
//                                latitude = mLocation.getLatitude();
//                                longitude = mLocation.getLongitude();
//                                mBoardActive.setLatitude("" + latitude);
//                                mBoardActive.setLongitude("" + longitude);
//                               // Log.e("lat", "" + latitude);
//                              // Log.e("long", "" + longitude);
//                                //Toast.makeText(context, "Location Worker Started : " + "enter into distance", Toast.LENGTH_SHORT).show();
//
//                                if (mBoardActive.getPastLongitude() == null && mBoardActive.getPastLatitude() == null) {
//                                    mBoardActive.setPastLatitude(latitude);
//                                    mBoardActive.setPastLongitude(longitude);
//                                }
//                                if(mBoardActive.getPastLatitude() != null && mBoardActive.getPastLongitude() != null)
//                                {
//                                    if (Double.parseDouble(mBoardActive.getPastLatitude()) != latitude && Double.parseDouble(mBoardActive.getPastLongitude()) != longitude) {
//                                        if (!Constants.FIRST_TIME_GET_GEOFENCE) {
//                                            Constants.FIRST_TIME_GET_GEOFENCE = true;
//                                            mBoardActive.getLocationList();
//                                        }
//                                        Location temp = new Location(LocationManager.GPS_PROVIDER);
//                                        temp.setLatitude(Double.parseDouble(mBoardActive.getPastLatitude()));
//                                        temp.setLongitude(Double.parseDouble(mBoardActive.getPastLongitude()));
//                                        Log.e("distance", "" + mLocation.distanceTo(temp));
//
//                                        Log.d(TAG, "PassLoc lat/lng: " + mBoardActive.getPastLatitude() + " " + mBoardActive.getPastLongitude());
//                                        if (mLocation.distanceTo(temp) > Constants.DISTANCE) {
//                                            //setPastLongitude(null);
//                                            // setPastLatitude(null);
//                                            mBoardActive.setPastLatitude(mLocation.getLatitude());
//                                            mBoardActive.setPastLongitude(mLocation.getLongitude());
//                                            Log.e("new lat", mBoardActive.getPastLatitude());
//                                            Log.e("new lat", mBoardActive.getPastLongitude());
//                                            Log.e("enter into distance", "enter into distance");
//                                            mBoardActive.setLocationArrayList(null);
//                                            mBoardActive.getLocationList();
//                                            //Toast.makeText(context, "Location Worker Started : " + "enter into distance", Toast.LENGTH_SHORT).show();
//
//                                        }
//
//                                    }
//
//                                }
                                stopLocationUpdates();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
        //WorkManager.getInstance().getWorkInfosByTag( BoardActive.class.getName()).cancel(true);
        return Result.success();
    }

    public void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    // This method sets the attributes to fetch location updates.
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

//      mLocationRequest.setFastestInterval(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
            return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
            return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

    }

}
