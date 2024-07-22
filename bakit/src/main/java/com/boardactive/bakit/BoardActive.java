package com.boardactive.bakit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.boardactive.bakit.R;
import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.boardactive.bakit.models.Attributes;
import com.boardactive.bakit.models.Coordinate;
import com.boardactive.bakit.models.GeoData;
import com.boardactive.bakit.models.GeofenceLocationModel;
import com.boardactive.bakit.models.Me;
import com.boardactive.bakit.models.MeRequest;
import com.boardactive.bakit.models.Stock;
import com.boardactive.bakit.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: In the class constructor you will need to pass in the getApplicationContext() from the main app
 * <p>
 * This SDK will track the device location and post the location to the BoardActive API
 * For it to operate correctly is requires:
 * User must allow location permissions
 * AppUrl is set by the main app and points to the correct BoardActive server
 * AppID is set by the main app (provided by BoardActive)
 * AppKey is set by the main app (provided by BoardActive)
 * AppToken is set by the main app (generated from Firebase service)
 * AppVersion is set by the main app (the current version of the main app)
 * <p>
 * You will need to initialize() from the main app to start the JobDispatcher
 * <p>
 * The main app will need to support Firebase Cloud Messaging. There can only be on service to receive
 * FCM messages. If multiple are declared in the MAnifest the only the first one will be chosen.
 * For this reason we have not incorporated Firebase Messaging in the SDK
 * <p>
 * All http calls use a callback to send reponse to main app
 * <p>
 * The JobDispatcher service will poll the device location and post it to the BoardActive server
 * The JobDispatcher is launched using the initialize() function. It also starts automatically at boot
 * using the BootReceiver BroadcastReceiver
 */

