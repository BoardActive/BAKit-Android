package com.boardactive.sdk.ui.addrops_fav;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.boardactive.sdk.location.AdDropReceiver;
import com.boardactive.sdk.models.AdDrops;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropsFavPresenter implements AdDropsFavPresenterInterface {

    Context mContext;
    String mLat, mLng;
    AdDropsFavViewInterface mvi;
    private String TAG = "AdDropsFavPresenter";

    public AdDropsFavPresenter(AdDropsFavViewInterface mvi) {
        this.mvi = mvi;
    }

    @Override
    public void getAdDropsFav(Context context) {
        mContext = context;

        mLat = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(AdDropReceiver.LAT, "");
        mLng = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(AdDropReceiver.LNG, "");

        getObservable().subscribeWith(getObserver());
    }

    public Observable<List<AdDrops>> getObservable(){
        return NetworkClient.getRetrofit(mLat, mLng).create(NetworkInterface.class)
                .getAdDropsFav()
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
