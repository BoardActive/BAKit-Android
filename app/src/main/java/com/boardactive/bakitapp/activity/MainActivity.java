package com.boardactive.bakitapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.boardactive.bakit.CheckPermissions;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private static final int NOTIFICATION_PERMISSION_CODE = 123;

    private View parent_view;

    //Add the BoardActive Object
    private BoardActive mBoardActive;
    private Button btn_userAttributes,
            btn_customAttributes,
            btn_getMe,
            btn_postEvent,
            btn_postLocation,
            btn_messages;
    private EditText httpReponse;
    private ToggleButton btnService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG, "[BAKitApp] onCreate()");

        httpReponse = (EditText) findViewById(R.id.httpResponse);

//        requestNotificationPermission();
        btn_userAttributes();
        btn_messages();
        btn_customAttributes();
        btn_postEvent();
        btn_postLocation();
        btn_getMe();
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
                if(isForeground){
                    mBoardActive.initialize(isForeground);
                }else{
                    mBoardActive.initialize(isForeground);
                }
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

    public void init() {
        // Check for Location Permissions
        CheckPermissions.checkForLocationPermissions(this);

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
        mBoardActive.setAppKey("ef748553-e55a-4cb4-b339-7813e395a5b1");

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
                        mBoardActive.initialize(false);

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
                        Log.d(TAG, "[BAKitApp] getMe(): " +  value.toString());
                        alertDialog(value);
                        onResume();
                    }
                });

            }

        });

    }

    public void btn_postEvent() {

        btn_postEvent = (Button) findViewById(R.id.btn_postEvent);

        btn_postEvent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                mBoardActive.postEvent(new BoardActive.PostEventCallback() {
//                    @Override
//                    public void onResponse(Object value) {
//                        Log.d(TAG, "[BAKit] LocationUpdatesIntentService onResponse" + value.toString());
//                    }
//                }, "received", "", "");
            }

        });

    }

    public void btn_postLocation() {

        btn_postLocation = (Button) findViewById(R.id.btn_postLocation);

        btn_postLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss Z (zzzz)");
                String date = df.format(Calendar.getInstance().getTime());

                mBoardActive.postLocation(new BoardActive.PostLocationCallback() {
                    @Override
                    public void onResponse(Object value) {
                        Log.d(TAG, "[BAKit] LocationUpdatesIntentService onResponse" + value.toString());
                    }
                }, 33.893402, -84.474600, date);
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

//    private void requestNotificationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
//            return;
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
//
//        }
//
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, NOTIFICATION_PERMISSION_CODE );
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        //Checking the request code of our request
//        if (requestCode == NOTIFICATION_PERMISSION_CODE ) {
//
//            //If permission is granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
//            } else {
//                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
