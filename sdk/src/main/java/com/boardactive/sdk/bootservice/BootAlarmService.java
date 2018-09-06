package com.boardactive.sdk.bootservice;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.boardactive.sdk.ui.AdDropMainActivity;

public class BootAlarmService extends Service {
public void onCreate() {

    super.onCreate();
    Intent intent = new Intent(this, AdDropMainActivity.class);
    PendingIntent pendingIntent = PendingIntent
            .getBroadcast(this, 0, intent, 0);
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis()
                    + (5 * 1000),
            System.currentTimeMillis()
                    + (5 * 1000), pendingIntent);
//    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (5 * 1000), pendingIntent);
    Toast.makeText(this, "Alarm set in 5 seconds",
        Toast.LENGTH_LONG).show();
    }


@Override
public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
   }

}