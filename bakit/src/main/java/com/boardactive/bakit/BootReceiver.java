package com.boardactive.bakit;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.boardactive.bakit.Tools.SharedPreferenceHelper;

/**
 * Automatically starts JobDispatcher at boot time.
 * <p>
 * AndroidManifest Entries:
 * <p>
 * <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * <p>
 * <receiver android:name=".BootReceiver" android:enabled="true"
 * android:permission="android.permission.RECEIVE_BOOT_COMPLETED">
 * <intent-filter>
 * <action android:name="android.intent.action.BOOT_COMPLETED" />
 * <category android:name="android.intent.category.DEFAULT" />
 * </intent-filter>
 * </receiver>
 */
public class BootReceiver extends BroadcastReceiver {


    public static final String TAG = BootReceiver.class.getSimpleName();

    private static final String JOB_TAG = "BootReceiver";
    private static final String IS_FOREGROUND = "isforeground";

    // This class is triggered a minute or two after the device is restarted, it starts our
    // location reporting service and Firebase Notification Job
    @Override
    public void onReceive(Context context, Intent intent) {

        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionState == PackageManager.PERMISSION_GRANTED) {

            if (SharedPreferenceHelper.getBoolean(context, IS_FOREGROUND, false)) {
                Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
                ContextCompat.startForegroundService(context, serviceIntent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }*/
            } else {
                startWorker();
            }
        }
    }

    /**
     * Starts the worker after device reboots with a one time request.
     */
    private void startWorker() {

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(LocationWorker.class)
                .addTag("OneTimeLocation")
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueueUniqueWork("OneTimeLocation", ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);

    }

}

