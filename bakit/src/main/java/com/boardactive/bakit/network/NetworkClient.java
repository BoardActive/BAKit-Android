package com.boardactive.bakit.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.boardactive.bakit.BAKitConstants;
import com.boardactive.bakit.Logg;

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
    private static Retrofit retrofit;

    // This function sends out our data to the API (events, locations, favorites, ect.)
    public static Retrofit getRetrofit(Context context){

        final BAKitConstants mConstants = new BAKitConstants();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final String mBA_API = settings.getString(mConstants.API_URL,"");
        final String mAppKey = settings.getString(mConstants.APP_KEY,"");
        final String mAppID = settings.getString(mConstants.APP_ID,"");
        final String mAppVersion = settings.getString(mConstants.APP_VERSION,"");
        final String mDeviceToken = settings.getString(mConstants.DEVICE_TOKEN,"");
        final String mDeviceOS = settings.getString(mConstants.DEVICE_OS,"");
        final String mDeviceOSVersion = settings.getString(mConstants.DEVICE_OS_VERSION,"");
        final String mDeviceId = settings.getString(mConstants.DEVICE_ID,"");

        if(retrofit==null){
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
                    String date = df.format(Calendar.getInstance().getTime());

                    Request request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .addHeader("X-BoardActive-App-Key", mAppKey)
                            .addHeader("X-BoardActive-App-Id", mAppID)
                            .addHeader("X-BoardActive-App-Version", mAppVersion)
                            .addHeader("X-BoardActive-Device-Token", mDeviceToken)
                            .addHeader("X-BoardActive-Device-OS", mDeviceOS)
                            .addHeader("X-BoardActive-Device-OS-Version", mDeviceOSVersion)
                            .method(original.method(), original.body())
                            .build();
                    Logg.d("RetroFit: " + request );
                    return chain.proceed(request);

                }
            });


            OkHttpClient okHttpClient = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(mBA_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

        }

        return retrofit;
    }
}
