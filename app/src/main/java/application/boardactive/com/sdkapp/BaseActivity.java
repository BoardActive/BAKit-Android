package application.boardactive.com.sdkapp;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import application.boardactive.com.sdkapp.R;

/**
 * BoardActive 2018.08.05
 */
public abstract class BaseActivity extends AppCompatActivity {

    public void showErrorMessage() {
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }
}
