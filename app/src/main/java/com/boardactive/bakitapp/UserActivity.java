package com.boardactive.bakitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.boardactive.bakit.BoardActive;
import com.boardactive.bakit.models.Me;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class UserActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private static final int ME_API_RESPONSE_CODE = 0;

    private Button btn_save, btn_cancel;
    private BoardActive mBoardActive;
    private Me mMe;

    private AutoCompleteTextView name, email, phone, dateBorn, facebookUrl, linkedInUrl, twitterUrl, instagramUrl, avatarUrl;
    private AppCompatRadioButton radioFemale, radioMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioFemale = (AppCompatRadioButton) findViewById(R.id.radio_female);
        radioMale = (AppCompatRadioButton) findViewById(R.id.radio_male);

        name = (AutoCompleteTextView) findViewById(R.id.name);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        phone = (AutoCompleteTextView) findViewById(R.id.phone);
        dateBorn = (AutoCompleteTextView) findViewById(R.id.dateBorn);
        facebookUrl = (AutoCompleteTextView) findViewById(R.id.facebookUrl);
        linkedInUrl = (AutoCompleteTextView) findViewById(R.id.linkedInUrl);
        twitterUrl = (AutoCompleteTextView) findViewById(R.id.twitterUrl);
        instagramUrl = (AutoCompleteTextView) findViewById(R.id.instagramUrl);
        avatarUrl = (AutoCompleteTextView) findViewById(R.id.avatarUrl);

        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
        mBoardActive.setAppUrl(BoardActive.APP_URL_DEV);

        // Add AppID provided by BoardActive
        mBoardActive.setAppId("164");

        // Add AppKey provided by BoardActive
        mBoardActive.setAppKey("bb85c28a-0ac4-439d-ad9c-5527be3cafdd");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

//        mStockAttributes = new Stock();

        mBoardActive.getMe(new BoardActive.GetMeCallback() {
            @Override
            public void onResponse(Object value) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                mMe = gson.fromJson(value.toString(), Me.class);
                name.setText(mMe.getAttributes().getStock().getName());
                email.setText(mMe.getAttributes().getStock().getEmail());
                phone.setText(mMe.getAttributes().getStock().getPhone());
                dateBorn.setText(mMe.getAttributes().getStock().getDateBorn());
                facebookUrl.setText(mMe.getAttributes().getStock().getFacebookUrl());
                linkedInUrl.setText(mMe.getAttributes().getStock().getLinkedInUrl());
                twitterUrl.setText(mMe.getAttributes().getStock().getTwitterUrl());
                instagramUrl.setText(mMe.getAttributes().getStock().getInstagramUrl());
                avatarUrl.setText(mMe.getAttributes().getStock().getAvatarUrl());
                if(mMe.getAttributes().getStock().getGender() == "f"){
                    radioFemale.setChecked(true);
                    radioMale.setChecked(false);
                } else {
                    radioFemale.setChecked(false);
                    radioMale.setChecked(true);
                }

                onResume();
            }
        });

        btn_save();
        btn_cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void btn_cancel() {

        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void btn_save() {

        btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (name.getText().equals("")){
                    mMe.getAttributes().getStock().setName(null);
                } else {
                    mMe.getAttributes().getStock().setName(name.getText().toString());
                }
                mMe.getAttributes().getStock().setEmail(email.getText().toString());
                mMe.getAttributes().getStock().setPhone(phone.getText().toString());
                mMe.getAttributes().getStock().setDateBorn(dateBorn.getText().toString());
                mMe.getAttributes().getStock().setFacebookUrl(facebookUrl.getText().toString());
                mMe.getAttributes().getStock().setLinkedInUrl(linkedInUrl.getText().toString());
                mMe.getAttributes().getStock().setTwitterUrl(twitterUrl.getText().toString());
                mMe.getAttributes().getStock().setInstagramUrl(instagramUrl.getText().toString());
                mMe.getAttributes().getStock().setAvatarUrl(avatarUrl.getText().toString());
                if(radioFemale.isChecked()) {
                    mMe.getAttributes().getStock().setGender("f");
                } else {
                    mMe.getAttributes().getStock().setGender("m");
                }

                putMe();
            }

        });

    }

    void putMe() {
        Gson gson = new Gson();
        String requestJson = gson.toJson(mMe);
        System.out.println(requestJson);

        mBoardActive.putMe(new BoardActive.PutMeCallback() {
            @Override
            public void onResponse(Object value) {
                finish();
            }
        }, mMe);

    }

}
