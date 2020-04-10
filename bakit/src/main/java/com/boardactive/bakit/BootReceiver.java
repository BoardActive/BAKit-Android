package com.boardactive.bakit;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/** Automatically starts JobDispatcher at boot time.
 *
 * AndroidManifest Entries:
 *
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 *
 *  <receiver android:name=".BootReceiver" android:enabled="true"
 *             android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
 *      <intent-filter>
 *          <action android:name="android.intent.action.BOOT_COMPLETED" />
 *          <category android:name="android.intent.category.DEFAULT" />
 *      </intent-filter>
 *  </receiver>
 *
 *
 * */
public class BootReceiver extends BroadcastReceiver {


    public static final String TAG = BootReceiver.class.getSimpleName();

    private Context mContext;
    private static final String JOB_TAG = "BootReceiver";

    // This class is triggered a minute or two after the device is restarted, it starts our
    // location reporting service and Firebase Notification Job
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionState == PackageManager.PERMISSION_GRANTED){

            startWorker();
        }

    }

    /** Starts the worker after device reboots with a one time request.
     * */
    private void startWorker(){
        //Flex Interval
        /*PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.MINUTES, 1, TimeUnit.MINUTES)
                .addTag(TAG)
                .build();
        //No Flex Interval
//        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, 1, TimeUnit.MINUTES)
//                .addTag(TAG)
//                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);*/


        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(LocationWorker.class)
                .addTag("OneTimeLocation")
                .build();

        WorkManager.getInstance().enqueueUniqueWork("OneTimeLocation", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);

    }

}

