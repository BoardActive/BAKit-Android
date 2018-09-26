package com.boardactive.sdk.adapters;

import android.app.Activity;
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
import android.widget.Toast;

import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.boardactive.sdk.ui.AdDropMainActivity;
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
    AdDropHolder mHolder;
    Boolean onFavoritesPage;
    Integer mPosition;

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
        mPosition = position;

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
         onFavoritesPage = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("favorites", false);
        if (onFavoritesPage == true) {
            //Set all the little hearts to true cause otherwise it don't wanna work
            //I think this dev bug is caused by the position id of the addrops in fav view..
            //..possibly being incorrect.
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
            holder.ivFav.setImageDrawable(myDrawable);

            // This listener is for Favorites List view only, it handles bookmark add/remove
            holder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // mHolder=holder;
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                    holder.ivFav.setImageDrawable(myDrawable);
                    addropList.get(position).setIsBookmarked(false);
                    String lat = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LAT", "");
                    String lng = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LNG", "");
                    getObservableRemoveBookmark(addropList.get(position).getPromotion_id(), lat, lng).subscribeWith(getObserverRemoveBookmark());


                }
            });
            // If not favorites view, handle bookmark img setting as normal
        } else {
            if (bookmark) {
                Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
                holder.ivFav.setImageDrawable(myDrawable);
            } else {
                Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                holder.ivFav.setImageDrawable(myDrawable);
            }
        }
        // This listener is for Main List view only, it handles bookmark add/remove
        if (onFavoritesPage == false) {
            holder.ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mHolder = holder;
                    if (bookmark) {
                        Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                        holder.ivFav.setImageDrawable(myDrawable);
                        addropList.get(position).setIsBookmarked(false);
                        String lat = PreferenceManager.getDefaultSharedPreferences(context)
                                .getString("LAT", "");
                        String lng = PreferenceManager.getDefaultSharedPreferences(context)
                                .getString("LNG", "");
                        getObservableRemoveBookmark(addropList.get(position).getPromotion_id(), lat, lng).subscribeWith(getObserverRemoveBookmark());
                    } else {
                        Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
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
                //final Activity activity = (Activity) context;
                //activity.recreate();
                Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
                mHolder.ivFav.setImageDrawable(myDrawable);
                if (onFavoritesPage == false) {
                    mHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart);
                    mHolder.ivFav.setImageDrawable(myDrawable);
                    addropList.get(mPosition).setIsBookmarked(true);
                    String lat = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LAT", "");
                    String lng = PreferenceManager.getDefaultSharedPreferences(context)
                            .getString("LNG", "");
                    getObservableAddBookmark(addropList.get(mPosition).getPromotion_id(), lat, lng).subscribeWith(getObserverAddBookmark());
                }
            });
        }

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
//                final Activity activity = (Activity) context;
//                activity.recreate();
                if (onFavoritesPage == false) {
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                    mHolder.ivFav.setImageDrawable(myDrawable);
                    mHolder.ivFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                Drawable myDrawable = context.getResources().getDrawable(R.drawable.ic_heart_outline);
                                mHolder.ivFav.setImageDrawable(myDrawable);
                                addropList.get(mPosition).setIsBookmarked(false);
                                String lat = PreferenceManager.getDefaultSharedPreferences(context)
                                        .getString("LAT", "");
                                String lng = PreferenceManager.getDefaultSharedPreferences(context)
                                        .getString("LNG", "");
                                getObservableRemoveBookmark(addropList.get(mPosition).getPromotion_id(), lat, lng).subscribeWith(getObserverRemoveBookmark());

                        }
                    });
                }
            }
        };
    }
}
