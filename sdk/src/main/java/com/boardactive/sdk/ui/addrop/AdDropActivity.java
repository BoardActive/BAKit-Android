package com.boardactive.sdk.ui.addrop;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boardactive.sdk.R;
import com.boardactive.sdk.models.AdDrop;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdDropActivity extends AppCompatActivity implements AdDropViewInterface {

    Integer mAdDrop_id;
    ProgressBar progressBar;
    Toolbar toolbar;
    private String TAG = "AdDropActivity";
    AdDropPresenter addropPresenter;

    TextView tvTitle,tvCategory,tvDescription;
    ImageView ivAdDrop, ivFav;
    Button btnDirections, btnRedeem;


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
                mAdDrop_id = extras.getInt("ADDROP_ID");
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

            ivFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addrop.getIsBookmarked()){
                        Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart_outline);
                        ivFav.setImageDrawable(myDrawable);
                        addrop.setIsBookmarked(true);
                    } else {
                        Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                        ivFav.setImageDrawable(myDrawable);
                        addrop.setIsBookmarked(false);
                    }
                }
            });

            btnDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
}
