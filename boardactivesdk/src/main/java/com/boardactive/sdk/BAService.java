package com.boardactive.sdk;

import android.app.ProgressDialog;
import android.database.Observable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class BAService {

//    private static final String TAG = BAService.class.getSimpleName();
//
//    Observable<List<String>> listObservable = Observable.just(getColorList());
//
//    ProgressDialog progressDoalog;
//
//    private class GetDataTask extends AsyncTask<Void, Void, Response> {
//
//        String IOS_PLAYER_ID = "b11abea5-5a74-43f3-b58a-57f141614695"; //Tom's iPhone
//        String ANDROID_PLAYER_ID = "a66f7bbc-f98c-4684-bbb7-e08721c8ddba"; //Tom's Android
//
//        @Override
//        protected void onPreExecute() {
//            progressDialog.show();
//        }
//
//        @Override
//        protected Response doInBackground(Void... params) {
//            OkHttpClient client = new OkHttpClient();
//
//            EndPointUrl endPointUrl = EndpointUrlProvider.getDefaultEndPointUrl();
//            String url = endPointUrl.getUrl();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .header("Content-Type", "application/json")
//                    .addHeader("X-BoardActive-Application-Key", "key")
//                    .addHeader("X-BoardActive-Application-Secret", "secret")
//                    .addHeader("X-BoardActive-Advertiser-Id", "*")
//                    .addHeader("X-BoardActive-Device-Id", ANDROID_PLAYER_ID)
//                    .addHeader("X-BoardActive-Device-OS", "android")
//                    .addHeader("X-BoardActive-Latitude", "33.889760")
//                    .addHeader("X-BoardActive-Longitude", "-84.469898")
//                    .build()
//                    ;
//
//            try {
//                Response response = client.newCall(request).execute();
//                return response;
//            } catch (IOException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Response response) {
//            if (response != null && response.isSuccessful()) {
//                try {
//
//                    String responseData = response.body().string();
//
//                    GsonBuilder gsonBuilder = new GsonBuilder();
//                    Gson gson = gsonBuilder.create();
//                    List<AdDrop> dataList = Arrays.asList(gson.fromJson(responseData, AdDrop[].class));
////                    generateVertDataList(dataList);
//
//                } catch (IOException e) {
//                    Log.e(TAG, e.toString());
//                }
//            } else {
//            }
//        }
//    }
//
//
//    public static Observable<Response> getData() {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        final OkHttpClient client = new OkHttpClient();
//
//        final Request request = new Request.Builder()
//                .url("https://github.com/ar-android/panfic/raw/master/Panfic/gen/com/ocit/data.json")
//                .get()
//                .addHeader("cache-control", "no-cache")
//                .addHeader("postman-token", "ac8311d5-3876-ea1e-53d3-85f9e397ea21")
//                .build();
//
//        return Observable.(new Observable.OnSubscribe<Response>() {
//            @Override public void call(Subscriber<? super Response> subscriber) {
//                try {
//                    Response response = client.newCall(request).execute();
//                    subscriber.onNext(response);
//                    subscriber.onCompleted();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    subscriber.onError(e);
//                }
//            }
//        });
//    }

}
