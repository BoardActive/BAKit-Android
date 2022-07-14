package com.boardactive.addrop.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.boardactive.bakitapp.customViews.CustomAttributesActivity;
import com.boardactive.addrop.R;
import com.boardactive.addrop.utils.GeofenceBroadCastReceiver;
import com.boardactive.addrop.utils.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.boardactive.bakitapp.BoardActive;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity  {

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
    private boolean mBounded;
    private LocationService locationService;
    final static int REQUEST_CODE = 1;
    private PendingIntent geofencePendingIntent;
    BroadcastReceiver br = new GeofenceBroadCastReceiver();



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
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("MYTAG", "This is your Firebase token" + token);

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
          ////      mBoardActive.checkLocationPermissions();
               // mBoardActive.setIsForeground(isForeground);
               // mBoardActive.initialize();
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

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, LocationService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);

    }
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            locationService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder) service;
            MainActivity.this.locationService = mLocalBinder.getService();
        }
    };
    public void init() {
        // Create an instant of BoardActive
        mBoardActive = new BoardActive(MainActivity.this);

        // Add URL to point to BoardActive REST API
//        mBoardActive.setAppUrl(BoardActive.APP_URL_PROD); // Production
        mBoardActive.setAppUrl(BoardActive.APP_URL_DEV); // Development

        // Add AppID provided by BoardActive
       // mBoardActive.setAppId("ADD_APP_ID");

        mBoardActive.setAppId("164");

        // Add AppKey provided by BoardActive
//        mBoardActive.setAppKey("ADD_APP_KEY");
       // mBoardActive.setAppKey("ef748553-e55a-4cb4-b339-7813e395a5b1");
        mBoardActive.setAppKey("88fd530b-c111-4077-a1d3-ad0a24b127fd");
        //mBoardActive.setAppKey("d17f0feb-4f96-4c2a-83fd-fd6302ae3a16");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

        // Optional, set to 'true' to run in foreground
        mBoardActive.setIsForeground(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
            Log.e(TAG, "onCreate: permission denied");
            mBoardActive.isPermissionGranted = false;

        } else {
            Log.e(TAG, "onCreate: permission granded");
            mBoardActive.isPermissionGranted = true;
            mBoardActive.getLocationList();

        }


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
                        mBoardActive.checkLocationPermissions();

                        // Initialize BoardActive
                        mBoardActive.initialize();

                        // Register the device with BoardActive
                        if(mBoardActive.isAppEnabled)
                        {
                            mBoardActive.postLogin(new BoardActive.PostLoginCallback() {
                                @Override
                                public void onResponse(Object value) {

                                }
                            },"tpowell+bakit@boardactive.com","Axiom!123");
                            mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                                @Override
                                public void onResponse(Object value) {
                                    Log.d(TAG, value.toString());
                                    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create();
                                    //   Me me = gson.fromJson(value.toString(), Me.class);
                                    JsonParser parser = new JsonParser();
                                    JsonElement je = parser.parse(value.toString());
                                    httpReponse.setText(gson.toJson(je));
                                    Log.d(TAG, gson.toJson(je));
                                }
                            });
                        }
                        mBoardActive.getGeoCoordinates(new BoardActive.GetMeCallback() {
                            @Override
                            public void onResponse(Object value) {
                            // mBoardActive.getLocationList();
                              // mBoardActive.addGeofence(MainActivity.this,getGeofencePendingIntent());
                            }
                        });

                    }
                });
    //   registerReceiver();
    }
    /* register receiver for geofence trigger*/

    public void registerReceiver(){
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void cancelService() {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
       // mBoardActive.removeGeofence(this);

    }

    @Override
    protected void onDestroy() {
      //  unregisterReceiver(br);
        super.onDestroy();
        cancelService();
    }

    @Override
    protected void onStop() {
      //  unregisterReceiver(br);
        super.onStop();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Toast.makeText(this, "starting broadcast", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GeofenceBroadCastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }
}
