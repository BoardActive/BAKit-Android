package com.boardactive.bakit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class NotificationReceiver  extends BroadcastReceiver {
    Bitmap bitmap;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
        context.stopService(serviceIntent);

    }
}
