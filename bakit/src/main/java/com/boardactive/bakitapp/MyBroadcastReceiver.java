package com.boardactive.bakitapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = BoardActive.class.getName();
    private static final String FETCH_LOCATION_WORKER_NAME = "Location";
    @Override
    public void onReceive(Context context, Intent intent) {

        startWorker();
    }

    private void startWorker() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(FETCH_LOCATION_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
    }
}
