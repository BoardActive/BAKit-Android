package com.boardactive.bakit.boot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.boardactive.bakit.Logg;
import com.boardactive.bakit.models.Location;
import com.boardactive.bakit.models.Response;
import com.boardactive.bakit.network.NetworkClient;
import com.boardactive.bakit.network.NetworkInterface;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class JobDispatcherService extends JobService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status>

{

    private static final String TAG = "[BoardActive] JobDispatcher";

    /**
     * Update interval of location request
     */
    private final int UPDATE_INTERVAL = 5000;

    /**
     * fastest possible interval of location request
     */
    private final int FASTEST_INTERVAL = 2500;

    /**
     * LocationRequest instance
     */
    private LocationRequest locationRequest;

    /**
     * GoogleApiClient instance
     */
    private GoogleApiClient googleApiClient;

    /**
     * Location instance
     */
    private android.location.Location lastLocation;

    private Context mContext;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Logg.d("onStartJob() " + currentDateTimeString);
        int permissionState = ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionState == PackageManager.PERMISSION_GRANTED){
            createGoogleApi();
        } else {
            Logg.d("No Location Permissions" + currentDateTimeString);
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Logg.d( "onStopJob() Job cancelled!");
        return false;
    }

    /**
     * this method tells whether google api client connected.
     * @param bundle - to get api instance
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logg.d("onConnected()");
        getLastKnownLocation();
    }

    /**
     * this method returns whether connection is suspended
     * @param i - 0/1
     */
    @Override
    public void onConnectionSuspended(int i) {
        Logg.d("connection suspended");
    }

    /**
     * this method checks connection status
     * @param connectionResult - connected or failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logg.d("connection failed");
    }

    /**
     * this method tells the result of status of google api client
     * @param status - success or failure
     */
    @Override
    public void onResult(@NonNull Status status) {
        Logg.d("result of google api client : " + status);
    }

    /**
     * Method is called when location is changed
     * @param location - location from fused location provider
     */
    @Override
    public void onLocationChanged(android.location.Location location) {

        Logg.d( "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    /**
     * extract last location if location is not available
     */
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {

            Logg.d("LastKnown location. " +
                    "  Lat: " + lastLocation.getLatitude() +" | Long: " + lastLocation.getLongitude());
            writeLastLocation();
            startLocationUpdates();

            DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
            String date = df.format(Calendar.getInstance().getTime());

            mLocation.setlatitude(String.valueOf(lastLocation.getLatitude()));
            mLocation.setlongitude(String.valueOf(lastLocation.getLongitude()));
            mLocation.setdeviceTime(date);
            Logg.d( "LATLNG" + mLocation.getlatitude() + mLocation.getlongitude());
            getObservable().subscribeWith(getObserver());

        } else {
            Logg.d("No location retrieved yet");
            startLocationUpdates();

            //here we can show Alert to start location
        }
    }

    /**
     * this method writes location to text view or shared preferences
     * @param location - location from fused location provider
     */
    @SuppressLint("SetTextI18n")
    private void writeActualLocation(android.location.Location location) {
        //here in this method we can do something with the location
    }

    /**
     * this method only provokes writeActualLocation().
     */
    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }

    /**
     * this method fetches location from fused location provider and passes to writeLastLocation
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Create google api instance
     */
    private void createGoogleApi() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //connect google api
        googleApiClient.connect();

    }


    //Create GeoPoint / API data building
    private Location mLocation = new Location();

    public Observable<Response> getObservable(){
        return NetworkClient.getRetrofit(getApplicationContext()).create(NetworkInterface.class)
                .postLocation(mLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public DisposableObserver<Response> getObserver(){
        return new DisposableObserver<Response>() {

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Response locationResponse) {
                Logg.d("Location HTTP Response: " + locationResponse);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Logg.d("JobDispatcherService() onError" + e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Logg.d("JobDispatcherService() onComplete");
            }
        };
    }
}