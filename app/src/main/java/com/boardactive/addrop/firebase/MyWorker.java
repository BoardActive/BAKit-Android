package com.boardactive.addrop.firebase;

import android.content.Context;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    private static final String TAG = "MyWorker";

    public MyWorker(@androidx.annotation.NonNull Context appContext, @androidx.annotation.NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @androidx.annotation.NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return Result.success();
    }
}