public class BoardActive implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * Default API Global values
     */
    public final static String APP_URL_PROD = "https://api.boardactive.com/mobile/v1/";

    public final static String APP_URL_DEV = "https://dev-api.boardactive.com/mobile/v1/";

    public final static String APP_ID = "2305";

    public final static String APP_KEY = "a7d23aec-326f-4c31-8650-3c91d2dab154";

    public final static String APP_VERSION = "1.0.0";
    /**
     * Global keys
     */
    public final static String BAKIT_USER_DATA = "BAKIT_USER_DATA";
    public final static String BAKIT_USER_EMAIL = "BAKIT_USER_EMAIL";
    public final static String BAKIT_USER_PASSWORD = "BAKIT_USER_PASSWORD";
    public final static String BAKIT_URL = "BAKIT_URL";
    public final static String BAKIT_APP_KEY = "BAKIT_APP_KEY";
    public final static String BAKIT_APP_ID = "BAKIT_APP_ID";
    public final static String BAKIT_APP_VERSION = "BAKIT_APP_VERSION";
    public final static String BAKIT_DEVICE_ID = "BAKIT_DEVICE_ID";
    public final static String BAKIT_DEVICE_OS = "BAKIT_DEVICE_OS";
    public final static String BAKIT_DEVICE_TOKEN = "BAKIT_DEVICE_TOKEN";
    public final static String BAKIT_DEVICE_OS_VERSION = "BAKIT_DEVICE_OS_VERSION";
    public final static String BAKIT_APP_TEST = "BAKIT_APP_TEST";
    public final static String BAKIT_LOCATION_LATITUDE = "BAKIT_LOCATION_LATITUDE";
    public final static String BAKIT_LOCATION_LONGITUDE = "BAKIT_LOCATION_LONGITUDE";
    public final static String BAKIT_PAST_LOCATION_LATITUDE = "BAKIT_PAST_LOCATION_LATITUDE";
    public final static String BAKIT_PAST_LOCATION_LONGITUDE = "BAKIT_PAST_LOCATION_LONGITUDE";
    private static final String BAKIT_IS_FOREGROUND = "isforeground";
    public static final String BAKIT_GEOFENCE_DATE = "GeofenceDate";
    public final static String BAKIT_GEOFENCE_LOCATION_LATITUDE = "BAKIT_GEOFENCE_LOCATION_LATITUDE";
    public final static String BAKIT_GEOFENCE_LOCATION_LONGITUDE = "BAKIT_GEOFENCE_LOCATION_LONGITUDE";

    public static final String TAG = BoardActive.class.getName();
    private static final int REQUEST_CODE = 1;
    private Context mContext;
    protected GsonBuilder gsonBuilder = new GsonBuilder();
    protected Gson gson;
    private static final String FETCH_LOCATION_WORKER_NAME = "Location";
    public List<Geofence> geofenceList = new ArrayList<>();
    public ArrayList<Coordinate> locationList = new ArrayList<>();
    public ArrayList<String> geofenceId = new ArrayList<>();
    public ArrayList<Coordinate> geoList = new ArrayList<>();

    private PendingIntent geofencePendingIntent;
    // periodic worker takes 15 mins repeatInterval by default to restart even if you set <15 mins.
    private int repeatInterval = 1;
    private GeofencingClient geofencingClient;
    public boolean isPermissionGranted = false;
    public GoogleApiClient googleApiClient;
    public double latitude;
    public double longitude;
    public boolean isHoursExcedeed = false;
    public boolean isAppEnabled = false;
    public LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    public Location lastLocation;
    public Location previousLocation;
    public boolean isAPICalled = true;
    private FusedLocationProviderClient fusedLocationClient;
    public String appName;

    /**
     * Constuctor
     *
     * @param context Pass in the Application Context from the main app
     */
    public BoardActive(Context context) {
        mContext = context;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG, "onStartJob() " + currentDateTimeString);
        gson = gsonBuilder.setLenient().create();
        googleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        geofencingClient = LocationServices.getGeofencingClient(mContext);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    /**
     * Handles the server error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     * @param err Volley error
     * @return String
     */
    private static String handleServerError(Object err) {
        VolleyError error = (VolleyError) err;
        NetworkResponse response = error.networkResponse;
        try {
            String string = new String(error.networkResponse.data);
            JSONObject object = new JSONObject(string);
            if (object.has("message")) {
                return response.statusCode + " - " + object.get("message").toString();
            } else if (object.has("error_description")) {
                return response.statusCode + " - " + object.get("error_description").toString();
            }
            //        } catch (JSONException e)
        } catch (Exception e) {
            return "Could not parse response: " + e.toString();
        }
        // invalid request
        return error.getMessage();

    }

    public String getAppUrl() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_URL, null);
    }

    /**
     * Set and Get variables
     */
    public void setAppUrl(String URL) {
        SharedPreferenceHelper.putString(mContext, BAKIT_URL, URL);
    }

    public String getAppKey() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_APP_KEY, null);
    }

    public void setAppKey(String AppKey) {
        SharedPreferenceHelper.putString(mContext, BAKIT_APP_KEY, AppKey);
    }

    public String getAppId() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_APP_ID, null);
    }

    public void setAppId(String AppId) {
        SharedPreferenceHelper.putString(mContext, BAKIT_APP_ID, AppId);
    }

    public String getAppToken() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_TOKEN, null);

    }

    public void setAppToken(String AppToken) {
        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_TOKEN, AppToken);
    }

    public String getAppVersion() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_APP_VERSION, null);

    }

    public void setAppVersion(String AppVersion) {
        SharedPreferenceHelper.putString(mContext, BAKIT_APP_VERSION, AppVersion);
    }

    public String getAppOSVersion() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null);
    }

    public void setAppOSVersion(String AppOSVersion) {
        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS_VERSION, AppOSVersion);
    }

    public String getAppOS() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null);
    }

    public void setAppOS(String AppOS) {
        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS, AppOS);
    }

    public String getUserEmail() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_USER_EMAIL, null);
    }

    public void setUserEmail(String AppUSerEmail) {
        SharedPreferenceHelper.putString(mContext, BAKIT_USER_EMAIL, AppUSerEmail);
    }

    public String getUserPassword() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_USER_PASSWORD, null);
    }

    public void setUserPassword(String AppUserPassword) {
        SharedPreferenceHelper.putString(mContext, BAKIT_USER_PASSWORD, AppUserPassword);
    }

    public String getLatitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_LOCATION_LATITUDE, null);
    }

    public void setLatitude(String Latitude) {
        SharedPreferenceHelper.putString(mContext, BAKIT_LOCATION_LATITUDE, Latitude);
    }

    public void setPastLatitude(Double latitude) {
        if (latitude == null) {
            SharedPreferenceHelper.putString(mContext, BAKIT_PAST_LOCATION_LATITUDE, "");

        } else {
            SharedPreferenceHelper.putString(mContext, BAKIT_PAST_LOCATION_LATITUDE, latitude.toString());

        }
    }

    public void setgeofenceLatitude(Double latitude) {
        SharedPreferenceHelper.putString(mContext, BAKIT_GEOFENCE_LOCATION_LATITUDE, latitude.toString());

    }

    public void setgeofenceLongitude(Double longitude) {
        SharedPreferenceHelper.putString(mContext, BAKIT_GEOFENCE_LOCATION_LONGITUDE, longitude.toString());

    }

    public String getGeoLatitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_GEOFENCE_LOCATION_LATITUDE, null);
    }

    public String getGeoLongitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_GEOFENCE_LOCATION_LONGITUDE, null);
    }

    public void setPastLongitude(Double longitude) {
        if (longitude == null) {
            SharedPreferenceHelper.putString(mContext, BAKIT_PAST_LOCATION_LONGITUDE, "");

        } else {
            SharedPreferenceHelper.putString(mContext, BAKIT_PAST_LOCATION_LONGITUDE, longitude.toString());

        }
    }

    public String getPastLatitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_PAST_LOCATION_LATITUDE, null);
    }

    public String getPastLongitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_PAST_LOCATION_LONGITUDE, null);
    }

    public String getLongitude() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_LOCATION_LONGITUDE, null);
    }

    public void setLongitude(String Longitude) {
        SharedPreferenceHelper.putString(mContext, BAKIT_LOCATION_LONGITUDE, Longitude);
    }

    public String getAppTest() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_APP_TEST, null);
    }

    public void setAppTest(String AppTest) {
        SharedPreferenceHelper.putString(mContext, BAKIT_APP_TEST, AppTest);
    }

    public Boolean getIsForeground() {
        return SharedPreferenceHelper.getBoolean(mContext, BAKIT_IS_FOREGROUND, false);
    }

    public void setIsForeground(boolean IsForeground) {
        SharedPreferenceHelper.putBoolean(mContext, BAKIT_IS_FOREGROUND, IsForeground);
    }

    public void setLocationArrayList(ArrayList<Coordinate> locationModelArrayList) {
        SharedPreferenceHelper.putArrayList(mContext, locationModelArrayList);
    }

    public void setGeoLocationArrayList(ArrayList<Coordinate> locationModelArrayList) {
        SharedPreferenceHelper.putGeoArrayList(mContext, locationModelArrayList);
    }

    public ArrayList<Coordinate> getLocationArrayList() {
        return SharedPreferenceHelper.getArrayList(mContext, "locationList");
    }

    public ArrayList<Coordinate> getGeoLocationArrayList() {
        return SharedPreferenceHelper.getArrayList(mContext, "GeolocationList");
    }

    public void setGeofenceDate(String geofenceDate) {
        SharedPreferenceHelper.putString(mContext, BAKIT_GEOFENCE_DATE, geofenceDate);
    }

    public void setCurrentLocationArrayList(ArrayList<Location> locationModelArrayList) {
        SharedPreferenceHelper.putCurrentLocationArrayList(mContext, locationModelArrayList);
    }

    public ArrayList<Location> getCurrentLocationArrayList() {
        return SharedPreferenceHelper.getCurrentLocationArrayList(mContext, "CurrentLocationList");
    }

    public String getGeofenceDate() {
        return SharedPreferenceHelper.getString(mContext, BAKIT_GEOFENCE_DATE, "");
    }

    /**
     * Set SDK Core Variables and launches Job Dispatcher
     * Checks is location permissions are on if not it will prompt user to turn on location
     * permissions.
     */
    public void initialize() {

        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS, "android");
        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS_VERSION, Build.VERSION.RELEASE);
        SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_ID, getUUID(mContext));
        //setLocationArrayList(locationList);

            /*Intent serviceIntent = new Intent(this, LocationService.class);
            stopService(serviceIntent);*/
        if (geofencingClient != null)
            googleApiClient.connect();
        /** Start the JobDispatcher to check for and post location */
        //StartWorker();
        //getLocationList();
        Log.d(TAG, "[BAKit]  initialize()");

    }

    /**
     * Private Function to launch serve to get and post location to BoaradActive Platform
     */
    public void StartWorker(String appName) {
        Log.d(TAG, "[BAKit]  StartWorker()");
        this.appName = appName;

//        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
//
//        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, 15  , TimeUnit.MINUTES)
//                //.addTag(TAG)
//                .setConstraints(constraints)
//                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
//                        2,
//                        TimeUnit.MINUTES)
//                .build();
//        WorkManager.getInstance(mContext.getApplicationContext()).enqueue(periodicWork);
//        Toast.makeText(mContext, "Location Worker Started : " + periodicWork.getId(), Toast.LENGTH_SHORT).show();

        boolean isForeground = getIsForeground();

        if (isForeground) {
            WorkManager.getInstance(mContext).cancelAllWork();
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (!serviceIsRunningInForeground(mContext)) {
                Intent serviceIntent = new Intent(mContext, LocationUpdatesService.class);
                serviceIntent.putExtra("appName",appName);
                ContextCompat.startForegroundService(mContext, serviceIntent);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(serviceIntent);
                } else {
                    mContext.startService(serviceIntent);
                }*/
            }
        } else {
            setIsForeground(false);
            Intent serviceIntent = new Intent(mContext, LocationUpdatesService.class);
            mContext.stopService(serviceIntent);

            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

            PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(LocationWorker.class, repeatInterval, TimeUnit.MINUTES)
                    .addTag(TAG)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL,
                            2,
                            TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance(mContext).enqueueUniquePeriodicWork(FETCH_LOCATION_WORKER_NAME, ExistingPeriodicWorkPolicy.REPLACE, periodicWork);
        }
    }


    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (mContext.getPackageName().equals(service.service.getPackageName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Check is all required variables are set
     */
    public Boolean isRegisteredDevice() {
        Boolean isLoggedIn = true;
        String APP_URL = getAppUrl();
        String APP_KEY = getAppKey();
        String APP_ID = getAppId();
        String APP_VERSION = getAppVersion();
        String APP_TOKEN = getAppToken();
        String APP_OS = getAppOS();
        String APP_OS_VERSION = getAppOSVersion();

        try {
            if (APP_URL.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppUrl is empty");
            }

            if (APP_KEY.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppKey is empty");
            }

            if (APP_ID.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppId is empty");
            }

            if (APP_VERSION.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppVersion is empty");
            }

            if (APP_TOKEN.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppToken is empty");
            }

            if (APP_OS.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppOS is empty");
            }

            if (APP_OS_VERSION.isEmpty()) {
                isLoggedIn = false;
                Log.d(TAG, "[BAKit] AppOSVersion is empty");
            }

        } catch (Exception e) {
            // This will catch any exception, because they are all descended from Exception
            System.out.println("Error " + e.getMessage());
            isLoggedIn = false;
            return isLoggedIn;
        }

        return isLoggedIn;

    }

    /**
     * Empty required variables
     */
    public void unRegisterDevice() {
        setAppKey(null);
        setAppId(null);
        setAppToken(null);
        setAppUrl(null);
        setAppOS(null);
        setAppOSVersion(null);
    }

    /**
     * get Device UUID to Create Event
     */
    private String getUUID(Context context) {
        String uniqueID = null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

        if (uniqueID == null) {
            uniqueID = SharedPreferenceHelper.getString(mContext, PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferenceHelper.putString(mContext, PREF_UNIQUE_ID, uniqueID);
            }
        }

        return uniqueID;
    }


    /**
     * post RegisterDevice with BoardActive Platform
     *
     * @param callback to return response from server
     */

    public void registerDevice(final PostRegisterCallback callback) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "me";

            StringRequest str = new StringRequest(Request.Method.PUT, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    JSONObject json = null;
                    Boolean isActive=false;
                    try {
                        json = new JSONObject(response);
                         isActive = json.getBoolean("isActive");
                        if (isActive) {
                            SharedPreferenceHelper.putString(mContext, Constants.APP_STATUS, "Enable");
                        } else {
                            SharedPreferenceHelper.putString(mContext, Constants.APP_STATUS, "Disable");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }


                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {

                        Attributes attributes = new Attributes();
                        Stock stock = new Stock();

                        /** Check for Location permission. If not then prompt to ask */
                        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION);

                        if (permissionState != PackageManager.PERMISSION_GRANTED) {
                            stock.setLocationPermission("false");
                        } else {
                            stock.setLocationPermission("true");
                        }

                        if (NotificationManagerCompat.from(mContext).areNotificationsEnabled())
                            stock.setNotificationPermission("true");
                        else
                            stock.setNotificationPermission("false");
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
                        stock.setDateLastOpenedApp(getLocalToUTCDate(cal.getTime()));
                        attributes.setStock(stock);

                        MeRequest meRequest = new MeRequest();
                        meRequest.setEmail("");
                        meRequest.setDeviceOS(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
                        meRequest.setDeviceOSVersion(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
                        Calendar cal1 = Calendar.getInstance();
                        meRequest.setDateLastOpenedApp(getLocalToUTCDate(cal1.getTime()));
                        meRequest.setAttributes(attributes);

                        //parse request object to json format and send as request body
                        return gson.toJson(meRequest).getBytes();
                    } catch (Exception e) {
                        Log.e(TAG, "error parsing request body to json");
                    }


                    return super.getBody();
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }
                //            @Override
                //            protected Map<String, String> getParams() {
                //                Map<String, String> params = new HashMap<String, String>();
                //                params.put("email", SharedPreferenceHelper.getString(mContext, BAKIT_USER_EMAIL, null));
                //                params.put("deviceOS", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
                //                params.put("deviceOSVersion", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
                //                Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
                //                return params;
                //            }
            };

            queue.add(str);
    }
    public String getLocalToUTCDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFmt.format(time);
    }

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     */
    public void getMe(final GetMeCallback callback) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "me";

            StringRequest str = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //  params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    return params;
                }

            };

            queue.add(str);
        }

    }

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     */
    public void putMe(final PutMeCallback callback) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "me";

            StringRequest str = new StringRequest(Request.Method.PUT, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", SharedPreferenceHelper.getString(mContext, BAKIT_USER_EMAIL, null));
                    params.put("deviceOS", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
                    params.put("deviceOSVersion", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
                    Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
                    return params;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        String json = "{" +
                                "email:" + SharedPreferenceHelper.getString(mContext, BAKIT_USER_EMAIL, null) + ", " +
                                "deviceOS:" + SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null) + ", " +
                                "deviceOSVersion:" + SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null) + ", " +
                                "attributes:" + "{ " +
                                "stock:" + "{)," +
                                "custom:" + "{} " +
                                "}" +
                                "}";

                        //parse request object to json format and send as request body
                        return gson.toJson(json).getBytes();
                    } catch (Exception e) {
                        Log.e(TAG, "error parsing request body to json");

                    }
                    return super.getBody();
                }
            };

            queue.add(str);
        }

    }

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     * @param me       type of Event to log
     */
    public void putMe(final PutMeCallback callback, final Me me) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "me";

            StringRequest str = new StringRequest(Request.Method.PUT, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                //            @Override
                //            protected Map<String, String> getParams() {
                //                Map<String, String> params = new HashMap<String, String>();
                //                params.put("email", getSharedPreference(BAKIT_USER_EMAIL));
                //                params.put("deviceOS", getSharedPreference(BAKIT_DEVICE_OS));
                //                params.put("deviceOSVersion", getSharedPreference(BAKIT_DEVICE_OS_VERSION));
                //                Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
                //                return params;
                //            }


                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Gson gson = new Gson();

                        Attributes attributes = new Attributes();
                        Stock stock = new Stock();

                        /** Check for Location permission. If not then prompt to ask */
                        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION);

                        if (permissionState != PackageManager.PERMISSION_GRANTED) {
                            stock.setLocationPermission("false");
                        } else {
                            stock.setLocationPermission("true");
                        }

                        stock.setNotificationPermission("true");

                        stock.setName(me.getAttributes().getStock().getName() + "");
                        stock.setEmail(me.getAttributes().getStock().getEmail() + "");
                        stock.setPhone(me.getAttributes().getStock().getPhone() + "");
                        stock.setPhone(me.getAttributes().getStock().getDateBorn() + "");
                        stock.setGender(me.getAttributes().getStock().getGender() + "");
                        stock.setFacebookUrl(me.getAttributes().getStock().getFacebookUrl() + "");
                        stock.setLinkedInUrl(me.getAttributes().getStock().getLinkedInUrl() + "");
                        stock.setTwitterUrl(me.getAttributes().getStock().getTwitterUrl() + "");
                        stock.setInstagramUrl(me.getAttributes().getStock().getInstagramUrl() + "");
                        stock.setAvatarUrl(me.getAttributes().getStock().getAvatarUrl() + "");

                        if (me.getAttributes().getStock().getName().equals("")) {
                            stock.setName(null);
                        } else {
                            stock.setName(me.getAttributes().getStock().getName() + "");
                        }

                        if (me.getAttributes().getStock().getEmail().equals("")) {
                            stock.setEmail(null);
                        } else {
                            stock.setEmail(me.getAttributes().getStock().getEmail() + "");
                        }

                        if (me.getAttributes().getStock().getPhone().equals("")) {
                            stock.setPhone(null);
                        } else {
                            stock.setPhone(me.getAttributes().getStock().getPhone() + "");
                        }

                        if (me.getAttributes().getStock().getDateBorn().equals("")) {
                            stock.setDateBorn(null);
                        } else {
                            stock.setDateBorn(me.getAttributes().getStock().getDateBorn());
                        }

                        if (me.getAttributes().getStock().getGender().equals("")) {
                            stock.setGender(null);
                        } else {
                            stock.setGender(me.getAttributes().getStock().getGender() + "");
                        }

                        if (me.getAttributes().getStock().getFacebookUrl().equals("")) {
                            stock.setFacebookUrl(null);
                        } else {
                            stock.setFacebookUrl(me.getAttributes().getStock().getFacebookUrl() + "");
                        }

                        if (me.getAttributes().getStock().getLinkedInUrl().equals("")) {
                            stock.setLinkedInUrl(null);
                        } else {
                            stock.setLinkedInUrl(me.getAttributes().getStock().getLinkedInUrl() + "");
                        }

                        if (me.getAttributes().getStock().getTwitterUrl().equals("")) {
                            stock.setTwitterUrl(null);
                        } else {
                            stock.setTwitterUrl(me.getAttributes().getStock().getTwitterUrl() + "");
                        }

                        if (me.getAttributes().getStock().getInstagramUrl().equals("")) {
                            stock.setInstagramUrl(null);
                        } else {
                            stock.setInstagramUrl(me.getAttributes().getStock().getInstagramUrl() + "");
                        }

                        if (me.getAttributes().getStock().getAvatarUrl().length() == 0) {
                            stock.setAvatarUrl(null);
                        } else {
                            stock.setAvatarUrl(me.getAttributes().getStock().getAvatarUrl() + "");
                        }

                        attributes.setStock(stock);

                        MeRequest meRequest = new MeRequest();
                        meRequest.setEmail("");
                        meRequest.setDeviceOS(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
                        meRequest.setDeviceOSVersion(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
                        meRequest.setAttributes(attributes);

                        //parse request object to json format and send as request body
                        return gson.toJson(meRequest).getBytes();
                    } catch (Exception e) {
                        Log.e(TAG, "error parsing request body to json");
                    }
                    return super.getBody();
                }
            };

            queue.add(str);
        }

    }


    public void putCustomAtrributes(final PutMeCallback callback, final HashMap<String, Object> me) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "me";

            StringRequest str = new StringRequest(Request.Method.PUT, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                //            @Override
                //            protected Map<String, String> getParams() {
                //                Map<String, String> params = new HashMap<String, String>();
                //                params.put("email", getSharedPreference(BAKIT_USER_EMAIL));
                //                params.put("deviceOS", getSharedPreference(BAKIT_DEVICE_OS));
                //                params.put("deviceOSVersion", getSharedPreference(BAKIT_DEVICE_OS_VERSION));
                //                Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
                //                return params;
                //            }


                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        Gson gson = new Gson();

                        Attributes attributes = new Attributes();
                        Stock stock = new Stock();
                        //                    Custom custom = new Custom();

                        /** Check for Location permission. If not then prompt to ask */
                        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                                Manifest.permission.ACCESS_FINE_LOCATION);

                        //                    if (permissionState != PackageManager.PERMISSION_GRANTED) {
                        //                        stock.setLocationPermission("false");
                        //                    } else {
                        //                        stock.setLocationPermission("true");
                        //                    }
                        //
                        //                    stock.setNotificationPermission("true");

                        attributes.setCustom(me);

                        MeRequest meRequest = new MeRequest();
                        meRequest.setEmail("");
                        meRequest.setDeviceOS(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
                        meRequest.setDeviceOSVersion(SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
                        meRequest.setAttributes(attributes);

                        //parse request object to json format and send as request body
                        return gson.toJson(meRequest).getBytes();
                    } catch (Exception e) {
                        Log.e(TAG, "error parsing request body to json");
                    }
                    return super.getBody();
                }
            };

            queue.add(str);
        }

    }

    /** Private Function to launch serve to get and post location to BoaradActive Platform */

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback               to return response from server
     * @param name                   type of Event to log
     * @param baMessageId            the id of the message to log event
     * @param baNotificationId       the id of the message to log event
     * @param firebaseNotificationId firebase notification id to log the event
     */
    public void postEvent(final PostEventCallback callback, final String name, final String baMessageId, final String baNotificationId, final String firebaseNotificationId) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;

            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "events";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "postEvent onResponse: " + response.toString());
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {
                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

//                @Override
//                public Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("name", name);
//                    params.put("baMessageId", baMessageId);
//                    params.put("baNotificationId", baNotificationId);
//                    params.put("firebaseNotificationId", firebaseNotificationId);
//                    return params;
//                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                //            @Override
                //            public byte[] getBody() throws AuthFailureError {
                //                try {
                //                    String json = "{" +
                //                            "name:" +  name + ", " +
                //                            "messageId:" +  messageId + ", " +
                //                            "firebaseNotificationId:" +  firebaseNotificationId +
                //                            "}";
                //
                //                    //parse request object to json format and send as request body
                //                    return gson.toJson(json).getBytes();
                //                } catch (Exception e) {
                //                    Log.e(TAG, "error parsing request body to json");
                //
                //                }
                //                return super.getBody();
                //            }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("name", name);
                        jsonObject.put("baMessageId", baMessageId);
                        jsonObject.put("baNotificationId", baNotificationId);
                        jsonObject.put("firebaseNotificationId", firebaseNotificationId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String requestBody = jsonObject.toString();

                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }
            };

            queue.add(stringRequest);
        }

    }

    /**
     * post Location and record in the BoardActive Platform
     *
     * @param callback   to return response from server
     * @param latitude   current latitude
     * @param longitude  current longitude
     * @param deviceTime current Date and Time
     */
    public void





    postLocation(final PostLocationCallback callback, final Double latitude, final Double longitude, final String deviceTime) {
        setLatitude(latitude.toString());
        setLongitude(longitude.toString());
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "locations";

            Log.d(TAG, "[BAKit] postLocation uri: " + uri);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] postLocation onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    isAPICalled = false;
                    //appendLog(response.toString());

                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {
                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                //            @Override
                //            public Map<String, String> getParams() {
                //                Map<String, String> params = new HashMap<>();
                //                params.put("latitude", latitude.toString());
                //                params.put("longitude", longitude.toString());
                //                params.put("deviceTime", deviceTime);
                //                Log.d(TAG, "[BAKit] postLocation params: " + params.toString());
                //                return params;
                //            }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                //            @Override
                //            public byte[] getBody() throws AuthFailureError {
                //                try {
                //                    String json = "{" +
                //                            "latitude:" +  latitude.toString() + ", " +
                //                            "longitude:" +  longitude.toString() + ", " +
                //                            "deviceTime:" +  deviceTime +
                //                            "}";
                //                    //parse request object to json format and send as request body
                //                    return gson.toJson(json).getBytes();
                //                } catch (Exception e) {
                //                    Log.e(TAG, "error parsing request body to json");
                //                }
                //                return super.getBody();
                //            }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("latitude", latitude.toString());
                        jsonObject.put("longitude", longitude.toString());
                        jsonObject.put("deviceTime", deviceTime);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String requestBody = jsonObject.toString();

                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

            };

            queue.add(stringRequest);
        }

    }

    /**
     * post Login to retrieve detail of a registered user in BoardActive Platform
     * This is only used by the Demo App
     *
     * @param callback to return response from server
     * @param email    registered email
     * @param password password of registered email
     */
    public void postLogin(final PostLoginCallback callback, final String email, final String password) {
        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        VolleyLog.DEBUG = true;
        String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "login";

        Log.d(TAG, "[BAKit] postLogin uri: " + uri);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "[BAKit] postLogin onResponse: " + response.toString());
                try {

                    JSONObject obj = new JSONObject(response);

                    for (int i = 0; i < obj.getJSONArray("apps").length(); i++) {
                        try {
                            JSONObject jsonObject = obj.getJSONArray("apps").getJSONObject(i);
                            Boolean isActive = jsonObject.getBoolean("isActive");
                            if (isActive) {
                                SharedPreferenceHelper.putString(mContext, Constants.APP_STATUS, "Enable");
                                Log.e("is Active", isActive.toString());
                            } else {
                                SharedPreferenceHelper.putString(mContext, Constants.APP_STATUS, "Disable");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                }
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String readableError = handleServerError(error);
                Log.d(TAG, readableError);
                callback.onResponse(readableError);
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                Log.d(TAG, "[BAKit] postLogin params: " + params.toString());
                return params;
            }

        };

        queue.add(stringRequest);
    }

    /**
     * Generate Headers required to use BoardActive API Endpoints
     */
    private Map<String, String> GenerateHeaders() {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("X-BoardActive-App-Key", SharedPreferenceHelper.getString(mContext, BAKIT_APP_KEY, null));
        headers.put("X-BoardActive-App-Id", SharedPreferenceHelper.getString(mContext, BAKIT_APP_ID, null));
        headers.put("X-BoardActive-App-Version", SharedPreferenceHelper.getString(mContext, BAKIT_APP_VERSION, null));
        headers.put("X-BoardActive-Device-Token", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_TOKEN, null));
        headers.put("X-BoardActive-Device-OS", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
        headers.put("X-BoardActive-Device-OS-Version", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
        headers.put("X-BoardActive-Is-Test-App", SharedPreferenceHelper.getString(mContext, BAKIT_APP_TEST, "0"));
        headers.put("X-BoardActive-Latitude", SharedPreferenceHelper.getString(mContext, BAKIT_LOCATION_LATITUDE, null));
        headers.put("X-BoardActive-Longitude", SharedPreferenceHelper.getString(mContext, BAKIT_LOCATION_LONGITUDE, null));
        Log.d(TAG, "[BAKit] GenerateHeaders: " + headers.toString());

        return headers;
    }

    public void getAttributes(final GetMeCallback callback) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "attributes";

            StringRequest str = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {

                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    return params;
                }

            };

            queue.add(str);
        }


    }

    /**
     * get geoefence list in boardacrive
     *
     * @param callback to return response from server
     */
    public void getGeoCoordinates(final GetMeCallback callback) {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

            VolleyLog.DEBUG = true;
            String locationUrl = "geofenceLocation";
            String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + locationUrl;
            StringRequest str = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "[BAKit] getLocation onResponse: " + response.toString());
                    VolleyLog.wtf(response);
                    callback.onResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String readableError = handleServerError(error);
                    Log.d(TAG, readableError);
                    callback.onResponse(readableError);
                }
            }) {


                @Override
                public Priority getPriority() {
                    return Priority.HIGH;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return GenerateHeaders();
                }

                @Override
                public byte[] getBody() throws AuthFailureError {

                    JSONObject jsonObject = new JSONObject();
                    try {
//                        if(LocationUpdatesBroadcastReceiver.firstLocation != null) {
//                            jsonObject.put("latitude", LocationUpdatesBroadcastReceiver.firstLocation.getLatitude());
//                            jsonObject.put("longitude", LocationUpdatesBroadcastReceiver.firstLocation.getLongitude());
//                            jsonObject.put("radius", 2000);
//                        }
                        if (latitude == 0.0 && longitude == 0.0) {
                            jsonObject.put("latitude", getLatitude());
                            jsonObject.put("longitude", getLongitude());
                            jsonObject.put("radius", Constants.API_DISTANCE);
                        } else {
                            jsonObject.put("latitude", latitude);
                            jsonObject.put("longitude", longitude);
                            jsonObject.put("radius", Constants.API_DISTANCE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String requestBody = jsonObject.toString();

                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.d(TAG, "[BAKit] postLogin params: " + params.toString());
                    //                params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    return params;
                }

            };
            str.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(str);
        }

    }

    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(mContext, RequestPermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

    }
    public void checkNotificationPermissions() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(mContext, RequestPermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }

    }

    public void setupLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); // this is the key ingredient
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) mContext, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "location lat/lng: " + location.getLatitude() + " " + location.getLongitude());
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    setLatitude("" + latitude);
                    setLongitude("" + longitude);
                    Log.e("lat", "" + latitude);
                    Log.e("long", "" + longitude);

                    if (getPastLongitude() == null && getPastLatitude() == null) {
                        setPastLatitude(latitude);
                        setPastLongitude(longitude);
                    }
                    if(getPastLatitude() != null && getPastLongitude() != null)
                    {
                        if (Double.parseDouble(getPastLatitude()) != latitude && Double.parseDouble(getPastLongitude()) != longitude) {
                            if (!Constants.FIRST_TIME_GET_GEOFENCE) {
                                Constants.FIRST_TIME_GET_GEOFENCE = true;
                                getLocationList();
                            }
                            Location temp = new Location(LocationManager.GPS_PROVIDER);
                            temp.setLatitude(Double.parseDouble(getPastLatitude()));
                            temp.setLongitude(Double.parseDouble(getPastLongitude()));
                            Log.e("distance", "" + location.distanceTo(temp));

                            Log.d(TAG, "PassLoc lat/lng: " + getPastLatitude() + " " + getPastLongitude());
                            if (location.distanceTo(temp) > Constants.DISTANCE) {
                                //setPastLongitude(null);
                                // setPastLatitude(null);
                                setPastLatitude(location.getLatitude());
                                setPastLongitude(location.getLongitude());
                                Log.e("new lat", getPastLatitude());
                                Log.e("new lat", getPastLongitude());
                                Log.e("enter into distance", "enter into distance");
                                setLocationArrayList(null);
                                getLocationList();

                            }

                        }

                    }

                }
            });
        }

    }

    public void getLastLocation() {
        try {
            FusedLocationProviderClient mLocationProvider = LocationServices.getFusedLocationProviderClient(mContext);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.e("last location", "" + latitude);
                        Log.e("last location long", "" + longitude);
                       // setPastLatitude(location.getLatitude());
                        //setPastLongitude(location.getLongitude());
                    } else {
                        Log.d("TAG", "location is null");
                        setupLocationRequest();
                    }
                }

            });
