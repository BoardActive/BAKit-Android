package com.boardactive.sdk;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * BoardActive 2018.08.05
 */
public abstract class BaseActivity extends AppCompatActivity {

    public void showErrorMessage() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}
