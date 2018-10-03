package com.boardactive.sdk.bootservice;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class AdDropBootReceiver extends BroadcastReceiver {
    private static final String JOB_TAG = "AdDropBootReceiver";
    private FirebaseJobDispatcher mDispatcher;

    private Context mContext;

    // This class is triggered a minute or two after the device is restarted, it starts our
    // location reporting service and Firebase Notification Job
    @Override
      public void onReceive(Context context, Intent intent) {
      mContext = context;

      int permissionState = ActivityCompat.checkSelfPermission(context,
              Manifest.permission.ACCESS_FINE_LOCATION);
      if(permissionState == PackageManager.PERMISSION_GRANTED){

          mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            scheduleJobDispatcherService();
        }

    }

    private void scheduleJobDispatcherService() {
        mContext = this.mContext;
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        Job myJob = mDispatcher.newJobBuilder()
                .setService(AdDropJobDispatcherService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mDispatcher.mustSchedule(myJob);
    }

    private void cancelJob(String jobTag) {
        if ("".equals(jobTag)) {
            mDispatcher.cancelAll();
        } else {
            mDispatcher.cancel(jobTag);
        }
    }

}