//            mLocationProvider.getLastLocation().addOnSuccessListener((Activity) mContext, location -> {
//                if (location != null) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    Log.e("last location", "" + latitude);
//                    Log.e("last location long", "" + longitude);
//                    setPastLatitude(location.getLatitude());
//                    setPastLongitude(location.getLongitude());
//                }
//            }).addOnFailureListener(e -> {
//                //some log here
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //  ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
            //       Constants.MY_PERMISSIONS_REQUEST_READ_LOCATION);
        } else {
            getLastLocation();
//            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//          // Log.e("last location",""+lastLocation.getLatitude());
//            //Log.e("last location long",""+lastLocation.getLongitude());
//
//            if(lastLocation != null){
//                //setPastLatitude(lastLocation.getLatitude());
//                //setPastLongitude(lastLocation.getLongitude());
//            }
            setupLocationRequest();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //latitude = location.getLatitude();
        // longitude = location.getLongitude();
    }

    /**
     * RegisterDevice Callback providing HTTP Response
     */
    public interface PostRegisterCallback<T> {
        void onResponse(T value);
    }

    /**
     * getMe Callback providing HTTP Response
     */
    public interface GetMeCallback<T> {
        void onResponse(T value);
    }

    /**
     * putMe Callback providing HTTP Response
     */
    public interface PutMeCallback<T> {
        void onResponse(T value);
    }

    /**
     * postEvent Callback providing HTTP Response
     */
    public interface PostEventCallback<T> {
        void onResponse(T value);
    }

    /**
     * postLocation Callback providing HTTP Response
     */
    public interface PostLocationCallback<T> {
        void onResponse(T value);
    }

    /**
     * postLogin Callback providing HTTP Response
     */
    public interface PostLoginCallback<T> {
        void onResponse(T value);
    }

    /*add geofence*/

    public void addGeofence() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencePendingIntent = null;
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(aVoid -> {
                    //                    Toast.makeText(mContext
                    //                            , "Geofencing has started", Toast.LENGTH_SHORT).show();
                    Log.e("geofencing started..", "geofence");


                })
                .addOnFailureListener(e -> {
                    Log.e("error", e.getMessage());
                    //                    Toast.makeText(mContext
                    //                            , "Geofencing failed", Toast.LENGTH_SHORT).show();

                });


    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        // Reuse the PendingIntent if we already have it.
