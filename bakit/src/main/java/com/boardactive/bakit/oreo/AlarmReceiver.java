package com.boardactive.bakit.oreo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String TAG = AlarmReceiver.class.getName();

    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";
    public static Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        /* enqueue the job */
        AlarmJobIntentService.enqueueWork(context, intent);
        mContext = context;
    }
    public static void cancelAlarm() {
        Log.d(TAG, "Cancel Alarm");

        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        
        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent());
    }
    public static void setAlarm(boolean force) {
        cancelAlarm();
        Log.d(TAG, "Start Alarm");
        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // EVERY X MINUTES
        long delay = (1000 * 60 * 1);
        long when = System.currentTimeMillis();
        if (!force) {
            when += delay;
        }
        
        /* fire the broadcast */
        alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent());
    }
    private static PendingIntent getPendingIntent() {
        Log.d(TAG, "getPendingIntent()");
        Intent AlarmJobIntentService = new Intent(mContext, AlarmJobIntentService.class);
        AlarmJobIntentService.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(mContext, 0, AlarmJobIntentService, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}