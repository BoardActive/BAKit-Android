package com.boardactive.bakit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.boardactive.bakit.oreo.MyJobIntentService;

public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = BootReceiver.class.getSimpleName();
    // This class is triggered a minute or two after the device is restarted, it starts our
    // location reporting service and Firebase Notification Job
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(context, MyJobIntentService.class);
            mIntent.putExtra("maxCountValue", 1000);
            MyJobIntentService.enqueueWork(context, mIntent);
        }

        }
    }