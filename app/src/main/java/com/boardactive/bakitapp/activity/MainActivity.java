package com.boardactive.bakitapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.boardactive.bakit.CheckPermissions;
import com.boardactive.bakit.GeneratedDialog;
import com.boardactive.bakit.Tools.SharedPreferenceHelper;
import com.boardactive.bakit.customViews.CustomAttributesActivity;
import com.boardactive.bakit.models.Me;
import com.boardactive.bakitapp.R;
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

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private View parent_view;
    private static final String IS_FOREGROUND = "isforeground";
    //Add the BoardActive Object
    private BoardActive mBoardActive;
    private Button btn_userAttributes,
            btn_customAttributes,
            btn_getMe,
            btn_messages;
    private EditText httpReponse;
    private ToggleButton btnService;
    public final static String BAKIT_URL = "BAKIT_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "[BAKitApp] onCreate()");

        httpReponse = (EditText) findViewById(R.id.httpResponse);

        btn_userAttributes();
        btn_messages();
        btn_customAttributes();
        btn_service();
        init();

    }
    ProgressDialog progressDialog;
    private void btn_service() {
        btnService = findViewById(R.id.btn_service);

        btnService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isForeground) {
                initHandler();
                progressDialog = new ProgressDialog(MainActivity.this,R.style.progressDialogTheme);
                progressDialog.setCancelable(false);
                progressDialog.show();
                mBoardActive.checkLocationPermissions();
                mBoardActive.setIsForeground(isForeground);
                mBoardActive.initialize();
            }
        });
    }

    private void initHandler() {
        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            public void run() {
                handlerCallback();
            }
        }, 5000);
    }

    private void handlerCallback() {
        if(progressDialog !=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showPermissionDialog(){
        GeneratedDialog.with(this)
                .setTitle(getResources().getString(com.boardactive.bakit.R.string.location_permission_title))
                .setDescription(getResources().getString(com.boardactive.bakit.R.string.location_permission_description))
                .setPositiveButtonText(getResources().getString(com.boardactive.bakit.R.string.ok))
                .setNegativeButtonText(getResources().getString(com.boardactive.bakit.R.string.CANCEL))
                .setPositiveListener(new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                        CheckPermissions.checkForLocationPermissions(MainActivity.this);
                    }
                })
                .setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        di.dismiss();
                    }
                })
                .showPermissionDialog();
    }

    public void init() {
        // Check for Location Permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showPermissionDialog();
            }
        }

        //CheckPermissions.checkForLocationPermissions(this);

        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
//        mBoardActive.setAppUrl(BoardActive.APP_URL_PROD); // Production
        mBoardActive.setAppUrl(BoardActive.APP_URL_DEV); // Development

        // Add AppID provided by BoardActive
//        mBoardActive.setAppId("ADD_APP_ID");
        mBoardActive.setAppId("164");

        // Add AppKey provided by BoardActive
//        mBoardActive.setAppKey("ADD_APP_KEY");
        mBoardActive.setAppKey("ef748553-e55a-4cb4-b339-7813e395a5b1");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

        // Optional, set to 'true' to run in foreground
        mBoardActive.setIsForeground(false);

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

                        mBoardActive.setIsForeground(false);

                        // Check for Location permissions
                     //   mBoardActive.checkLocationPermissions();

                        // Initialize BoardActive
                        mBoardActive.initialize();

                        // Register the device with BoardActive
                        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {
                                Log.d(TAG, value.toString());
                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                Me me = gson.fromJson(value.toString(), Me.class);

                                JsonParser parser = new JsonParser();
                                JsonElement je = parser.parse(value.toString());
                                httpReponse.setText(gson.toJson(je));
                                Log.d(TAG, gson.toJson(je));
                            }
                        });
                    }
                });
    }


    public void btn_messages() {

        btn_messages = (Button) findViewById(R.id.btn_messages);

        btn_messages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getBaseContext(), MessagesActivity.class);
                startActivity(intent);
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
//                Intent intent = new Intent(getBaseContext(), CustomActivity.class);
                Intent intent = new Intent(getBaseContext(), CustomAttributesActivity.class);
                intent.putExtra("baseUrl",BoardActive.APP_URL_DEV);
                startActivity(intent);
            }

        });

    }
}
