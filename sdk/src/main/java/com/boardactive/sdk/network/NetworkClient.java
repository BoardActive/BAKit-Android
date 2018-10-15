package com.boardactive.sdk.network;

import android.content.Context;
import android.util.Log;

import com.boardactive.sdk.adapters.BuildConfigReader;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient{
    private static String mLat;
    private static String mLng;
    private static Context mContext;
    private static Retrofit retrofit;

    // If you are developing/testing your app please use our dev API to avoid test data in production
    //private static String REST_URL = "https://dev-api.boardactive.com/"; //BA DEV URL
    private static String REST_URL = "https://api.boardactive.com/"; //BA URL
    // app_id is the Advertiser's ID from the BoardActive Platform




    public void NetworkClient (Context context){
        mContext = context;
    }


    // This function sends out our data to the API (events, locations, favorites, ect.)
    public static Retrofit getRetrofit(   String lng ,  String lat){
        final String app_id = (String)  BuildConfigReader.getBuildConfigValue("APP_ID");
        final String environment = (String)  BuildConfigReader.getBuildConfigValue("ENVIRONMENT");
        final String DEVICE_TOKEN = FirebaseInstanceId.getInstance().getToken();
         mLat = lat;
         mLng = lng;
         //Get AppID from gradle.properties
         if (app_id == null) {
             Log.w("MISCONFIGURATION", "App_ID was not found, did you set it in the Build.Gradle and gradle.properties?");
         }
         //Get Environment
        Log.d("NetworkClient", "Advertiser ID:"+app_id+"Environment" + environment);
         if (environment=="dev"){
             REST_URL = "https://dev-api.boardactive.com/";
         }

        if(retrofit==null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
                    String date = df.format(Calendar.getInstance().getTime());

                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .addHeader("X-BoardActive-Application-Key", "key")
                            .addHeader("X-BoardActive-Application-Secret", "secret")
                            .addHeader("X-BoardActive-Advertiser-Ids", app_id)
                            .addHeader("X-BoardActive-Device-Token", DEVICE_TOKEN)
                            .addHeader("X-BoardActive-Device-Time", date)
                            .addHeader("X-BoardActive-Device-OS", "android")
                            .addHeader("X-BoardActive-Device-Id", DEVICE_TOKEN)
                            .addHeader("X-BoardActive-Latitude", "" + mLat)
                            .addHeader("X-BoardActive-Longitude", "" + mLng)
                            .method(original.method(), original.body())
                            .build();
                    Log.d("RetroFit","Request sent: " +mLat + "- " + mLng +request );
                    return chain.proceed(request);

                }
            });


            OkHttpClient okHttpClient = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(REST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

        }

        return retrofit;
    }



}
