package com.boardactive.bakit.oreo;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.boardactive.bakit.Tools.Utils;


public class AlarmJobIntentService extends JobIntentService {

    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";

    /* Give the Job a Unique Id */
    private static final int JOB_ID = 1000;
    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, AlarmJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /* your code here */
        /* reset the alarm */
        Utils.sendNotification(this);

        AlarmReceiver.setAlarm(false);
        stopSelf();
    }

}