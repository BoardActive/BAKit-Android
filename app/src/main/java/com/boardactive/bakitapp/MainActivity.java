package com.boardactive.bakitapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.boardactive.bakit.BoardActive;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Add the BoardActive Object
    private BoardActive mBoardActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
        mBoardActive.setAppUrl(BoardActive.APP_URL_PROD);

        // Add AppID provided by BoardActive
        mBoardActive.setAppId("ADD_APP_ID");

        // Add AppKey provided by BoardActive
        mBoardActive.setAppKey("ADD_APP_KEY");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

        // Get Firebase Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        // Add Firebase Token to BoardActive
                        mBoardActive.setAppToken(fcmToken);

                        // Initialize BoardActive
                        mBoardActive.initialize();

                        // Register the device with BoardActive
                        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {
                                Log.d("[BAkit]", value.toString());
                                onResume();
                            }
                        });
                    }
                });
    }
}
