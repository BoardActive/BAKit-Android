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
package com.boardactive.bakit;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.boardactive.bakit.Tools.Utils;
import com.google.android.gms.location.LocationResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LocationService extends IntentService {

    public static final String TAG = LocationService.class.getName();

    public static final String ACTION_PROCESS_UPDATES =
            "com.boardactive.bakit.action" +
                    ".PROCESS_UPDATES";

    private BoardActive mBoardActive;

    public LocationService() {
        // Name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Location location = result.getLastLocation();

                    DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
                    String date = df.format(Calendar.getInstance().getTime());
                    mBoardActive = new BoardActive(getApplicationContext());

                    Utils.setLocationUpdatesResult(this, locations);
                    Utils.sendNotification(this, Utils.getLocationResultTitle(getApplicationContext(), locations));

                    mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                        @Override
                        public void onResponse(Object value) {
                            Log.d(TAG, "[BAKit] LocationService onResponse" + value.toString());
//                    Log.i(TAG, Utils.getLocationUpdatesResult(this));
                        }
                    }, location.getLatitude(), location.getLongitude(), date);
                    Log.d(TAG, result.toString());
                }
            }
        }
    }
}
