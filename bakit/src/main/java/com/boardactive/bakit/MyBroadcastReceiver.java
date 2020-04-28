package com.boardactive.bakit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = BoardActive.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();

        startWorker();
    }

    private void startWorker() {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
    }
}
