package com.boardactive.bakit;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class PermissionExceptionHandler {
Context context;

    public PermissionExceptionHandler(Context context) {
        this.context = context;
    }

    public boolean wantToStartWorker() {
        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionState == PackageManager.PERMISSION_GRANTED) {
            int backgroundPermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                backgroundPermission = ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION);


                if (backgroundPermission == PackageManager.PERMISSION_GRANTED) {
                    // start worker after 2 min
                    return true;
                } else {
                    // start worker after 2 min
// stop all works
                    return isAppRunning(context, context.getApplicationContext().getPackageName());
                }
            } else {
                return true;
            }
        } else {
            // stop all works
           return false;
        }

    }


    public static boolean isAppRunning(Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = null;
        if (activityManager != null) {
            procInfos = activityManager.getRunningAppProcesses();
        }
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static PermissionExceptionHandler with(Context context) {
        return new PermissionExceptionHandler(context);
    }
}
