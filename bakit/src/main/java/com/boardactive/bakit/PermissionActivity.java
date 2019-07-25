package com.boardactive.bakit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Transparent Activity to prompt user for location permissions
 *
 * */
public class PermissionActivity extends AppCompatActivity {

    private String[] permissions;
    private int pCode = 12321;
    public PermissionListener permissionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

            boolean flag = false;
            for (String s : permissions)
                if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED)
                    flag = true;

            if (flag) {
                requestPermissions(permissions, pCode);
            } else {
                permissionListener.permissionResult(true);
                finish();
            }
        }else
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == pCode) {
            boolean flag = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                for (int i = 0, len = permissions.length; i < len; i++)
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        flag = false;
            if (flag) {
                if (permissionListener != null)
                    permissionListener.permissionResult(true);
            } else if (permissionListener != null)
                permissionListener.permissionResult(false);
            finish();
        }
    }
}
