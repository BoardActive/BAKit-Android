package com.boardactive.sdk.bootservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
      public void onReceive(Context context, Intent intent) {

      Toast.makeText(context, "Intent BroadcastReceiver Detected.", Toast.LENGTH_LONG).show();
      System.out.println("This is the BootReceiver");

        /****** For Start Activity *****/
//        Intent i = new Intent(context, AdDropMainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

        /***** For start Service  ****/
        Intent service = new Intent(context, BootAlarmService.class);
        //service.setAction(RECEIVE_BOOT_COMPLETED);
        context.startService(service);
      }

}