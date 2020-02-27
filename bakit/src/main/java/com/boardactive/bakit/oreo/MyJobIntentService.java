package com.boardactive.bakit.oreo;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.boardactive.bakit.BoardActive;
import com.boardactive.bakit.Service.LocationUpdatesIntentService;
import com.google.android.gms.location.LocationResult;


public class MyJobIntentService extends JobIntentService {
    final Handler mHandler = new Handler();
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 2;

    public static final String TAG = LocationUpdatesIntentService.class.getName();

    public static final String ACTION_PROCESS_UPDATES =
            "com.boardactive.bakit.action" +
                    ".PROCESS_UPDATES";

    private BoardActive mBoardActive;


    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, MyJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast("Job Execution Started");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Write code here.. Perform Long operation here such as Download/Upload of file, Sync Some data
         * The system or framework is already holding a wake lock for us at this point
         */
        int maxCount = intent.getIntExtra("maxCountValue", -1);
        /**
         * Suppose we want to print 1 to 1000 number with one-second interval, Each task will take time 1 sec, So here now sleeping thread for one second.
         */
        for (int i = 0; i < maxCount; i++) {
            Log.d(TAG, "onHandleWork: The number is: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showToast("Job Execution Finished");
    }

    // Helper for showing tests
    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}