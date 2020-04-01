package com.boardactive.bakit;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATES = "PROCESS_UPDATES";
    public static final String TAG = LocationUpdatesBroadcastReceiver.class.getName();


    static String addressFragments = "";
    static List<Address> addresses = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {

            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                Log.d("Dhruvit Test::::::"," Address:");
                getLocationUpdates(context,intent,"PROCESS_UPDATES");
            }
        }
    }



    @SuppressLint("MissingPermission")
    public static void getLocationUpdates(final Context context, final Intent intent, String broadcastevent)  {

        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
            String nowDate = formatter.format(today);


            List<Location> locations = result.getLocations();
            Location firstLocation = locations.get(0);

            getAddress(firstLocation,context);
//            LocationRequestHelper.getInstance(context).setValue("locationTextInApp","You are at "+getAddress(firstLocation,context)+"("+nowDate+") with accuracy "+firstLocation.getAccuracy()
//            +" Latitude:"+firstLocation.getLatitude()+" Longitude:"+firstLocation.getLongitude()+" Speed:"+firstLocation.getSpeed()+" Bearing:"+firstLocation.getBearing());
            Log.d("Dhruvit Test::::::"," Latitude:"+firstLocation.getLatitude()+" Longitude:"+firstLocation.getLongitude());
            updateLocation(context,firstLocation);
        }
    }

    private static void updateLocation(Context context, Location firstLocation) {
        //make a log file and write location details

        File file = new File(Environment.getExternalStorageDirectory() + "/BoardActive/");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
            String date = df.format(Calendar.getInstance().getTime());
            File locationLogFile = new File(file, "LocationLogs.txt");
            if(!locationLogFile.exists())
                file.createNewFile();



            BufferedWriter fos = null;
            try {
                fos = new BufferedWriter(new FileWriter(locationLogFile.getPath(), true));
                fos.append("Location: " + getAddress(firstLocation,context) + "\n" + "Time: " + date+ "\n" + "Latitude: " +firstLocation.getLatitude()+"\n" +"Longitude: " + firstLocation.getLongitude() + "\n\n");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String getAddress(Location location,Context context){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Address found using the Geocoder.
        addresses = null;
        Address address = null;
        addressFragments="";
        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            address = addresses.get(0);
        } catch (IOException ioException) {
            Log.e(TAG, "error", ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e(TAG, "Latitude = " + location.getLatitude() +
                    ", Longitude = " + location.getLongitude(), illegalArgumentException);
        }

        if (addresses == null || addresses.size()  == 0) {
            Log.i(TAG, "ERORR");
            addressFragments = "NO ADDRESS FOUND";
        } else {
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments = addressFragments+String.valueOf(address.getAddressLine(i));
            }
        }
        return addressFragments;
    }
}
