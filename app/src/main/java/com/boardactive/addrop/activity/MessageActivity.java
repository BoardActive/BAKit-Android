package com.boardactive.addrop.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardactive.addrop.R;
import com.boardactive.addrop.firebase.MyFirebaseMessagingService;
import com.boardactive.addrop.model.Message;
import com.boardactive.addrop.model.MessageData;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.DAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.addrop.utils.Tools;
import com.boardactive.bakit.BoardActive;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.InputStream;

public class MessageActivity extends AppCompatActivity {

    private static final String MESSAGE_ID = "MESSAGE_ID";
    private static String TAG = "MessageActivity";

    CardView card_social, card_qrcode;
    private View search_bar;
    private String mTitle;

    private MessageEntity messageEntity;
    private MessageData messageData;
    private Gson gson;
    private Integer id;
    private DAO dao;

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
//        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_basic, menu);
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
        gson = new Gson();
        Bundle extras = intent.getExtras();
        MessageEntity messageEntity = null;

        if (extras != null) {
            id = (Integer) extras.get(MyFirebaseMessagingService.EXTRA_MESSADE_ID);
            dao = AppDatabase.getDb(this).getDAO();
            messageEntity = dao.getMessage(id);
            messageData = gson.fromJson(messageEntity.getMessageData(), MessageData.class);
        }

        mTitle = messageEntity.getTitle();
        BoardActive mBoardActive = new BoardActive(getApplicationContext());
        mBoardActive.postEvent(new BoardActive.PostEventCallback() {
            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "Received Event: " + value.toString());
            }
        }, "Opened", messageEntity.getMessageId(), messageEntity.getNotificationId());


        TextView titleTextView = (TextView) findViewById(R.id.textView1);
        titleTextView.setText(messageEntity.getTitle());

        TextView bodyTextView = (TextView) findViewById(R.id.textView2);
        bodyTextView.setText(messageEntity.getBody());

        // Main ImageView
        if (messageEntity.getImageUrl() == null) {
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_image)), "http://bit.ly/2Y6G5q0");
        } else {
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_image)), messageEntity.getImageUrl());
        }

        // Company Links Card
        CardView card_company = (CardView) findViewById(R.id.card_company);
        card_company.setVisibility(View.GONE);

        if (messageData.getUrlLandingPage() != null) {
            card_company.setVisibility(View.VISIBLE);
            AppCompatImageView iv_company_landingpage = (AppCompatImageView) findViewById(R.id.iv_company_landingpage);
            iv_company_landingpage.setVisibility(View.VISIBLE);
            iv_company_landingpage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(messageData.getUrlLandingPage());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        if (messageData.getEmail() != null) {
            card_company.setVisibility(View.VISIBLE);
            AppCompatImageView iv_company_email = (AppCompatImageView) findViewById(R.id.iv_company_email);
            iv_company_email.setVisibility(View.VISIBLE);
            iv_company_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String subject = "Regarding the BoardActive Demo App";
                    final String mailTo = "mailto";
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            mailTo, messageData.getEmail(), null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
            });
        }

        if (messageData.getStoreAddress() != null) {
            card_company.setVisibility(View.VISIBLE);
            AppCompatImageView iv_company_address = (AppCompatImageView) findViewById(R.id.iv_company_address);
            iv_company_address.setVisibility(View.VISIBLE);
            iv_company_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String mailTo = "https://";
                    Uri uri = Uri.parse(messageData.getStoreAddress());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        if (messageData.getPhoneNumber() != null) {
            card_company.setVisibility(View.VISIBLE);
            AppCompatImageView iv_company_phone = (AppCompatImageView) findViewById(R.id.iv_company_phone);
            iv_company_phone.setVisibility(View.VISIBLE);
            iv_company_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + messageData.getPhoneNumber());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        // Social Links Card
        CardView card_social = (CardView) findViewById(R.id.card_social);
        card_social.setVisibility(View.GONE);

        if (messageData.getUrlFacebook() != null) {
            card_social.setVisibility(View.VISIBLE);
            AppCompatImageView iv_social_facebook = (AppCompatImageView) findViewById(R.id.iv_social_facebook);
            iv_social_facebook.setVisibility(View.VISIBLE);
            iv_social_facebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(messageData.getUrlFacebook());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        if (messageData.getUrlTwitter() != null) {
            card_social.setVisibility(View.VISIBLE);
            AppCompatImageView iv_social_twitter = (AppCompatImageView) findViewById(R.id.iv_social_twitter);
            iv_social_twitter.setVisibility(View.VISIBLE);
            iv_social_twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(messageData.getUrlTwitter());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        if (messageData.getUrlLinkedIn() != null) {
            card_social.setVisibility(View.VISIBLE);
            AppCompatImageView iv_social_linkedin = (AppCompatImageView) findViewById(R.id.iv_social_linkedin);
            iv_social_linkedin.setVisibility(View.VISIBLE);
            iv_social_linkedin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(messageData.getUrlLinkedIn());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        if (messageData.getUrlYoutube() != null) {
            card_social.setVisibility(View.VISIBLE);
            AppCompatImageView iv_social_youtube = (AppCompatImageView) findViewById(R.id.iv_social_youtube);
            iv_social_youtube.setVisibility(View.VISIBLE);
            iv_social_youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(messageData.getUrlYoutube());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        // QRCode
        CardView card_qrcode = (CardView) findViewById(R.id.card_qrcode);
        card_qrcode.setVisibility(View.GONE);
        if (messageData.getUrlQRCode() != null) {
            card_qrcode.setVisibility(View.VISIBLE);
            Tools.displayImageOriginal(this, ((ImageView) findViewById(R.id.iv_qrcode)), messageData.getUrlQRCode());
        }

    }

}
