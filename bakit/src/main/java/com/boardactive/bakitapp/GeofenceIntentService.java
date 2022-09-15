package com.boardactive.bakitapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.boardactive.bakitapp.models.Coordinate;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GeofenceIntentService extends IntentService implements LocationListener {
    protected static final String TAG = "GeofenceTransitionsIS";
     BoardActive mBoardActive;
    private double latitude;
    private double longitude;
    public GeofenceIntentService() {
        super(TAG);
       // mBoardActive = new BoardActive(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        Log.i(TAG, "Geofencing Event : " + event);
        if (event.hasError()) {
            Log.i(TAG, "GeofencingEvent Error : " + event.getErrorCode());
            return;
        }

        // Get the type of transition (entry or exit)
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "GeofencingEvent Enter");
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a");
            String date = df.format(Calendar.getInstance().getTime());
            List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
            String locId = triggeringGeofences.get(0).getRequestId();
            //sendNotification(locId, context);
            if(mBoardActive.getLocationArrayList() != null && mBoardActive.getLocationArrayList().size() >0)
            {
                ArrayList<Coordinate> locationList = new ArrayList<>();
                locationList= mBoardActive.getLocationArrayList();

                for(int i=0;i<locationList.size();i++)
                {
                    Coordinate coordinateModel = locationList.get(i);
                    if(locId.equals(coordinateModel.getLatitude() + coordinateModel.getLongitude())){
                        coordinateModel.setLastNotifyDate(date);
                        locationList.set(i,coordinateModel);

                        mBoardActive.setLocationArrayList(locationList);
                        break;

                    }

                }
            }
            DateFormat df1 = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            String date1 = df1.format(Calendar.getInstance().getTime());


            mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                @Override
                public void onResponse(Object value) {
                    Log.d(TAG, "[BAKit] onResponse" + value.toString());
                }
            }, latitude, longitude, date1);
        }
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.i(TAG, "GeofencingEvent dwell");
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            String date = df.format(Calendar.getInstance().getTime());
            mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                @Override
                public void onResponse(Object value) {
                    Log.d(TAG, "[BAKit] onResponse" + value.toString());
                }
            }, latitude, longitude, date);

           // mBoardActive.removeGeofence(context);
            mBoardActive.getLocationList();
          //  mBoardActive.removeGeofence(getApplicationContext());
        }
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "GeofencingEvent Exit");
           // mBoardActive.removeGeofence(context);

            String description = getGeofenceTransitionDetails(event);
            Log.i(TAG, "GeofencingEvent description : " + description);
        }

    }

    private static String getGeofenceTransitionDetails (GeofencingEvent event){
        String transitionString = GeofenceStatusCodes.getStatusCodeString(event.getGeofenceTransition());
        List<String> triggeringIDs = new ArrayList<String>();
        for (Geofence geofence : event.getTriggeringGeofences()) {
            triggeringIDs.add(geofence.getRequestId());
        }
        return String.format("%s: %s", transitionString, TextUtils.join(", ", triggeringIDs));
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
