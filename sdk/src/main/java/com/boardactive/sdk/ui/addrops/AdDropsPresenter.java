package com.boardactive.sdk.ui.addrops;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import com.boardactive.sdk.models.AdDrops;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropsPresenter implements AdDropsPresenterInterface {

    Context mContext;
    AdDropsViewInterface mvi;
    String mLat, mLng;

    private String TAG = "AdDropsPresenter";

    public AdDropsPresenter(AdDropsViewInterface mvi) {
        this.mvi = mvi;
    }

    @Override
    public void getAdDrops(Context context) {
        mContext = context;

        mLat = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("LAT", "");
        mLng = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("LNG", "");

        getObservable().subscribeWith(getObserver());
    }

    public Observable<List<AdDrops>> getObservable(){
        return NetworkClient.getRetrofit(mLat, mLng).create(NetworkInterface.class)
                .getAdDrops()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<List<AdDrops>> getObserver(){
        return new DisposableObserver<List<AdDrops>>() {

            @Override
            public void onNext(@NonNull List<AdDrops> addrops) {
                Log.d(TAG,"OnNext");
                mvi.displayAdDrops(addrops);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"onError"+e);
                e.printStackTrace();
                mvi.displayError("Error fetching Data");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete");
                mvi.hideProgressBar();
            }
        };
    }

}
