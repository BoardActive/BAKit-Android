package com.boardactive.bakit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardActive {

    private final Context mContext;

    public final static String APP_URL_PROD = "https://api.boardactive.com/mobile/v1/";
    public final static String APP_URL_DEV = "https://springer-api.boardactive.com/mobile/v1/";
    public final static String APP_KEY_PROD = "b70095c6-1169-43d6-a5dd-099877b4acb3";
    public final static String APP_KEY_DEV = "d17f0feb-4f96-4c2a-83fd-fd6302ae3a16";

    public final static String BAKIT_URL = "BAKIT_URL";
    public final static String BAKIT_APP_KEY = "BAKIT_APP_KEY";
    public final static String BAKIT_APP_ID = "BAKIT_APP_ID";
    public final static String BAKIT_APP_VERSION = "BAKIT_APP_VERSION";
    public final static String BAKIT_DEVICE_ID = "BAKIT_DEVICE_ID";
    public final static String BAKIT_DEVICE_OS = "BAKIT_DEVICE_OS";
    public final static String BAKIT_DEVICE_TOKEN = "BAKIT_DEVICE_TOKEN";
    public final static String BAKIT_DEVICE_OS_VERSION = "BAKIT_DEVICE_OS_VERSION";
    public final static String BAKIT_APP_TEST = "BAKIT_APP_TEST";

    private static final String TAG = "[BAKit] BoardActive";

    public LocationCallback BAKIT_LOCATION;

    /** Service to track and post device location */
    private FirebaseJobDispatcher mDispatcher;

    public BoardActive(Context context) {
        mContext = context;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG, "onStartJob() " + currentDateTimeString);
    }

    public void setAppUrl(String URL){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_URL, URL);
        editor.commit();
    }

    public String getAppUrl() {
        return getSharedPrecerence(BAKIT_URL);
    }

    public void setAppKey(String AppKey){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_APP_KEY, AppKey);
        editor.commit();
    }

    public String getAppKey() {
        return getSharedPrecerence(BAKIT_APP_KEY);
    }

    public void setAppId(String AppId){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_APP_ID, AppId);
        editor.commit();
    }

    public String getAppId() {
        return getSharedPrecerence(BAKIT_APP_ID);
    }

    public void setAppToken(String AppToken){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_DEVICE_TOKEN, AppToken);
        editor.commit();
    }

    public String getAppToken() {
        return getSharedPrecerence(BAKIT_DEVICE_TOKEN);
    }

    public void setAppVersion(String AppVersion){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_APP_VERSION, AppVersion);
        editor.commit();
    }

    public String getAppVersion() {
        return getSharedPrecerence(BAKIT_APP_VERSION);
    }

    public void setAppOSVersion(String AppOSVersion){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_DEVICE_OS_VERSION, AppOSVersion);
        editor.commit();
    }

    public String getAppOSVersion() {
        return getSharedPrecerence(BAKIT_DEVICE_OS_VERSION);
    }

    public void setAppOS(String AppOS){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_DEVICE_OS, AppOS);
        editor.commit();
    }

    public String getAppOS() {
        return getSharedPrecerence(BAKIT_DEVICE_OS);
    }

    public void setAppTest(String AppTest){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_APP_TEST, AppTest);
        editor.commit();
    }

    public String getAppTest() {
        return getSharedPrecerence(BAKIT_APP_TEST);
    }

    /** Set SDK Core Variables and launches Job Dispatcher */
    public void initialize() {
        Log.d(TAG, "initialize()");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BAKIT_DEVICE_OS, "android");
        editor.putString(BAKIT_DEVICE_OS_VERSION, Build.VERSION.RELEASE);
        editor.putString(BAKIT_DEVICE_ID, getUUID(mContext));
        editor.commit();

        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionState != PackageManager.PERMISSION_GRANTED){
            Intent intent = new Intent(mContext, PermissionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
        }

        if (getSharedPrecerence(BAKIT_URL).isEmpty() || getSharedPrecerence(BAKIT_URL) == null) {
            setAppUrl(APP_URL_PROD);
        }

        if (isRegisteredDevice()) {
            StartJob();
        } else {
            Log.d(TAG, "DEVICE IS NOT REGISTERED Please set SDK Variables ");
        }
    }

    public Boolean isRegisteredDevice() {
        Boolean isLoggedIn = true;
        String APP_URL = getAppUrl();
        String APP_KEY = getAppKey();
        String APP_ID = getAppId();
        String APP_VERSION = getAppVersion();
        String APP_TOKEN = getAppToken();
        String APP_OS = getAppOS();
        String APP_OS_VERSION = getAppOSVersion();

        if(APP_URL.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The APP URL is not set");
        }

        if(APP_KEY.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The APP KEY is not set");
        }

        if(APP_ID.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The APP ID is not set");
        }

        if(APP_VERSION.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The APP VERSION is not set");
        }

        if(APP_TOKEN.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The DEVICE TOKEN is not set");
        }

        if(APP_OS.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The DEVICE OS is not set");
        }

        if(APP_OS_VERSION.isEmpty()) {
            isLoggedIn = false;
        } else {
            Log.d(TAG, "The DEVICE OS VERSION is not set");
        }

        return isLoggedIn;

    }

    public void unRegisterDevice() {
        setAppKey("");
        setAppId("");
        setAppToken("");
        setAppUrl("");
        setAppOS("");
        setAppOSVersion("");
    }

    /** RegisterDevice Callback providing HTTP Response */
    public interface PostRegisterCallback<T> {
        void onResponse(T value);
    }

    /** PostEvent Callback providing HTTP Response */
    public interface PostEventCallback<T> {
        void onResponse(T value);
    }

    /** PostLocation Callback providing HTTP Response */
    public interface PostLocationCallback<T> {
        void onResponse(T value);
    }

    /** CurrentLocation Callback providing HTTP Response */
    public interface CurrentLocationCallback<T> {
        void onResponse(T value);
    }

    /** Private Function to launch serve to get location and send to BoaradActive Platform */
    private void StartJob() {
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        Job myJob = mDispatcher.newJobBuilder()
                .setService(JobDispatcherService.class)
                .setTag(TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mDispatcher.mustSchedule(myJob);
    }

    /** Private Function to get Device UUID to Create Event */
    private String getUUID(Context context) {
        String uniqueID = null;
        String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }

        return uniqueID;
    }

    public void RegisterDevice(final PostRegisterCallback callback) {
        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        VolleyLog.DEBUG = true;
        String uri = getSharedPrecerence(BAKIT_URL) + "me";

        StringRequest str = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "[BAKit] RegisterDevice onResponse: " + response.toString());
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d(TAG, "[BAkit] RegisterDevice No network available");
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG, "[BAkit] RegisterDevice Error AuthFailureError: " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.d(TAG, "[BAkit] RegisterDevice Error ServerError: " + error.toString());
                } else if (error instanceof ParseError) {
                    Log.d(TAG, "[BAkit] RegisterDevice Error ParseError: " + error.toString());
                } else {
                    Log.d(TAG, "[BAkit] RegisterDevice Error: " + error.toString());
                }

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    callback.onResponse(networkResponse.statusCode);
                }
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
                params.put("email", "");
                params.put("deviceOS", getSharedPrecerence(BAKIT_DEVICE_OS));
                params.put("deviceOSVersion", getSharedPrecerence(BAKIT_DEVICE_OS_VERSION));
                Log.d(TAG, "[BAKit] RegisterDevice params: " + params.toString());
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", "");
                    jsonObject.put("deviceOS", getSharedPrecerence(BAKIT_DEVICE_OS));
                    jsonObject.put("deviceOSVersion", getSharedPrecerence(BAKIT_DEVICE_OS_VERSION));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String requestBody = jsonObject.toString();
                Log.d(TAG, "[BAKit] RegisterDevice requestBody: " + requestBody);

                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

        };

        queue.add(str);
    }

    public void postEvent(final PostEventCallback callback, final String name, final String messageId, final String firebaseNotificationId) {
        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        VolleyLog.DEBUG = true;

        String uri = getSharedPrecerence(BAKIT_URL) + "events";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "postEvent onResponse: " + response.toString());
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NetworkError) {
                    Log.d(TAG, "[BAkit] postEvent No network available");
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG, "[BAkit] postEvent Error AuthFailureError: " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.d(TAG, "[BAkit] postEvent Error ServerError: " + error.toString());
                } else if (error instanceof ParseError) {
                    Log.d(TAG, "[BAkit] postEvent Error ParseError: " + error.toString());
                } else {
                    Log.d(TAG, "[BAkit] postEvent Error: " + error.toString());
                }

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    callback.onResponse(networkResponse.statusCode);
                }
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("messageId", messageId);
                params.put("firebaseNotificationId", firebaseNotificationId);
                params.put("testMsg", null);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return GenerateHeaders();
            }

        };

        queue.add(stringRequest);
    }

    public void postLocation(final PostLocationCallback callback, final Double latitude, final Double longitude, final String deviceTime) {
        RequestQueue queue = AppSingleton.getInstance(mContext).getRequestQueue();

        VolleyLog.DEBUG = true;
        String uri = getSharedPrecerence(BAKIT_URL) + "locations";
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

                if (error instanceof NetworkError) {
                    Log.d(TAG, "[BAkit] postLocation No network available");
                } else if (error instanceof AuthFailureError) {
                    Log.d(TAG, "[BAkit] postLocation Error AuthFailureError: " + error.toString());
                } else if (error instanceof ServerError) {
                    Log.d(TAG, "[BAkit] postLocation Error ServerError: " + error.toString());
                } else if (error instanceof ParseError) {
                    Log.d(TAG, "[BAkit] postLocation Error ParseError: " + error.toString());
                } else {
                    Log.d(TAG, "[BAkit] postLocation Error: " + error.toString());
                }

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    callback.onResponse(networkResponse.statusCode);
                }
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", latitude.toString());
                params.put("longitude", longitude.toString());
                params.put("deviceTime", deviceTime);
                Log.d(TAG, "[BAKit] postLocation params: " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return GenerateHeaders();
            }

        };

        queue.add(stringRequest);
    }

    private Map<String, String> GenerateHeaders() {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        headers.put("X-BoardActive-App-Key", getSharedPrecerence(BAKIT_APP_KEY));
        headers.put("X-BoardActive-App-Id", getSharedPrecerence(BAKIT_APP_ID));
        headers.put("X-BoardActive-App-Version", getSharedPrecerence(BAKIT_APP_VERSION));
        headers.put("X-BoardActive-Device-Token", getSharedPrecerence(BAKIT_DEVICE_TOKEN));
        headers.put("X-BoardActive-Device-OS", getSharedPrecerence(BAKIT_DEVICE_OS));
        headers.put("X-BoardActive-Device-OS-Version", getSharedPrecerence(BAKIT_DEVICE_OS_VERSION));
        headers.put("X-BoardActive-Is-Test-App", getSharedPrecerence(BAKIT_APP_TEST));
        Log.d(TAG, "[BAKit] GenerateHeaders: " + headers.toString());

        return headers;
    }

    private String getSharedPrecerence(String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(name,"");
        return value;
    }

    public void CurrentLocation(final CurrentLocationCallback callback){

        BAKIT_LOCATION = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    callback.onResponse(location);
                }
            };
        };
    }

}


