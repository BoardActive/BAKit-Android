package com.boardactive.addrop.dialog;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.boardactive.addrop.R;

public class SettingsDialog extends AlertDialog.Builder {

    public interface InputSenderDialogListener{
        public abstract void onSignIn(Boolean development, String email, String password);
    }

    private EditText mEmail, mPassword;
    private CheckBox mDevelopment;

    public SettingsDialog(AppCompatActivity activity, final InputSenderDialogListener listener) {
        super( new ContextThemeWrapper(activity, R.style.AppTheme) );

        @SuppressLint("InflateParams") // It's OK to use NULL in an AlertDialog it seems...
                View dialogLayout = LayoutInflater.from(activity).inflate(R.layout.dialog_settings, null);
        setView(dialogLayout);

        mEmail = dialogLayout.findViewById(R.id.email);
        mPassword = dialogLayout.findViewById(R.id.password);
        mDevelopment = dialogLayout.findViewById(R.id.chk_development);

        setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if( listener != null )
                    listener.onSignIn(mDevelopment.isChecked(), String.valueOf(mEmail.getText()), String.valueOf(mPassword.getText()));
            }
        });

    }

    public SettingsDialog setCredentials(String userName, String password){
        mEmail.setText( userName );
        mPassword.setText( password );
        return this;
    }

    @Override
    public AlertDialog show() {
        AlertDialog dialog = super.show();
        Window window = dialog.getWindow();
        if( window != null )
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }
}