package com.boardactive.addrop.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.boardactive.addrop.R;

public class AppIdActivity extends AppCompatActivity {
    AppCompatEditText editText;
    Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_id);
        editText = findViewById(R.id.editAppId);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             String text= editText.getText().toString().trim();
                                             Intent intent= new Intent(getApplicationContext(),MainActivity.class);
                                             intent.putExtra("APPID",text);
                                             startActivity(intent);
                                         }
                                     }
        );

    }
}