package com.boardactive.bakit;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.boardactive.bakit.models.Attributes;
import com.boardactive.bakit.models.Custom;
import com.boardactive.bakit.models.Me;
import com.boardactive.bakit.models.MeRequest;
import com.boardactive.bakit.models.Stock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

public class BoardActive {

    /**
     * Default API Global values
     */
    public final static String APP_URL_PROD = "https://api.boardactive.com/mobile/v1/";
    public final static String APP_URL_DEV = "https://dev-api.boardactive.com/mobile/v1/";
    public final static String APP_KEY_PROD = "b70095c6-1169-43d6-a5dd-099877b4acb3";
    public final static String APP_KEY_DEV = "d17f0feb-4f96-4c2a-83fd-fd6302ae3a16";
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
    public static final String TAG = BoardActive.class.getName();
    private static final String IS_FOREGROUND = "isforeground";
    private final Context mContext;
    protected GsonBuilder gsonBuilder = new GsonBuilder();
    protected Gson gson;
    private static final String FETCH_LOCATION_WORKER_NAME = "Location";

    // periodic worker takes 15 mins repeatInterval by default to restart even if you set <15 mins.
    private int repeatInterval = 1;

    /**
     * Constuctor
     *
     * @param context Pass in the Application Context from the main app
     */
    public BoardActive(Context context) {
        mContext = context;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG, "onStartJob() " + currentDateTimeString);
        gson = gsonBuilder.create();
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

    /**
     * Set SDK Core Variables and launches Job Dispatcher
     * Checks is location permissions are on if not it will prompt user to turn on location
     * permissions.
     *
     * @param isForeground
     */
    public void initialize(boolean isForeground) {

        /*if (!NotificationManagerCompat.from(mContext).areNotificationsEnabled()) {
            Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, mContext.getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, 1);
            mContext.startActivity(settingsIntent);
        }*/

            SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS, "android");
            SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_OS_VERSION, Build.VERSION.RELEASE);
            SharedPreferenceHelper.putString(mContext, BAKIT_DEVICE_ID, getUUID(mContext));

            /** Start the JobDispatcher to check for and post location */
            StartWorker(isForeground);
            Log.d(TAG, "[BAKit]  initialize()");

    }




    /**
     * Private Function to launch serve to get and post location to BoaradActive Platform
     *
     * @param isForeground if isForeground true then it starts foreground service else it starts worker.
     */
    private void StartWorker(boolean isForeground) {
        Log.d(TAG, "[BAKit]  StartWorker()");

        SharedPreferenceHelper.putBoolean(mContext, IS_FOREGROUND, isForeground);
        if (isForeground) {
            WorkManager.getInstance(mContext).cancelAllWork();
            if (!serviceIsRunningInForeground(mContext)) {
                Intent serviceIntent = new Intent(mContext, LocationUpdatesService.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(serviceIntent);
                } else {
                    mContext.startService(serviceIntent);
                }
            }
        } else {
            SharedPreferenceHelper.putBoolean(mContext, IS_FOREGROUND, false);
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
//                params.put("email", SharedPreferenceHelper.getString(mContext, BAKIT_USER_EMAIL, null));
//                params.put("deviceOS", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS, null));
//                params.put("deviceOSVersion", SharedPreferenceHelper.getString(mContext, BAKIT_DEVICE_OS_VERSION, null));
//                Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
//                return params;
//            }
        };

        queue.add(str);
    }

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     */
    public void getMe(final GetMeCallback callback) {
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
//                params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return params;
            }

        };

        queue.add(str);
    }

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     */
    public void putMe(final PutMeCallback callback) {
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

    /**
     * post Event and log in the BoardActive Platform
     *
     * @param callback to return response from server
     * @param me       type of Event to log
     */
    public void putMe(final PutMeCallback callback, final Me me) {
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


    public void putCustomAtrributes(final PutMeCallback callback, final HashMap<String, Object> me) {
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

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("baMessageId", baMessageId);
                params.put("baNotificationId", baNotificationId);
                params.put("firebaseNotificationId", firebaseNotificationId);
                return params;
            }

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

    /**
     * post Location and record in the BoardActive Platform
     *
     * @param callback   to return response from server
     * @param latitude   current latitude
     * @param longitude  current longitude
     * @param deviceTime current Date and Time
     */
    public void postLocation(final PostLocationCallback callback, final Double latitude, final Double longitude, final String deviceTime) {
        setLatitude(latitude.toString());
        setLongitude(longitude.toString());

        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        VolleyLog.DEBUG = true;
        String uri = SharedPreferenceHelper.getString(mContext, BAKIT_URL, null) + "locations";

        Log.d(TAG, "[BAKit] postLocation uri: " + uri);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "[BAKit] postLocation onResponse: " + response.toString());
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

    public void checkLocationPermissions(boolean isForeground) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(mContext, RequestPermissionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("isForeground",isForeground);
            mContext.startActivity(intent);
        }
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
}




