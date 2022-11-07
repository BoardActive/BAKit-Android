package com.boardactive.addrop.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardactive.addrop.R;
import com.boardactive.bakitapp.BoardActive;
import com.bumptech.glide.Glide;

public class DisplayImageActivity extends AppCompatActivity {
    TextView textTitle;
    TextView textDesc;
    ImageView imageNotification;
    ImageView closeImage;
    String messageId;
    String notificationId;
    String firebaseNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        init();
    }

    public  void init(){
        textDesc = findViewById(R.id.txtDesc);
        textTitle = findViewById(R.id.txtTitle);
        imageNotification = findViewById(R.id.notificationImage);
        closeImage = findViewById(R.id.closeImage);
        String title="";
        String desc="";

        if(getIntent().getExtras() != null){
            title= getIntent().getStringExtra("key.TITLE");
            desc= getIntent().getStringExtra("key.DESC");
            String imageUrl= getIntent().getStringExtra("key.IMAGE_URL");
            messageId = getIntent().getStringExtra("MessageId");
            firebaseNotificationId = getIntent().getStringExtra("FirebaseMessageId").trim();
            notificationId = getIntent().getStringExtra("NotificationId");

            textTitle.setText(title);
            textDesc.setText(desc);
            if(imageUrl != null){
                Glide.with(this).load(Uri.parse(imageUrl)).into(imageNotification);

            }


        }
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        BoardActive mBoardActive = new BoardActive(getApplicationContext());

        mBoardActive.postEvent(new BoardActive.PostEventCallback() {

            @Override
            public void onResponse(Object value) {
                Log.d("TAG", "[BAKitApp] Received Event: " + value.toString());
            }
        }, "opened",messageId, notificationId, firebaseNotificationId);
    }
}