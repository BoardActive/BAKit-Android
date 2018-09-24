package com.boardactive.sdk.ui.addrop;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boardactive.sdk.models.AdDrop;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropPresenter implements AdDropPresenterInterface {

    Context mContext;
    AdDropViewInterface mvi;
    private String TAG = "AdDropPresenter";
    Integer mAddrop_id;
    String mLat, mLng;

    public AdDropPresenter(AdDropViewInterface mvi) {
        this.mvi = mvi;
    }

    @Override
    public void getAdDrop(Integer addrop_id, Context context) {
        mContext = context;

        mAddrop_id = addrop_id;
        mLat = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("LAT", "");
        mLng = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("LNG", "");
        Log.w(TAG,"LATLONG"+mLat+" "+mLng);

        getObservable().subscribeWith(getObserver());
    }

    public Observable<AdDrop> getObservable(){
        return NetworkClient.getRetrofit(mLat, mLng).create(NetworkInterface.class)
                .getAdDrop(mAddrop_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDrop> getObserver(){
        return new DisposableObserver<AdDrop>() {

            @Override
            public void onNext(@NonNull AdDrop adDrop) {
                Log.d(TAG,"OnNext");
                mvi.displayAdDrop(adDrop);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"onError"+ e);
                e.printStackTrace();
                mvi.displayError("Error fetching Data ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
                mvi.hideProgressBar();
            }
        };
    }

}
