package com.boardactive.bakit;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.boardactive.bakit.boot.JobDispatcherService;
import com.boardactive.bakit.models.Event;
import com.boardactive.bakit.models.Response;
import com.boardactive.bakit.models.User;
import com.boardactive.bakit.network.NetworkClient;
import com.boardactive.bakit.network.NetworkInterface;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BoardActive extends Application {
    private static final String JOB_TAG = "BoardActive";

    private FirebaseJobDispatcher mDispatcher;
    private Context mContext;

    private BAKitConstants mBAKitConstants = new BAKitConstants();

    public BoardActive(Context context) {
        mContext = context;

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Logg.d("onStartJob() " + currentDateTimeString);

    }

    /** Set SDK Core Variables and launch Job Dispatcher */
    public void initialize(String fcmToken, String AppKey, String AppId, String AppVersion) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mBAKitConstants.API_URL, "https://api.boardactive.com/mobile/v1/");
        editor.putString(mBAKitConstants.APP_KEY, AppKey);
        editor.putString(mBAKitConstants.APP_ID, AppId);
        editor.putString(mBAKitConstants.APP_VERSION, AppVersion);
        editor.putString(mBAKitConstants.DEVICE_TOKEN, fcmToken);
        editor.putString(mBAKitConstants.DEVICE_OS, "android");
        editor.putString(mBAKitConstants.DEVICE_OS_VERSION, Build.VERSION.RELEASE);
        editor.putString(mBAKitConstants.DEVICE_ID, getUUID(mContext));
        editor.commit();

        int permissionState = ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionState != PackageManager.PERMISSION_GRANTED){
            mContext.startActivity(new Intent(mContext, PermissionActivity.class));
        }

        StartJob();
        Logg.d();
    }

    public interface RegisterCallback<T> {
        void onResponse(T value);
    }

    public interface EventCallback<T> {
        void onResponse(T value);
    }

    public void RegisterDevice(RegisterCallback callback) {
        putMe().subscribeWith(putMe(callback));
        Logg.d("RegisterDevice()");
    }

    public void createEvent(EventCallback callback, String eventName, String fcm_notificationId, String mesageId) {
        mEvent.setname(eventName);
        mEvent.setfirebaseNotificationId(fcm_notificationId);
        mEvent.setmessageId(mesageId);
        postEvent().subscribeWith(postEvent(callback));
        Logg.d("postEvent()");
    }


    private void StartJob() {
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(mContext));
        Job myJob = mDispatcher.newJobBuilder()
                .setService(JobDispatcherService.class)
                .setTag(JOB_TAG)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(5, 30))
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        mDispatcher.mustSchedule(myJob);

    }

    private Observable<User> putMe(){
        return NetworkClient.getRetrofit(mContext).create(NetworkInterface.class)
                .putMe()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<User> putMe(final RegisterCallback callback){
        return new DisposableObserver<User>() {

            @Override
            public void onNext(@io.reactivex.annotations.NonNull User baME) {
                Logg.d("BoardActive() OnNext: " + baME.toString());
                callback.onResponse(baME);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Logg.d("BoardActive() onError"+ e);
                e.printStackTrace();
                callback.onResponse(e);
            }

            @Override
            public void onComplete() {
                Logg.d("BoardActive() onComplete");
            }
        };
    }

    private Event mEvent = new Event();

    private Observable<Response> postEvent(){
        return NetworkClient.getRetrofit(mContext).create(NetworkInterface.class)
                .postEvent(mEvent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<Response> postEvent(final EventCallback callback){
        return new DisposableObserver<Response>() {

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Response response) {
                Logg.d("BoardActive() OnNext: " + response.toString());
                callback.onResponse(response);
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                Logg.d("BoardActive() onError"+ e);
                e.printStackTrace();
                callback.onResponse(e);
            }

            @Override
            public void onComplete() {
                Logg.d("BoardActive() onComplete");
            }
        };
    }

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


    public String getApiUrl() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.API_URL,"");
        return value;
    }

    public String getAppKey() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.APP_KEY,"");
        return value;
    }

    public String getAppId() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.APP_ID,"");
        return value;
    }

    public String getAppVersion() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.APP_VERSION,"");
        return value;
    }

    public String getDeviceToken() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.DEVICE_TOKEN,"");
        return value;
    }

    public String getDeviceOSVersion() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.DEVICE_OS_VERSION,"");
        return value;
    }

    public String getDeviceID() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.DEVICE_ID,"");
        return value;
    }

    public String getDeviceOS() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        final String value = settings.getString(mBAKitConstants.DEVICE_OS,"");
        return value;
    }

}
