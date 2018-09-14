package com.boardactive.sdk.bootservice;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import com.boardactive.sdk.R;
import com.firebase.jobdispatcher.Constraint;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class BootReceiver extends BroadcastReceiver {
  private static final String JOB_TAG = "BootReceiver Start Job";
  private FirebaseJobDispatcher mDispatcher;

  private Context mContext;

    @Override
      public void onReceive(Context context, Intent intent) {
      mContext = context;

      Toast.makeText(context, "Intent BroadcastReceiver Detected.", Toast.LENGTH_LONG).show();
      System.out.println("This is the BootReceiver");

        /****** For Start Activity *****/
//        Intent i = new Intent(context, AdDropMainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

        /***** For start Service  ****/
//        Intent service = new Intent(context, BootAlarmService.class);
//        //service.setAction(RECEIVE_BOOT_COMPLETED);
//        context.startService(service);

      Toast.makeText(context, "Boot Start of Location Service",
              Toast.LENGTH_LONG).show();


      int permissionState = ActivityCompat.checkSelfPermission(context,
              Manifest.permission.ACCESS_FINE_LOCATION);
      if(permissionState == PackageManager.PERMISSION_GRANTED){

        // Android O requires a Notification Channel.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//          //Android JobScheduler API 21
//          JobScheduler jobScheduler;
//          ComponentName componentName;
//          JobInfo jobInfo;
//
//          jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
//          componentName = new ComponentName(context, LocationJobService.class);
//          jobInfo = new JobInfo.Builder(1, componentName)
//                  .setMinimumLatency(10000) //10 sec interval
//                  .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
//          jobScheduler.schedule(jobInfo);
//
//        } else {

          Job myJob = mDispatcher.newJobBuilder()
                  .setService(LocationJobDispatcher.class)
                  .setTag(JOB_TAG)
                  .setRecurring(true)
                  .setTrigger(Trigger.executionWindow(5, 30))
                  .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                  .setReplaceCurrent(false)
                  .setConstraints(Constraint.ON_ANY_NETWORK)
                  .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                  .build();
          mDispatcher.mustSchedule(myJob);

//        }

      }

    }

}