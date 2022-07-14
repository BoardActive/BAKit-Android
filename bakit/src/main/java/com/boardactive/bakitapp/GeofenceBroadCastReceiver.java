package com.boardactive.bakitapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.boardactive.bakitapp.Tools.SharedPreferenceHelper;
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

public  class GeofenceBroadCastReceiver extends BroadcastReceiver implements LocationListener {
    private String audioFile;
    private BoardActive mBoardActive;
    private double latitude;
    private double longitude;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("on Receiver","on receive");
        mBoardActive = new BoardActive(context);
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("TAG", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.e("on enter","on enter");
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a");
            String date = df.format(Calendar.getInstance().getTime());
          //  Coordinate coordinateModel = new Coordinate();


            //setGeofenceDate(context,date);
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String locId = triggeringGeofences.get(0).getRequestId();
            sendNotification(locId, context);
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
                    Log.d("TAG", "[BAKit] onResponse" + value.toString());
                }
            }, latitude, longitude, date1);
//            Intent serviceIntent = new Intent(context, LocationService.class);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent);
//            } else {
//                context.startService(serviceIntent);
//            }
            mBoardActive.removeGeofence(context);
            mBoardActive.getLocationList();

        } else {
            // Log the error.
            Log.e("TAG", "Error");
        }
    }
    public void setGeofenceDate(Context context,String geofenceDate) {
        SharedPreferenceHelper.putString(context, BoardActive.BAKIT_GEOFENCE_DATE, geofenceDate);
    }
    private void sendNotification(String locId, Context context) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle("Location Reached")
//                .setContentText("you reached " + locId)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setAutoCancel(true);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}