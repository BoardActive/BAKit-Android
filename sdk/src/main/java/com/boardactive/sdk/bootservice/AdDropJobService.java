package com.boardactive.sdk.bootservice;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.models.AdDropLatLng;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AdDropJobService extends JobService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    /**
     * Update interval of location request
     */
    private final int UPDATE_INTERVAL = 5000;

    /**
     * fastest possible interval of location request
     */
    private final int FASTEST_INTERVAL = 2500;

    /**
     * The Job scheduler.
     */
    JobScheduler jobScheduler;

    /**
     * The Tag.
     */
    String TAG = "AdDropJobService";

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
    private Location lastLocation;

    /**
     * Method is called when location is changed
     * @param location - location from fused location provider
     */
    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged [" + location + "]");
        lastLocation = location;
        writeActualLocation(location);
    }

    /**
     * extract last location if location is not available
     */
    @SuppressLint("MissingPermission")
    private void getLastKnownLocation() {
        //Log.d(TAG, "getLastKnownLocation()");
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {

            Log.i(TAG, "LastKnown location. " +
                    "Long: " + lastLocation.getLongitude() +
                    " | Lat: " + lastLocation.getLatitude());
            writeLastLocation();
            startLocationUpdates();

            mAdDropLatLng.setLat(String.valueOf(lastLocation.getLongitude()));
            mAdDropLatLng.setLng(String.valueOf(lastLocation.getLatitude()));
            getObservable().subscribeWith(getObserver());

        } else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();

            //here we can show Alert to start location
        }
    }

    /**
     * this method writes location to text view or shared preferences
     * @param location - location from fused location provider
     */
    @SuppressLint("SetTextI18n")
    private void writeActualLocation(Location location) {
        Log.d(TAG, location.getLatitude() + ", " + location.getLongitude());
        //here in this method you can use web service or any other thing
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
        //Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Default method of service
     * @param params - JobParameters params
     * @return boolean
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        startJobAgain();

        createGoogleApi();

        return false;
    }

    /**
     * Create google api instance
     */
    private void createGoogleApi() {
        //Log.d(TAG, "createGoogleApi()");
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

    /**
     * disconnect google api
     * @param params - JobParameters params
     * @return result
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        googleApiClient.disconnect();
        return false;
    }

    /**
     * starting job again
     */
    private void startJobAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "Job Started");
            ComponentName componentName = new ComponentName(getApplicationContext(),
                    AdDropJobService.class);
            jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
            JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                    .setMinimumLatency(10000) //10 sec interval
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).setRequiresCharging(false).build();
            jobScheduler.schedule(jobInfo);
        }
    }

    /**
     * this method tells whether google api client connected.
     * @param bundle - to get api instance
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    /**
     * this method returns whether connection is suspended
     * @param i - 0/1
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"connection suspended");
    }

    /**
     * this method checks connection status
     * @param connectionResult - connected or failed
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"connection failed");
    }

    /**
     * this method tells the result of status of google api client
     * @param status - success or failure
     */
    @Override
    public void onResult(@NonNull Status status) {
        Log.d(TAG,"result of google api client : " + status);
    }



    //Create GeoPoint
    private AdDropLatLng mAdDropLatLng = new AdDropLatLng();
    public static final String LAT = "LAT";
    public static final String LNG = "LNG";


    public Observable<AdDropBookmarkResponse> getObservable(){
        return NetworkClient.getRetrofit(mAdDropLatLng.getLat(), mAdDropLatLng.getLng()).create(NetworkInterface.class)
                .createGeopoint(mAdDropLatLng)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDropBookmarkResponse> getObserver(){
        return new DisposableObserver<AdDropBookmarkResponse>() {

            @Override
            public void onNext(@io.reactivex.annotations.NonNull AdDropBookmarkResponse adDropBookmarkResponse) {
                Log.d(TAG,"AdDropJobService() OnNext");
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Log.d(TAG,"AdDropJobService() onError"+ e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"AdDropJobService() onComplete");
            }
        };
    }
}
