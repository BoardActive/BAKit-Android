package com.boardactive.bakitapp;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.boardactive.bakit.BoardActive;
import com.boardactive.bakit.models.Me;
import com.boardactive.bakitapp.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

public class UserActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();
    private static final int ME_API_RESPONSE_CODE = 0;

    private Button btn_save, btn_cancel;
    private BoardActive mBoardActive;
    private Me mMe;

    private AutoCompleteTextView name, email, phone, facebookUrl, linkedInUrl, twitterUrl, instagramUrl, avatarUrl;
//    private TextView dateBorn;
    private Calendar calendar;
    private int year, month, day;
    private AppCompatRadioButton radioFemale, radioMale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        radioFemale = (AppCompatRadioButton) findViewById(R.id.radio_female);
        radioMale = (AppCompatRadioButton) findViewById(R.id.radio_male);

        name = (AutoCompleteTextView) findViewById(R.id.name);
        email = (AutoCompleteTextView) findViewById(R.id.email);
        phone = (AutoCompleteTextView) findViewById(R.id.phone);
        facebookUrl = (AutoCompleteTextView) findViewById(R.id.facebookUrl);
        linkedInUrl = (AutoCompleteTextView) findViewById(R.id.linkedInUrl);
        twitterUrl = (AutoCompleteTextView) findViewById(R.id.twitterUrl);
        instagramUrl = (AutoCompleteTextView) findViewById(R.id.instagramUrl);
        avatarUrl = (AutoCompleteTextView) findViewById(R.id.avatarUrl);
//        dateBorn = (TextView) findViewById(R.id.avatarUrl);

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
                facebookUrl.setText(mMe.getAttributes().getStock().getFacebookUrl());
                linkedInUrl.setText(mMe.getAttributes().getStock().getLinkedInUrl());
                twitterUrl.setText(mMe.getAttributes().getStock().getTwitterUrl());
                instagramUrl.setText(mMe.getAttributes().getStock().getInstagramUrl());
                avatarUrl.setText(mMe.getAttributes().getStock().getAvatarUrl());
                String d = mMe.getAttributes().getStock().getDateBorn().toString();
                ((TextView) findViewById(R.id.dateBorn)).setText(d);

//                ((TextView) findViewById(R.id.dateBorn)).setText(mMe.getAttributes().getStock().getDateBorn());

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
        initComponent();


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
                String d = ((TextView) findViewById(R.id.dateBorn)).getText().toString();
                mMe.getAttributes().getStock().setDateBorn(d);
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

    private void initComponent() {
        ((Button) findViewById(R.id.bt_pick)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePickerLight((Button) view);
            }
        });
    }

    private void dialogDatePickerLight(final Button bt) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        ((TextView) findViewById(R.id.dateBorn)).setText(Tools.getFormattedDateSimple(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

}
