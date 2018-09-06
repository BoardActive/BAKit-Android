package com.boardactive.addrop;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.boardactive.sdk.fcm.AdDropFCMActivity;
import com.boardactive.sdk.location.AdDropLocationActivity;
import com.boardactive.sdk.ui.addrop.AdDropActivity;
import com.boardactive.sdk.ui.AdDropMainActivity;



public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    Intent intent_1 = new Intent(MainActivity.this, AdDropLocationActivity.class);
                    startActivity(intent_1);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_addrops);
                    Intent intent_2 = new Intent(MainActivity.this, AdDropMainActivity.class);
                    startActivity(intent_2);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Intent intent_3 = new Intent(MainActivity.this, AdDropFCMActivity.class);
                    startActivity(intent_3);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
