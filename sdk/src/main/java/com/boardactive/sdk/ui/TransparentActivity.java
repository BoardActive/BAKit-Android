/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boardactive.sdk.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.boardactive.sdk.bootservice.AdDropJobDispatcherService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

/**
 * The only activity in this sample. Displays UI widgets for requesting and removing location
 * updates, and for the batched location updates that are reported.
 *
 * Location updates requested through this activity continue even when the activity is not in the
 * foreground. Note: apps running on "O" devices (regardless of targetSdkVersion) may receive
 * updates less frequently than the interval specified in the {@link LocationRequest} when the app
 * is no longer in the foreground.
 */
public class TransparentActivity extends Activity  {
    private static final String JOB_TAG = "AdDropBootReceiver";
    private static final String TAG = TransparentActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ad_drop);
//        JobScheduler jobScheduler;
//        ComponentName componentName;
//        JobInfo jobInfo;
//        Context context = this;
//        jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
//        componentName = new ComponentName(context, LocationJobService.class);
//        jobInfo = new JobInfo.Builder(1, componentName)
//                .setMinimumLatency(10000) //10 sec interval
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
//        jobScheduler.schedule(jobInfo);

        Log.d(TAG, "TransparentActivity calling Locationjobservice");
        Context context = this;

//        Intent service = new Intent(context, BootAlarmService.class);
//        //service.setAction(RECEIVE_BOOT_COMPLETED);
//        context.startService(service);

//        JobScheduler jobScheduler;
//        ComponentName componentName;
//        JobInfo jobInfo;
//
//        jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
//        componentName = new ComponentName(context, AdDropJobService.class);
//        jobInfo = new JobInfo.Builder(1, componentName)
//                .setMinimumLatency(10000) //10 sec interval
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
//        jobScheduler.schedule(jobInfo);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob;
        myJob = dispatcher.newJobBuilder()
                .setService(AdDropJobDispatcherService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        dispatcher.mustSchedule(myJob);
        //startService(new Intent(this, com.boardactive.sdk.bootservice.LocationJobService.class));

        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}