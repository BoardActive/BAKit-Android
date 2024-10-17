package com.boardactive.bakit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.boardactive.bakit.Tools.SharedPreferenceHelper;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RequestPermissionActivity extends AppCompatActivity {

    public final static String BAKIT_DEVICE_OS = "BAKIT_DEVICE_OS";
    public final static String BAKIT_DEVICE_OS_VERSION = "BAKIT_DEVICE_OS_VERSION";
    public final static String BAKIT_DEVICE_ID = "BAKIT_DEVICE_ID";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 1003;
    private static final int BACKGROUND_LOCATION_PERMISSION_CODE = 1002;
    private static final int MY_PERMISSIONS_REQUEST_NOTIFICATION = 1004;

    public static final String TAG = BoardActive.class.getName();
    private static final String FETCH_LOCATION_WORKER_NAME = "Location";
    private static final String IS_FOREGROUND = "isforeground";

    // periodic worker takes 15 mins repeatInterval by default to restart even if you set <15 mins.
    private int repeatInterval = 15;

    BoardActive mBoardActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_request_permission);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());
        requestLocationPermission();
        requestNotificationPermission();
    }

    private void requestLocationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                // Background Location Permission is granted so do your work here
//            } else {
//                // Ask for Background Location Permission
//                askPermissionForBackgroundUsage();
//            }
//        }else
//        {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_READ_LOCATION);
//        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_LOCATION);

    }

    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {

        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, MY_PERMISSIONS_REQUEST_NOTIFICATION);
    }

    private void askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                SharedPreferenceHelper.putString(this, BAKIT_DEVICE_OS, "android");
                SharedPreferenceHelper.putString(this, BAKIT_DEVICE_OS_VERSION, Build.VERSION.RELEASE);
                SharedPreferenceHelper.putString(this, BAKIT_DEVICE_ID, getUUID());

                boolean isForeground = mBoardActive.getIsForeground();
                /** Start the JobDispatcher to check for and post location */
                StartWorker(isForeground);
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            }
        } else if (requestCode == BACKGROUND_LOCATION_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // User granted for Background Location Permission.
            } else {
                // User declined for Background Location Permission.
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_NOTIFICATION) {

            // If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                // Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void StartWorker(boolean isForeground) {
        Log.d(TAG, "[BAKit]  StartWorker()");
//        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
//
//        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, repeatInterval, TimeUnit.MINUTES)
//                .addTag(TAG)
//                .setConstraints(constraints)
//                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
//                        2,
//                        TimeUnit.MINUTES)
//                .build();
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(FETCH_LOCATION_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
//        SharedPreferenceHelper.putBoolean(this, IS_FOREGROUND, isForeground);

        if (isForeground) {
            WorkManager.getInstance(this).cancelAllWork();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (!serviceIsRunningInForeground(this)) {
                Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(serviceIntent);
                } else {
                    this.startService(serviceIntent);
                }*/
            }
        } else {
            SharedPreferenceHelper.putBoolean(this, IS_FOREGROUND, false);
            Intent serviceIntent = new Intent(this, LocationUpdatesService.class);
            this.stopService(serviceIntent);

            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

            PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, repeatInterval, TimeUnit.MINUTES)
                    .addTag(TAG)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                            2,
                            TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance(this).enqueueUniquePeriodicWork(FETCH_LOCATION_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
        }
    }


    /**
     * get Device UUID to Create Event
     */
    private String getUUID() {
        String uniqueID = null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

        if (uniqueID == null) {
            uniqueID = SharedPreferenceHelper.getString(this, PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferenceHelper.putString(this, PREF_UNIQUE_ID, uniqueID);
            }
        }

        return uniqueID;
    }


    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (this.getPackageName().equals(service.service.getPackageName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }
}
