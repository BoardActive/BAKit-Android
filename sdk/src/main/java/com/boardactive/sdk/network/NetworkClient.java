package com.boardactive.sdk.network;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

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

public class NetworkClient {

    public static Context mContext;
    public static Retrofit retrofit;

    private static String REST_URL = "https://dev-api.boardactive.com/"; //BA URL
    //    private static String ANDROID_PLAYER_ID = "d5a6d4ae-592a-4368-ab70-021d040db3b9"; //Tom's Android
    private static String ANDROID_PLAYER_ID = "06a6d615-f88e-4a68-bb23-032297ca7703"; //Tom's Android
    //Test Advertiser ID 333
    private static String HARCODED_ADVERTISER_ID = "333"; //Tom's Android
    private static String ANDROID_LAT = "33.8898219"; //Tom's Latitude
    private static String ANDROID_LNG = "-84.4699005"; //Tom's Longitude


    public void NetworkClient(Context context){
        mContext = context;
    }

    public static Retrofit getRetrofit(final String lng, final String lat){
        final String DEVICE_TOKEN = FirebaseInstanceId.getInstance().getToken();

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
                            .addHeader("X-BoardActive-Advertiser-Ids", "333")
                            .addHeader("X-BoardActive-Device-Token", DEVICE_TOKEN)
                            .addHeader("X-BoardActive-Device-Time", date)
                            .addHeader("X-BoardActive-Device-OS", "android")
                            .addHeader("X-BoardActive-Latitude", "" + lat)
                            .addHeader("X-BoardActive-Longitude", "" + lng)
                            .method(original.method(), original.body())
                            .build();
                    Log.d("RetroFit","Request sent: " +request );
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
