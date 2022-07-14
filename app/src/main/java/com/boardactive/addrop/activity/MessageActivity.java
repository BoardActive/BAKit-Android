package com.boardactive.addrop.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.boardactive.addrop.firebase.FCMService;
import com.boardactive.addrop.R;

import com.boardactive.bakitapp.BoardActive;
import com.boardactive.addrop.models.MessageData;
import com.boardactive.addrop.models.OfferList;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.MessageDAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.addrop.utils.Tools;
import com.google.gson.Gson;

public class MessageActivity extends AppCompatActivity {

    public static final String TAG = MessageActivity.class.getName();

    private MessageEntity mMessageEntity;
    private MessageData mMessageData;
    private OfferList mOfferList;
    private Gson gson;
    private Integer id;
    private MessageDAO mMessageDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        onNewIntent(getIntent());
        initToolbar();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

        } else if (item.getItemId() == R.id.action_close) {
            finish();
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        gson = new Gson();
        Bundle extras = intent.getExtras();
        mMessageDAO = AppDatabase.getDb(this).getMessageDAO();

        if (extras != null) {
            id = (Integer) extras.get(FCMService.EXTRA_MESSADE_ID);
        }

        mMessageEntity = mMessageDAO.getMessage(id);

        String messageData = mMessageEntity.getMessageData();

        mMessageData = gson.fromJson(messageData, MessageData.class);

        if (!mMessageEntity.getIsRead()){
            BoardActive mBoardActive = new BoardActive(getApplicationContext());
            mBoardActive.postEvent(new BoardActive.PostEventCallback() {
                @Override
                public void onResponse(Object value) {
                    Log.d(TAG, "Received Event: " + value.toString());
                }
            }, "opened", mMessageEntity.getBaMessageId(), mMessageEntity.getBaNotificationId(), mMessageEntity.getFirebaseNotificationId());
            mMessageEntity.setIsRead(true);;
            mMessageDAO.insertMessage(mMessageEntity);
        }

        TextView titleTextView = (TextView) findViewById(R.id.textView1);
        titleTextView.setText(mMessageEntity.getTitle());

        TextView bodyTextView = (TextView) findViewById(R.id.textView2);
        bodyTextView.setText(mMessageEntity.getBody());

        // Main ImageView
        if (mMessageEntity.getImageUrl() == null) {
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_image)), "http://bit.ly/2Y6G5q0");
        } else {
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_image)), mMessageEntity.getImageUrl());
        }

        // Company Links Card
        CardView card_company = (CardView) findViewById(R.id.card_company);
        card_company.setVisibility(View.GONE);

        try {
            if (mMessageData.getUrlLandingPage() != null) {
                card_company.setVisibility(View.VISIBLE);
                AppCompatImageView iv_company_landingpage = (AppCompatImageView) findViewById(R.id.iv_company_landingpage);
                iv_company_landingpage.setVisibility(View.VISIBLE);
                iv_company_landingpage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mMessageData.getUrlLandingPage());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {

        }

        try {
            if (mMessageData.getEmail() != null) {
                card_company.setVisibility(View.VISIBLE);
                AppCompatImageView iv_company_email = (AppCompatImageView) findViewById(R.id.iv_company_email);
                iv_company_email.setVisibility(View.VISIBLE);
                iv_company_email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String subject = "Regarding the BoardActive Demo App";
                        final String mailTo = "mailto";
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                mailTo, mMessageData.getEmail(), null));
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                    }
                });
            }

        } catch (Exception e){

        }

        try {
            if (mMessageData.getStoreAddress() != null) {
                card_company.setVisibility(View.VISIBLE);
                AppCompatImageView iv_company_address = (AppCompatImageView) findViewById(R.id.iv_company_address);
                iv_company_address.setVisibility(View.VISIBLE);
                iv_company_address.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String map = "https://maps.google.com/?q=";
                        Uri uri = Uri.parse(map + mMessageData.getStoreAddress());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        try {
            if (mMessageData.getPhoneNumber() != null) {
                card_company.setVisibility(View.VISIBLE);
                AppCompatImageView iv_company_phone = (AppCompatImageView) findViewById(R.id.iv_company_phone);
                iv_company_phone.setVisibility(View.VISIBLE);
                iv_company_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("tel:" + mMessageData.getPhoneNumber());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        // Social Links Card
        CardView card_social = (CardView) findViewById(R.id.card_social);
        card_social.setVisibility(View.GONE);

        try {
            if (mMessageData.getUrlFacebook() != null) {
                card_social.setVisibility(View.VISIBLE);
                AppCompatImageView iv_social_facebook = (AppCompatImageView) findViewById(R.id.iv_social_facebook);
                iv_social_facebook.setVisibility(View.VISIBLE);
                iv_social_facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mMessageData.getUrlFacebook());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        try {
            if (mMessageData.getUrlTwitter() != null) {
                card_social.setVisibility(View.VISIBLE);
                AppCompatImageView iv_social_twitter = (AppCompatImageView) findViewById(R.id.iv_social_twitter);
                iv_social_twitter.setVisibility(View.VISIBLE);
                iv_social_twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mMessageData.getUrlTwitter());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        try {
            if (mMessageData.getUrlLinkedIn() != null) {
                card_social.setVisibility(View.VISIBLE);
                AppCompatImageView iv_social_linkedin = (AppCompatImageView) findViewById(R.id.iv_social_linkedin);
                iv_social_linkedin.setVisibility(View.VISIBLE);
                iv_social_linkedin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mMessageData.getUrlLinkedIn());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        try {
            if (mMessageData.getUrlYoutube() != null) {
                card_social.setVisibility(View.VISIBLE);
                AppCompatImageView iv_social_youtube = (AppCompatImageView) findViewById(R.id.iv_social_youtube);
                iv_social_youtube.setVisibility(View.VISIBLE);
                iv_social_youtube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(mMessageData.getUrlYoutube());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e){

        }

        // QRCode
        CardView card_qrcode = (CardView) findViewById(R.id.card_qrcode);
        card_qrcode.setVisibility(View.GONE);
        try {
            if (mMessageData.getUrlQRCode() != null) {
                card_qrcode.setVisibility(View.VISIBLE);
                Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_qrcode)), mMessageData.getUrlQRCode());
            }

        } catch (Exception e){

        }

        // offer_list
        CardView card_offer_list = (CardView) findViewById(R.id.card_offer_list);
//        card_offer_list.setVisibility(View.GONE);
        try {
            if (mMessageData.getOffer_list() != null) {
                mOfferList = gson.fromJson(mMessageData.getOffer_list(), OfferList.class);
                card_offer_list.setVisibility(View.VISIBLE);
                TextView txt_offer_title = (TextView) findViewById(R.id.txt_offer_title);
                txt_offer_title.setText(mOfferList.toString());
            }

        } catch (Exception e){

        }

    }

    public static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

}
