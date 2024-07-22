package com.boardactive.bakit;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.boardactive.bakit.R;
import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.boardactive.bakit.models.Coordinate;
import com.boardactive.bakit.utils.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GeofenceBroadCastReceiver extends BroadcastReceiver implements LocationListener {
    private String audioFile;
    private BoardActive mBoardActive;
    private double latitude;
    private double longitude;
    private boolean isApiCalled = false;
    public int count=0;
    boolean isSameRegion=false;
    private static final String CHANNEL_ID = "channel_01";
    String locId ="";
    String geofenceTransitionDetails="";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("on Receiver", "on receive");
        mBoardActive = new BoardActive(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("TAG", errorMessage);
            Log.i("TAG", "GeofencingEvent Error : " + geofencingEvent.getErrorCode());

            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.e("on enter", "on enter");
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a");
            // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            String date = df.format(Calendar.getInstance().getTime());
            //  Coordinate coordinateModel = new Coordinate();
            mBoardActive.setgeofenceLatitude(geofencingEvent.getTriggeringLocation().getLatitude());
            mBoardActive.setgeofenceLongitude(geofencingEvent.getTriggeringLocation().getLongitude());

            //setGeofenceDate(context,date);
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            locId = triggeringGeofences.get(0).getRequestId();
            // Get the transition details as a String.
            geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences);


            Location temp = new Location(LocationManager.GPS_PROVIDER);
            temp.setLatitude(Double.parseDouble(mBoardActive.getGeoLatitude()));
            temp.setLongitude(Double.parseDouble(mBoardActive.getGeoLongitude()));
            // Get the transition details as a String.

            DateFormat df1 = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            String date1 = df1.format(Calendar.getInstance().getTime());
            Log.e("get api status1",""+ SharedPreferenceHelper.getBoolean(context, Constants.API_CALLED_STATUS,false));

            //if (SharedPreferenceHelper.getBoolean(context, Constants.API_CALLED_STATUS,false)) {
                Log.e("if get api status",""+ SharedPreferenceHelper.getBoolean(context, Constants.API_CALLED_STATUS,false));
            mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                                          @Override
                                          public void onResponse(Object value) {
                                           //   SharedPreferenceHelper.putBoolean(context, Constants.API_CALLED_STATUS, true);
                                              // mBoardActive.appendLog("enter into api");
                                              // count++;
                                              Log.d("TAG", "[BAKit] onResponse" + value.toString());
                                          }
                                      },geofencingEvent.getTriggeringLocation().getLatitude(), geofencingEvent.getTriggeringLocation().getLongitude()
                    , date1);

          //  }
            if (mBoardActive.getLocationArrayList() != null && mBoardActive.getLocationArrayList().size() > 0) {
                ArrayList<Coordinate> locationList = new ArrayList<>();
                locationList = mBoardActive.getLocationArrayList();

                for (int i = 0; i < locationList.size(); i++) {
                    Coordinate coordinateModel = locationList.get(i);
                    if (locId.equals(coordinateModel.getId().toString())) {
                        Log.e("last notify date",""+coordinateModel.getLastNotifyDate());
                        mBoardActive.removeGeofence(context,locId,true);
                        SharedPreferenceHelper.putBoolean(context, Constants.API_CALLED_STATUS, false);
                       // count=0;

                        if(coordinateModel.getLastNotifyDate() != null && !coordinateModel.getLastNotifyDate().equals("")){
                           // DateFormat df1 = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a");

                            String outputPattern = "dd-MMM-yyyy";
                            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                         //   String date1 = outputFormat.format(Calendar.getInstance().getTime());
//                            try {
//                                //Date dateNow = outputFormat.parse(date1);
//                                Log.e("date",""+date1);
//                                    Date dateNow = df1.parse(coordinateModel.getLastNotifyDate());
//                                    String lastNotifyDate = outputFormat.format(dateNow);
//                                //Date lastNotifyDate=df1.parse(coordinateModel.getLastNotifyDate());
//                               // Date lastNotifyDate = outputFormat.parse(coordinateModel.getLastNotifyDate());
//                                if(lastNotifyDate != null) {
//                                    isSameRegion = date1.equals(lastNotifyDate);
//                                }
//                                Log.e("is same region",""+isSameRegion);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                isSameRegion=false;
//                            }


                        }else
                        {
                            isSameRegion=false;
                        }

                        coordinateModel.setLastNotifyDate(date);
                        locationList.set(i, coordinateModel);
                        mBoardActive.setLocationArrayList(locationList);
                        break;

                    }
                }
               // if(!SharedPreferenceHelper.getBoolean(context, Constants.API_CALLED_STATUS,false)){

               // }

            }
        }
         else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
             List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
             geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                     triggeringGeofences);
           // SharedPreferenceHelper.putBoolean(context, Constants.API_CALLED_STATUS, true);
           // count =0;
           // sendNotification(context,locId,geofenceTransitionDetails);
             Log.e("exit trigger","exit");
             //  mBoardActive.removeGeofence(context);
        } else {
            // Log the error.
            Log.e("TAG", "Error");
            //SharedPreferenceHelper.putBoolean(context, Constants.API_CALLED_STATUS, true);
        }
    }

    public void setGeofenceDate(Context context, String geofenceDate) {
        SharedPreferenceHelper.putString(context, BoardActive.BAKIT_GEOFENCE_DATE, geofenceDate);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    private void sendNotification(Context context,String locId,String notificationDetails) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager)context. getSystemService(Context.NOTIFICATION_SERVICE);
        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(context.getString(R.string.app_name), name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        //   Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ba_logo)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ba_logo))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(context.getString(R.string.geofence_transition_notification_text));

        // .setContentText("you reached" +locId);
        //.setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(context.getString(R.string.app_name)); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        // Issue the notification
        mNotificationManager.notify(m, builder.build());
    }
    private String getGeofenceTransitionDetails(
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "Exited";
            default:
                return "Unknown transition";
        }
    }
}