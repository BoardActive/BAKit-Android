package com.boardactive.sdk.bootservice;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

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

    @Override
      public void onReceive(Context context, Intent intent) {
      mContext = context;

        /****** Boot Start Transparent Activity *****/
//        Intent i = new Intent(context, AdDropBootActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

      int permissionState = ActivityCompat.checkSelfPermission(context,
              Manifest.permission.ACCESS_FINE_LOCATION);
      if(permissionState == PackageManager.PERMISSION_GRANTED){

          mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));


          // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//          //Android API >=21
//            JobScheduler jobScheduler;
//            ComponentName componentName;
//            JobInfo jobInfo;
//
//            jobScheduler = (JobScheduler) mContext.getSystemService(mContext.JOB_SCHEDULER_SERVICE);
//            componentName = new ComponentName(mContext, AdDropJobService.class);
//            jobInfo = new JobInfo.Builder(1, componentName)
//                    .setMinimumLatency(10000) //10 sec interval
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
//            jobScheduler.schedule(jobInfo);
//        } else {
            scheduleJobDispatcherService();
        }

//      }

    }

    private void scheduleJobDispatcherService() {
        Job myJob = mDispatcher.newJobBuilder()
                .setService(AdDropJobDispatcherService.class)
                .setTag(JOB_TAG)
                .setRecurring(false)
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