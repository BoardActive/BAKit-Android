package com.boardactive.bakit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardActive {

    private static Context mContext;

    private final static String API_URL = "API_URL";
    private final static String APP_KEY = "APP_KEY";
    private final static String APP_ID = "APP_ID";
    private final static String APP_VERSION = "APP_VERSION";
    private final static String DEVICE_ID = "DEVICE_ID";
    private final static String DEVICE_OS = "DEVICE_OS";
    private final static String DEVICE_TOKEN = "DEVICE_TOKEN";
    private final static String DEVICE_OS_VERSION = "DEVICE_OS_VERSION";

    private static final String TAG = "BoardActive";

    /** Service to track and post device location */
    private FirebaseJobDispatcher mDispatcher;

    public BoardActive() {
    }

    public BoardActive(Context context) {
        mContext = context;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Log.d(TAG, "onStartJob() " + currentDateTimeString);
    }

    /** Set SDK Core Variables and launches Job Dispatcher */
    public void initialize(String fcmToken, String AppKey, String AppId, String AppVersion) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_URL, "https://api.boardactive.com/mobile/v1/");
        editor.putString(APP_KEY, AppKey);
        editor.putString(APP_ID, AppId);
        editor.putString(APP_VERSION, AppVersion);
        editor.putString(DEVICE_TOKEN, fcmToken);
        editor.putString(DEVICE_OS, "android");
        editor.putString(DEVICE_OS_VERSION, Build.VERSION.RELEASE);
        editor.putString(DEVICE_ID, getUUID(mContext));
        editor.commit();

        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionState != PackageManager.PERMISSION_GRANTED){
            mContext.startActivity(new Intent(mContext, PermissionActivity.class));
        }

        StartJob();
        Log.d(TAG, "initialize()");
    }


    /** RegisterDevice Callback providing HTTP Response */
    public interface RegisterCallback<T> {
        void onResponse(T value);
    }

    /** CreateEvent Callback providing HTTP Response */
    public interface EventCallback<T> {
        void onResponse(T value);
    }

    /** CreateEvent Callback providing HTTP Response */
    public interface LocationCallback<T> {
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

    public void RegisterDevice(final RegisterCallback callback, final String email) {
        RequestQueue queue = AppController.getInstance().getRequestQueue();

        VolleyLog.DEBUG = true;
        String uri = getSharedPrecerence(API_URL) + "me";

        StringRequest str = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "RegisterDevice onResponse: " + response.toString());
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, errorListener) {
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
                params.put("email", email);
                params.put("deviceOS", getSharedPrecerence(DEVICE_OS));
                params.put("deviceOSVersion", getSharedPrecerence(DEVICE_OS_VERSION));
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("email", email);
                    jsonObject.put("deviceOS", getSharedPrecerence(DEVICE_OS));
                    jsonObject.put("deviceOSVersion", getSharedPrecerence(DEVICE_OS_VERSION));
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

        };

        queue.add(str);
    }


    public void postEvent(final EventCallback callback, final String name, final String messageId, final String firebaseNotificationId) {
        RequestQueue queue = AppController.getInstance().getRequestQueue();

        VolleyLog.DEBUG = true;

        String uri = getSharedPrecerence(API_URL) + "events";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "postEvent onResponse: " + response.toString());
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, errorListener) {
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

    public void postLocation(final LocationCallback callback, final String latitude, final String longitude, final String deviceTime) {
        RequestQueue queue = AppController.getInstance().getRequestQueue();


        VolleyLog.DEBUG = true;
        String uri = getSharedPrecerence(API_URL) + "locations";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "postLocation onResponse: " + response.toString());
                VolleyLog.wtf(response);
                callback.onResponse(response);
            }
        }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("deviceTime", deviceTime);
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
        headers.put("X-BoardActive-App-Key", getSharedPrecerence(APP_KEY));
        headers.put("X-BoardActive-App-Id", getSharedPrecerence(APP_ID));
        headers.put("X-BoardActive-App-Version", getSharedPrecerence(APP_VERSION));
        headers.put("X-BoardActive-Device-Token", getSharedPrecerence(DEVICE_TOKEN));
        headers.put("X-BoardActive-Device-OS", getSharedPrecerence(DEVICE_OS));
        headers.put("X-BoardActive-Device-OS-Version", getSharedPrecerence(DEVICE_OS_VERSION));
        return headers;
    }

    private String getSharedPrecerence(String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(name,"");
        return value;
    }

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error instanceof NetworkError) {
                Log.d(TAG, "No network available");
            } else {
                Log.d(TAG, "Error: " + error.toString());
            }
        }
    };

}


