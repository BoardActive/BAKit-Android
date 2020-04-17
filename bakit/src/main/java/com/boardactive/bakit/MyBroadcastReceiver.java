package com.boardactive.bakit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class MyBroadcastReceiver extends BroadcastReceiver {
    boolean isForeground= false;
    public static final String TAG = BoardActive.class.getName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
        if(intent !=null){
            isForeground = intent.getBooleanExtra("isForeground",false);
        }

        startWorker();
    }

    private void startWorker() {
        if(isForeground){
            PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(ForegroundLocationWorker.class,1,TimeUnit.MINUTES)
                    .addTag(TAG)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                            2,
                            TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance().enqueueUniquePeriodicWork("ForegroundLocation", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
        }else{
        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, 1, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("Location", ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
        }
    }
}
