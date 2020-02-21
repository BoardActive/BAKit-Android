package com.boardactive.bakitapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.boardactive.bakit.models.Me;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.boardactive.bakit.BoardActive;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private View parent_view;

    //Add the BoardActive Object
    private BoardActive mBoardActive;
    private Button btn_userAttributes, btn_customAttributes, btn_getMe;
    private EditText httpReponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "[BAKitApp] onCreate()");

        httpReponse = (EditText) findViewById(R.id.httpResponse);

        btn_userAttributes();
        btn_customAttributes();
        btn_getMe();
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    public void init() {
        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
//        mBoardActive.setAppUrl(BoardActive.APP_URL_PROD);
        mBoardActive.setAppUrl(BoardActive.APP_URL_DEV);

        // Add AppID provided by BoardActive
//        mBoardActive.setAppId("ADD_APP_ID");
        mBoardActive.setAppId("164");

        // Add AppKey provided by BoardActive
//        mBoardActive.setAppKey("ADD_APP_KEY");
        mBoardActive.setAppKey("bb85c28a-0ac4-439d-ad9c-5527be3cafdd");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

        // Get Firebase Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "[BAKitApp] getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        Log.d(TAG, "[BAKitApp] fcmToken: " + fcmToken);


                        // Add Firebase Token to BoardActive
                        mBoardActive.setAppToken(fcmToken);

                        // Initialize BoardActive
                        mBoardActive.initialize();

                        // Register the device with BoardActive
                        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {

                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                Me me = gson.fromJson(value.toString(), Me.class);

                                JsonParser parser = new JsonParser();
                                JsonElement je = parser.parse(value.toString());
                                httpReponse.setText(gson.toJson(je));
                                onResume();
                            }
                        });
                    }
                });

    }


    public void btn_userAttributes() {

        btn_userAttributes = (Button) findViewById(R.id.btn_userAttributes);

        btn_userAttributes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), UserActivity.class);
                startActivity(intent);
            }

        });

    }

    public void btn_customAttributes() {

        btn_customAttributes = (Button) findViewById(R.id.btn_customAttributes);

        btn_customAttributes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), CustomActivity.class);
                startActivity(intent);
            }

        });

    }

    public void btn_getMe() {

        btn_getMe = (Button) findViewById(R.id.btn_getMe);

        btn_getMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mBoardActive.getMe(new BoardActive.GetMeCallback() {
                    @Override
                    public void onResponse(Object value) {
                        Log.d("[BAKitApp] getMe(): ", value.toString());
                        alertDialog(value);
                        onResume();
                    }
                });

            }

        });

    }

    private void alertDialog(Object value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(value.toString());

//        builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
////                Snackbar.make(parent_view, "Discard clicked", Snackbar.LENGTH_SHORT).show();
//            }
//        });
        builder.setNegativeButton("CANCEL", null);
        builder.show();
    }
}
