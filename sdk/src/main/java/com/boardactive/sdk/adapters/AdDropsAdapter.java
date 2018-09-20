package com.boardactive.sdk.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.boardactive.sdk.ui.addrop.AdDropActivity;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.boardactive.sdk.R;
import com.boardactive.sdk.models.AdDrops;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropsAdapter extends RecyclerView.Adapter<AdDropsAdapter.AdDropHolder> {
    private static final String TAG = "AdDropsAdapter";

    List<AdDrops> addropList;
    Context context;

    public AdDropsAdapter(List<AdDrops> addropList, Context context) {
        this.addropList = addropList;
        this.context = context;
    }

    @Override
    public AdDropsAdapter.AdDropHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_ad_drops,parent,false);
        final AdDropsAdapter.AdDropHolder mh = new AdDropsAdapter.AdDropHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(final AdDropsAdapter.AdDropHolder holder, final int position) {

//        final Boolean bookmark = addropList.get(position).getIsBookmarked();
        final Boolean bookmark = ((addropList.get(position).getIsBookmarked() == null) ? false : addropList.get(position).getIsBookmarked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("AdDropsAdapter", "position = " + addropList.get(position).getPromotion_id());
                Intent intent = new Intent(context.getApplicationContext(), AdDropActivity.class);
                intent.putExtra("promotion_id", addropList.get(position).getPromotion_id());
                context.getApplicationContext().startActivity(intent);
            }
        });

        holder.tvTitle.setText(addropList.get(position).getTitle());
        holder.tvCategory.setText(addropList.get(position).getCategory());
        holder.tvDescription.setText(addropList.get(position).getDescription());

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));
        builder.build().load(addropList.get(position).getImage_url())
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivAdDrop);

        if(bookmark){
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
            holder.ivFav.setImageDrawable(myDrawable);
        } else {
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
            holder.ivFav.setImageDrawable(myDrawable);
        }

        Log.d(TAG, "");

        holder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmark){
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
                    holder.ivFav.setImageDrawable(myDrawable);
                    addropList.get(position).setIsBookmarked(false);
                    String lat = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LAT", "");
                    String lng = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LNG", "");
                    getObservableRemoveBookmark(addropList.get(position).getPromotion_id(), lat, lng).subscribeWith(getObserverRemoveBookmark());
                } else {
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                    holder.ivFav.setImageDrawable(myDrawable);
                    addropList.get(position).setIsBookmarked(true);
                    String lat = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LAT", "");
                    String lng = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LNG", "");
                    getObservableAddBookmark(addropList.get(position).getPromotion_id(), lat, lng).subscribeWith(getObserverAddBookmark());
                }
                }
        });

    }

    @Override
    public int getItemCount() {
        return addropList.size();
    }

    public class AdDropHolder extends RecyclerView.ViewHolder {

        TextView tvTitle,tvCategory,tvDescription;
        ImageView ivAdDrop, ivFav;

        public AdDropHolder(View v) {
            super(v);
            ivAdDrop = (ImageView) v.findViewById(R.id.ivAdDrop);
            ivFav = (ImageView) v.findViewById(R.id.ivFav);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvCategory = (TextView) v.findViewById(R.id.tvCategory);
            tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        }
    }

    public Observable<AdDropBookmarkResponse> getObservableAddBookmark(Integer promotion_id, String lat, String lng){
        return NetworkClient.getRetrofit(lat, lng).create(NetworkInterface.class)
                .createAdDropBookmark(promotion_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDropBookmarkResponse> getObserverAddBookmark(){
        return new DisposableObserver<AdDropBookmarkResponse>() {

            @Override
            public void onNext(@NonNull AdDropBookmarkResponse adDropBookmarkResponse) {
                Log.d(TAG,"Create Bookmark OnNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Create Bookmark onError"+ e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Create Bookmark onComplete");
            }
        };
    }

    public Observable<AdDropBookmarkResponse> getObservableRemoveBookmark(Integer promotion_id, String lat, String lng){
        return NetworkClient.getRetrofit(lat, lng).create(NetworkInterface.class)
                .removeAdDropBookmark(promotion_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDropBookmarkResponse> getObserverRemoveBookmark(){
        return new DisposableObserver<AdDropBookmarkResponse>() {

            @Override
            public void onNext(@NonNull AdDropBookmarkResponse adDropBookmarkResponse) {
                Log.d(TAG,"Remove Bookmark OnNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Remove Bookmark onError"+ e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Remove Bookmark onComplete");
            }
        };
    }
}