//        if (geofencePendingIntent != null) {
//            return geofencePendingIntent;
//        }
        // Toast.makeText(mContext, "starting broadcast", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext,GeofenceBroadCastReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            geofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;
    }

    public void removeGeofence(Context context, String id,Boolean iscomesFromBroadcast) {
        geofencingClient.removeGeofences(Collections.singletonList(id))
                .addOnSuccessListener(aVoid -> {
                    //     Toast.makeText(context, "Geofencing has been removed", Toast.LENGTH_SHORT).show();
                    Log.e("geofencing removed..", "geofence");
                    setLocationArrayList(null);
                    //getLocationList();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context
                            , "Geofencing could not be removed", Toast.LENGTH_SHORT).show();
                });
    }
    /* get coorindates from server*/

    public void getLocationList() {
        if (SharedPreferenceHelper.getString(mContext, Constants.APP_STATUS, "").equals("Enable")) {
            if (getLocationArrayList() == null) {
                getGeoCoordinates(new BoardActive.GetMeCallback() {
                    @Override
                    public void onResponse(Object value) {
                        try {
                            JsonElement jelement = new JsonParser().parse(value.toString());
                            JsonObject jobject = jelement.getAsJsonObject();
                            Gson gson = new GsonBuilder().setLenient().create();

                            GeofenceLocationModel geofenceLocationModel = gson.fromJson(jobject, GeofenceLocationModel.class);
                            locationList.clear();
                            locationList = new ArrayList<>();
                            ArrayList<LatLng>  arrayListLatLng = new ArrayList<LatLng>();
//                            for (int j = 0; j < geofenceLocationModel.getData().size(); j++) {
//                                GeoData geoData = geofenceLocationModel.getData().get(j);
//                                if (getGeofencePendingIntent() != null) {
//                                    removeGeofence(mContext, geoData.getId().toString());
//                                }
//                            }
                                ArrayList<Double> distanceList = new ArrayList<Double>();
                            for (int j = 0; j < geofenceLocationModel.getData().size(); j++) {
                                GeoData geoData = geofenceLocationModel.getData().get(j);
                                if (getGeofencePendingIntent() != null) {
                                    removeGeofence(mContext, geoData.getId().toString(),false);
                                }
                                for (int i = 0; i < geoData.getCoordinates().size(); i++) {
                                    Coordinate locationModel = geoData.getCoordinates().get(i);
                                    locationModel.setLastNotifyDate("");
                                    locationModel.setId(geoData.getId());
                                    locationModel.setType(geoData.getType());

                                    if(locationModel.getType().equals("polygon")){
                                        arrayListLatLng.add(new LatLng(Double.parseDouble(geoData.getCoordinates().get(i).getLatitude()),Double.parseDouble(geoData.getCoordinates().get(i).getLongitude())));
                                       Log.e( "",String.valueOf(computeCentroid(arrayListLatLng).latitude +""+String.valueOf(computeCentroid(arrayListLatLng).longitude)));
                                        Location coordinateLatLng = new Location(LocationManager.GPS_PROVIDER);
                                        coordinateLatLng.setLatitude(Double.parseDouble(geoData.getCoordinates().get(i).getLatitude()));
                                        coordinateLatLng.setLongitude(Double.parseDouble(geoData.getCoordinates().get(i).getLongitude()));
                                        Location centerLatLng = new Location(LocationManager.GPS_PROVIDER);
                                        centerLatLng.setLatitude(computeCentroid(arrayListLatLng).latitude);
                                        centerLatLng.setLongitude(computeCentroid(arrayListLatLng).longitude);
                                        distanceList.add((double) coordinateLatLng.distanceTo(centerLatLng));
                                        locationModel.setLatitude(String.valueOf(computeCentroid(arrayListLatLng).latitude));
                                        locationModel.setLongitude(String.valueOf(computeCentroid(arrayListLatLng).longitude));

                                    }
                                    if (geoData.getRadius() != null) {
                                        locationModel.setRadius(geoData.getRadius());

                                    } else {
                                        if(Collections.max(distanceList)>0.0){
                                            locationModel.setRadius(Collections.max(distanceList).intValue());
                                        }

                                    }
                                    //Log.e("max",Collections.max(distanceList).toString());
                                    locationModel.setLatitude(locationModel.getLatitude());
                                    locationModel.setLongitude(locationModel.getLongitude());
                                    locationList.add(locationModel);
                                }
                                //comment for geofence circular date (3-11-2022)
//                                Coordinate locationModel = geoData.getCoordinates().get(0);
//                                locationModel.setLastNotifyDate("");
//                                locationModel.setId(geoData.getId());
//                                locationModel.setRadius(geoData.getRadius());
//                                //locationModel.setCoordinates(geoData.getCoordinates());
//                                locationModel.setLatitude(geoData.getCoordinates().get(0).getLatitude());
//                                locationModel.setLongitude(geoData.getCoordinates().get(0).getLongitude());
//                                locationModel.setType(geoData.getType());
//                                locationList.add(locationModel);

                            }
                            setLocationArrayList(locationList);
                            Log.e("locationlist", "" + locationList.size());
                            Log.e("sharedpref size", "" + getLocationArrayList().size());
                            setUpRegion();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            } else {
                setUpRegion();

            }
        }
    }

    private void sendNotification(String locId, String response, Context context) {
        String channelId = "BAKit";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ba_logo)
                .setContentTitle("Location Reached")
                .setContentText("Location Api Called of getlocation " + locId + response)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, builder.build());
    }

    public void setUpRegion() {
        ArrayList<Coordinate> locationNewArrayList = new ArrayList<>();
        if (getLocationArrayList() != null && getLocationArrayList().size() > 0) {
            for (int i = 0; i < getLocationArrayList().size(); i++) {
                Coordinate coordinateModel = getLocationArrayList().get(i);
                if (coordinateModel.getLastNotifyDate() != null && !coordinateModel.getLastNotifyDate().equals("")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss a");
                    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                    // 2022-10-19T14:06: 2022-10-19T14:06:49.941Z49.941Z
                    Date date1 = null;
                    try {
                        date1 = dateFormat.parse(coordinateModel.getLastNotifyDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date date2 = null;
                    try {
                        date2 = dateFormat.parse(dateFormat.format(Calendar.getInstance().getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long difference = 0;
                    if (date2 != null) {
                        if (date1 != null) {
                            difference = date2.getTime() - date1.getTime();
                            Log.e("differecnce", "" + difference);

                            long hours = 0;
                            long minutes = 0;
                            int geofenceTimeLimit = 24;
                            minutes
                                    = (difference
                                    / (1000 * 60))
                                    % 60;
                            hours
                                    = (difference
                                    / (1000 * 60 * 60))
                                    % 24;
                            Log.e("hours", "" + hours);
                            Log.e("minutes", "" + minutes);

                            if (Integer.parseInt(String.valueOf(hours)) > geofenceTimeLimit) {
                                locationNewArrayList.clear();
                                locationNewArrayList.addAll(getLocationArrayList());
                                Log.e("minutes", "" + minutes);

                            } else {
                                isHoursExcedeed = false;
                            }


                        }
                    }


                } else {
                    locationNewArrayList.clear();
                    locationNewArrayList.addAll(getLocationArrayList());


                }

            }
        }
        if (locationNewArrayList.size() > 0) {
            Log.e("new loc size", "" + locationNewArrayList.size());
            geofenceList.clear();
            for (int i = 0; i < locationNewArrayList.size(); i++) {
                //Log.e("i",""+i);
                Coordinate coordinateLocationModel = locationNewArrayList.get(i);
                if (i >= 100) {
                    break;
                }
                if (coordinateLocationModel != null && coordinateLocationModel.getRadius() != null) {
                    createGeofence(coordinateLocationModel.getId().toString(), coordinateLocationModel, coordinateLocationModel.getRadius());
                }
            }
            Log.e("geofenceList", "" + geofenceList.size());
        }


    }

    public void createGeofence(String id, Coordinate locationModel, int radius) {
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                //change with id
                .setRequestId(id)
                //.setRequestId(locationModel.getLatitude() + locationModel.getLongitude())
                .setCircularRegion(
                        Double.parseDouble(locationModel.getLatitude()),
                        Double.parseDouble(locationModel.getLongitude()),
                        radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .build());
        addGeofence();
    }

    public void appendLog(String text) {
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private boolean isPointInPolygon(LatLng tap, ArrayList<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(tap, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    private boolean rayCastIntersect(LatLng tap, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = tap.latitude;
        double pX = tap.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    private LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }}




