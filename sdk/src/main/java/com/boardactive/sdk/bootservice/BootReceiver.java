package com.boardactive.sdk.bootservice;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
      public void onReceive(Context context, Intent intent) {

      Toast.makeText(context, "Intent BroadcastReceiver Detected.", Toast.LENGTH_LONG).show();
      System.out.println("This is the BootReceiver");

        /****** For Start Activity *****/
//        Intent i = new Intent(context, AdDropMainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

        /***** For start Service  ****/
        Intent service = new Intent(context, BootAlarmService.class);
        //service.setAction(RECEIVE_BOOT_COMPLETED);
        context.startService(service);

      JobScheduler jobScheduler;
      ComponentName componentName;
      JobInfo jobInfo;

      jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
      componentName = new ComponentName(context, LocationJobService.class);
      jobInfo = new JobInfo.Builder(1, componentName)
              .setMinimumLatency(10000) //10 sec interval
              .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
      jobScheduler.schedule(jobInfo);
      }

}