/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boardactive.sdk.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.boardactive.sdk.R;
import com.boardactive.sdk.bootservice.AdDropJobDispatcherService;
import com.boardactive.sdk.models.AdDropEvent;
import com.boardactive.sdk.models.AdDropEventParams;
import com.boardactive.sdk.network.NetworkClient;
import com.boardactive.sdk.network.NetworkInterface;
import com.boardactive.sdk.ui.addrop.AdDropActivity;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AdDropMessagingService extends FirebaseMessagingService {

    private static final String TAG = "AdDropMessagingService";
    Integer promotion_id;
    String mDeviceToken;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                 mDeviceToken = instanceIdResult.getToken();
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            }
        });

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            promotion_id = Integer.parseInt(remoteMessage.getData().get("promotion_id"));
            Log.d(TAG, "PROMO ID:" +promotion_id );
            //                        AdDropRegister app = new AdDropRegister();
//                        app.setFirebaseProjectId("boardactive-sdk");
//                        app.setFirebaseClientEmail("firebase-adminsdk-1zs9w@boardactive-sdk.iam.gserviceaccount.com");
//                        app.setBundleIdentifier(getPackageName());
//                        app.setName("BASDK TEST");
//                        app.setAdvertiser_id(233);
//                        app.setFirebasePrivateKey("-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDY4WaoSTyoeHhp\\ndabJxjMtKMsjF9ZELP1HgHixtS+A5SkwrNr1F0VR5vyCoYaoOPBOMnzTO7tZpYy3\\nsZ576Biwk9y+sYJq2mf1s66Z/1qOmJbMK45lWQIhQwuRydeTHCZiL82nKXqWQ5qz\\n4/9unfSbg4jZvC8dZcz/R/dutPzWUdWOXDArY8xYJ9LBA55xB2KXL4Frdu9wAEVz\\n+FdiSJIEZZ9hi6/N/bmse3x5M+lxtNiXd0Ok3D5NKMAlGDIVwsRzMYmOfg2f0H65\\nVnKSJkrwAvKxapNjABDml1Mdic1RhamphU9fV2M53Z5RPv392V17AGOMFgoUdYgs\\n9DnxYn/BAgMBAAECggEAFZV/FDXvr7uHjkVLc07Clpa1+rjbVd8dWNGYEWV9BAiq\\nbAHz64rklvVUQYLmRhUek0Wyhwoth8syQTDP2zh6xo38nMzJNC2mxXzbnk9buY/F\\niC24hu0QCXPk8Z4jEmythD2KK730yYISYh9Jcz6MkrKJOtQf4z7XoztMDGw+Mmz0\\n/a4TaMZLqLORfeJVGG15omeo1RTESgw8eeC3zbaMmQuR612flxoKlC3y/gMS9WS8\\nmgJeSqK/u3SnLNptpLV3gLrN30hzka4R4LvXBcNQK/laYA6F4bGmxqiHLYj8i/Gc\\nBI/GoAsq1XT+/j7EsLe3EEYQLCgdD11eYkGkXB7MbQKBgQD83+m8OQqgugHnsc5a\\nPmPxhTKTSYHvb12lsR7WCKVF46yxDnGXwg0qipJIPpkGus74QlM9fi6YcOEtwkQK\\n5lVPoM0VXVJrrWAlV3pRtI7GCYdSN4wUOtm7/2WbxSoy7ycaEFRjEZljDWQKdP5P\\nrVcwf7mrKOHv8v8y1X40PjMIBQKBgQDbj5qDnQej8IaGbvTHbj87u98IJWg76lwX\\nJWjC+C9tn8xe/DXB3SXxM0w3QzVCDfoPmZCeIL/7okWuw4SGNGr3J4il/0pP/0Sr\\n0MXGbdBHD2F2cUwEZKSUz1t8Ijsn7uibCHvGmeBPakV0h3i+HP8KFCVoodXxJ3/w\\nnt21Ar/RjQKBgQCCG9NvfQny2MHSLLI3zJIv2pDDJ7crMunELvXmulwPMa3RC9V2\\nd+m+Ub4iXdLum3+STM33fc0Lskip+qJ32Ttb1SiwLWwS6wnlLLVLBNPRIWX2742r\\nevw8tpPZKgEkY9iCmJRSxONfC6zFlJyk8lNCKPWnE0ns4+JajW56AubO4QKBgDlC\\n5oVUut1iqXL+FRC+C/fEM5KoTtrxcDsJIp1WpOfuORq8pDh/OJoDSulOueEUTBct\\nca4L1IYH+CxwCWwG167FvLmuLu9WH86/kBUEJsGhnUWKnsy2gsXcnnttYgg0Iq3s\\nNHvDPeD4Ukzl1/OdFFbIkkkLjARszM0wYZoHsYcxAoGALAJuHhSJVkHKLIvFCI2B\\nDEXd5seQSwI7InAE+pRHmrwh3LOftW0PVFVPFrHk5RgArMHapelgOuQ3BEl8E0MQ\\nunpJcvmECwuGjYEhkzzovEQ3Ornd3wbttCwX4XDE2rg47tkfUlbbpMfrSRse3uBN\\n84LVpNjNrRtn0BRleNdUZiQ=\\n-----END PRIVATE KEY-----\\n");
//
            AdDropEvent event = new AdDropEvent();
            event.setName("Received");
            AdDropEventParams params = new AdDropEventParams();
            params.setAdvertisement_id("1132");
            params.setPromotion_id("845");
            params.setFirebaseNotificationId(mDeviceToken);



            event.setParams(params);

            Log.w("FCM", "Promo: "+params.getPromotion_id() + " AdvertisementId: " +params.getAdvertisement_id() );
            String lat ="0";
            String lng ="0";
            getObservableSendEvent(event, lat, lng).subscribeWith(getObserverSendEvent());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(remoteMessage.getNotification().getBody(), promotion_id );
    }
    // [END receive_message]


    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */


    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(AdDropJobDispatcherService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public void sendNotification(String messageBody, Integer promotion_id) {
        Intent intent = new Intent(this, AdDropActivity.class);
        intent.putExtra("promotion_id", promotion_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    public Observable<AdDropEvent> getObservableSendEvent(AdDropEvent event, String lat, String lng){
        return NetworkClient.getRetrofit(lat, lng).create(NetworkInterface.class)
                .sendEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public DisposableObserver<AdDropEvent> getObserverSendEvent(){
        return new DisposableObserver<AdDropEvent>() {

            @Override
            public void onNext(@NonNull AdDropEvent adDropBookmarkResponse) {
                Log.d(TAG,"Create Event OnNext");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Create Event onError"+ e);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Create Event onComplete");
                //Drawable myDrawable = getBaseContext().getResources().getDrawable(R.drawable.ic_heart);
                //ivFav.setImageDrawable(myDrawable);
                //refresh();
            }
        };
    }

}
