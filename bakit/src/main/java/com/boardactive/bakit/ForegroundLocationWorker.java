package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.common.util.concurrent.ListenableFuture;

public class ForegroundLocationWorker extends ListenableWorker {
    private LocationSettingsRequest mLocationSettingsRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public static final long UPDATE_INTERVAL = 50 * 1000;
    public static final float SMALLEST_DISPLACEMENT = 5.0F;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;
    Context context;
    private ResolvableFuture<Result> mFuture;


    public ForegroundLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }



    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {
        mFuture = ResolvableFuture.create();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            createNotificationChannel();
            Notification notification;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                notification = new NotificationCompat.Builder(context,
                        "BAKit")
                        .setContentTitle("This is an ongoing notification.")
                        .setChannelId("BAKit")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .build();

            }else{
                notification = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setContentTitle("This is an ongoing notification.")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .build();
            }

        notificationManager.notify(1002, notification);


            ForegroundInfo foregroundInfo = new ForegroundInfo(1002,notification);
            setForegroundAsync(foregroundInfo);



        Log.e("Dhruvit Test::::","Test statement 1" );
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

            createLocationRequest();
            buildLocationSettingsRequest();

            try {
                Log.e("Dhruvit Test::::","Test statement 4" );
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, getPendingIntent());
                Log.e("Dhruvit Test::::","Test statement 6" );
            } catch (Exception e) {
                e.printStackTrace();
                mFuture.set(Result.retry());
                return mFuture;
            }
        mFuture.set(Result.retry());
            return mFuture;
    }

    private void buildLocationSettingsRequest() {

        Log.e("Dhruvit Test::::","Test statement 3" );
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    // This method sets the attributes to fetch location updates.
    private void createLocationRequest() {

        Log.e("Dhruvit Test::::","Test statement 2" );
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }

    private PendingIntent getPendingIntent() {

        Log.e("Dhruvit Test::::","Test statement 5" );
        Intent intent = new Intent(getApplicationContext(), LocationUpdatesBroadcastReceiver.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BAKit",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
