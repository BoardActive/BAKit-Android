package com.boardactive.sdk.ui.addrop;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boardactive.sdk.R;
import com.boardactive.sdk.models.AdDrop;
import com.boardactive.sdk.models.AdDropBookmarkResponse;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.boardactive.sdk.ui.AdDropMainActivity;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropActivity extends AppCompatActivity implements AdDropViewInterface {

    Integer mAdDrop_id;
    ProgressBar progressBar;
    Toolbar toolbar;
    private String TAG = "AdDropActivity";
    AdDropPresenter addropPresenter;

    TextView tvTitle,tvCategory,tvDescription;
    ImageView ivAdDrop, ivFav;
    Button btnDirections, btnRedeem;
    Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_drop);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ivAdDrop = (ImageView) findViewById(R.id.ivAdDrop);
        ivFav = (ImageView) findViewById(R.id.ivFav);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        btnDirections = (Button) findViewById(R.id.btnDirections);
        btnRedeem = (Button) findViewById(R.id.btnRedeem);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                //Nathan: Get additional data from FCM notification, for some reason it comes in as
                //a string instead of an int...
                try {
                    String temp = extras.getString("promotion_id");

                    if (temp == null){
                        Integer temp2 = extras.getInt("promotion_id");
                        mAdDrop_id = temp2;
                    }
                    else {
                        mAdDrop_id = Integer.parseInt(temp);
                    }
                }catch (Exception e) {
                    Log.w(TAG,"ERROR getting AdDrop ID from Intent/FCM Notification: " + e.getMessage());
                    //Lets spit out main list view instead of loading null promo
                    Intent myIntent = new Intent(AdDropActivity.this, AdDropMainActivity.class);
                    AdDropActivity.this.startActivity(myIntent);
                }


            }
        }

        setupMVP();
        setupViews();
        getAdDropItem();
    }

    private void setupMVP() {
        addropPresenter = new AdDropPresenter(this);
    }

    private void setupViews(){
        setSupportActionBar(toolbar);
    }

    private void getAdDropItem() {
        addropPresenter.getAdDrop(mAdDrop_id, getBaseContext());
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(AdDropActivity.this,str,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //You can handle some notification data stuff here if desired
//        if(intent.getStringExtra("promotion_id").equals("845")){
//            Log.d(TAG,"AdDrop response was the correct promo id");
//        }

    }

    @Override
    public void displayAdDrop(final AdDrop addrop) {
        if(addrop!=null) {
            tvTitle.setText(addrop.getTitle());
            tvCategory.setText(addrop.getCategory());
            tvDescription.setText(addrop.getDescription());

            Picasso.Builder builder = new Picasso.Builder(getBaseContext());
            builder.downloader(new OkHttp3Downloader(getBaseContext()));
            builder.build().load(addrop.getImage_url())
                    .placeholder((R.drawable.ic_launcher_background))
                    .error(R.drawable.ic_launcher_background)
                    .into(ivAdDrop);

            if (!addrop.getIsBookmarked()){
                Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart_outline);
                ivFav.setImageDrawable(myDrawable);
            } else {
                Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                ivFav.setImageDrawable(myDrawable);
            }
            //If array is out of bounds below, unrender the Directions button
            try {
                String location = addrop.getLocations().get(0).getLongitude();
            } catch (Exception e) {
                btnDirections.setVisibility(View.GONE);
                Log.w(TAG,"ERROR getting setting btn to invis: " + e.getMessage());
            }

            ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addrop.getIsBookmarked()){
                        Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart_outline);
                        ivFav.setImageDrawable(myDrawable);
                        addrop.setIsBookmarked(false);
                        String lat = PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString("LAT", "");
                        String lng = PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString("LNG", "");
                        getObservableRemoveBookmark(addrop.getPromotion_id(), lat, lng).subscribeWith(getObserverRemoveBookmark());
                    } else {
                        Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                        ivFav.setImageDrawable(myDrawable);
                        addrop.setIsBookmarked(true);
                        String lat = PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString("LAT", "");
                        String lng = PreferenceManager.getDefaultSharedPreferences(mContext)
                                .getString("LNG", "");


                        JSONObject obj = null;
                        String json = "{ name: received," +
                                "params:{" +
                                "promotion_id:565, " +
                                "advertisement_id:883, " +
                                "firebaseNotificationId:1536259012270989" +
                                "}" +
                                "}";


                        try {

                             obj = new JSONObject(json);

                            Log.d("My App", obj.toString());

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
                        }

                        getObservableSetEvent(obj.toString(), addrop.getAdvertisement_id(), addrop.getPromotion_id(), lat, lng).subscribeWith(getObserverAddBookmark());
                        getObservableAddBookmark(addrop.getPromotion_id(), lat, lng).subscribeWith(getObserverSetEvent());


                    }
                }
            });

            btnDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String addr1 = addrop.getLocations().get(0).getAddress_one();
                    String addr2 = addrop.getLocations().get(0).getAddress_two();
                    String city = addrop.getLocations().get(0).getCity();
                    String zip = addrop.getLocations().get(0).getZip_code();

                    Uri uri = Uri.parse("google.navigation:q="+addr1+","+addr2+","+city+","+zip);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            });

            if (addrop.getQrUrl()!=null){
                btnRedeem.setVisibility(View.VISIBLE);
            } else {
                btnRedeem.setVisibility(View.INVISIBLE);
            }

            btnRedeem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadRedeem(addrop.getQrUrl());
                }
            });

        }else{
            Log.d(TAG,"AdDrop response null");
        }

    }

    @Override
    public void displayError(String e) {

        showToast(e);

    }

    //Added in Part 2 of the series
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.close){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadRedeem(String image_url) {

        CropSquareTransformation transform = new CropSquareTransformation();

        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.qr_url,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView imageView = (ImageView) layout.findViewById(R.id.qr_url);

        Picasso.Builder builder = new Picasso.Builder(getBaseContext());
        builder.downloader(new OkHttp3Downloader(getBaseContext()));
        builder.build().load(image_url)
                .placeholder((R.drawable.ic_launcher_background))
                .error(R.drawable.ic_launcher_background)
                .resize(250, 250)
                .centerCrop()
                .transform(transform)
                .into(imageView);

//        image.setImageDrawable(tempImageView.getDrawable());

        imageDialog.setView(layout);

        //        imageDialog.setPositiveButton(resources.getString(R.string.ok_button), new DialogInterface.OnClickListener(){
//
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//
//        });

        imageDialog.create();
        imageDialog.show();
    }
    public Observable<AdDropBookmarkResponse> getObservableAddBookmark(Integer promotion_id, String lat, String lng){
        return NetworkClient.getRetrofit(lat, lng).create(NetworkInterface.class)
                .createAdDropBookmark(promotion_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<AdDropBookmarkResponse> getObservableSetEvent(String eventName, Integer promotion_id, Integer advertiser_id, String lat, String lng){
        return NetworkClient.getRetrofit(lat, lng).create(NetworkInterface.class)
                .setEvent(eventName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public DisposableObserver<AdDropBookmarkResponse> getObserverSetEvent(){
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
                Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                ivFav.setImageDrawable(myDrawable);
                //refresh();
            }
        };
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
                Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                ivFav.setImageDrawable(myDrawable);
                //refresh();
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
                Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart_outline);
                ivFav.setImageDrawable(myDrawable);
                //refresh();
            }
        };
    }
    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
