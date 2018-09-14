/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boardactive.sdk.location;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boardactive.sdk.bootservice.LocationJobService;
import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.models.AdDropLatLng;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;



/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should not be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
public class AdDropReceiver extends BroadcastReceiver {
    private static final String TAG = "AdDropReceiver";

    private AdDropLatLng mAdDropLatLng = new AdDropLatLng();

    static final String ACTION_PROCESS_UPDATES =
            "com.boardactive.sdk.action" +
                    ".PROCESS_UPDATES";

    public static final String LAT = "LAT";
    public static final String LNG = "LNG";

    @Override
    public void onReceive(Context context, Intent intent) {
        long UPDATE_INTERVAL = 60000; // Every 60 seconds.

        long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds

        long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.

        LocationRequest mLocationRequest;

        if (intent != null) {
            final String action = intent.getAction();

            Log.d(TAG, action);

            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Log.d(TAG, "Equals TRUE");
//                LocationResult result = LocationResult.extractResult(intent);
//                if (result != null) {
//                    List<Location> locations = result.getLocations();
//                    for (Location location : locations) {
//                        String lat = String.valueOf(location.getLatitude());
//                        String lng = String.valueOf(location.getLongitude());
//
//                        Log.i(TAG, "Lat: " + lat);
//                        Log.i(TAG, "Lng: " + lng);
//
//                        mAdDropLatLng.setLat(lat);
//                        mAdDropLatLng.setLng(lng);
//
//                        PreferenceManager.getDefaultSharedPreferences(context)
//                                .edit()
//                                .putString(LAT, lat)
//                                .apply();
//
//                        PreferenceManager.getDefaultSharedPreferences(context)
//                                .edit()
//                                .putString(LNG, lng)
//                                .apply();
//
//                        getObservable().subscribeWith(getObserver());
//                    }
//                    Utils.setLocationUpdatesResult(context, locations);
//                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations));
//                    Log.i(TAG, Utils.getLocationUpdatesResult(context));
//
//                }

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
    }

    public Observable<AdDropBookmarkResponse> getObservable(){
        return NetworkClient.getRetrofit(mAdDropLatLng.getLat(), mAdDropLatLng.getLng()).create(NetworkInterface.class)
                .createGeopoint(mAdDropLatLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDropBookmarkResponse> getObserver(){
        return new DisposableObserver<AdDropBookmarkResponse>() {

            @Override
            public void onNext(@NonNull AdDropBookmarkResponse adDropBookmarkResponse) {
                Log.d(TAG,"OnNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"onError"+ e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
            }
        };
    }
}
