/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.boardactive.addrop.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.boardactive.addrop.R;
import com.boardactive.addrop.activity.MessageActivity;
import com.boardactive.addrop.room.AppDatabase;
import com.boardactive.addrop.room.DAO;
import com.boardactive.addrop.room.table.MessageEntity;
import com.boardactive.bakit.BoardActive;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    public static final String EXTRA_MESSADE_ID = "key.EXTRA_MESSADE_ID";
    private static final String MESSAGE_ID = "MESSAGE_ID";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    private DAO dao;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "[BAAdDrop] From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "[BAAdDrop] Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "[BAAdDrop] Message Notification Body: " + remoteMessage);
            sendNotification(remoteMessage);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
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
        Log.d(TAG, "[BAAdDrop] Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
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
     * Set notification's tap action.
     *
     * @param remoteNotification FCM notification.
     */
    private void sendNotification(final RemoteMessage remoteNotification) {
        Long currentDateandTime = System.currentTimeMillis();
        MessageEntity obj = new MessageEntity();

        dao = AppDatabase.getDb(this).getDAO();
        Integer count = dao.getMessageCount();
        obj.setId(count);
        obj.setMessageId(remoteNotification.getData().get("messageId"));
        obj.setNotificationId(remoteNotification.getMessageId());
        obj.setTitle(remoteNotification.getData().get("title"));
        obj.setBody(remoteNotification.getData().get("body"));
        obj.setImageUrl(remoteNotification.getData().get("imageUrl"));
        obj.setLatitude(remoteNotification.getData().get("latitude"));
        obj.setLongitude(remoteNotification.getData().get("longitude"));
        obj.setMessageData(remoteNotification.getData().get("messageData"));
        obj.setIsTestMessage(remoteNotification.getData().get("isTestMessage"));
        obj.setDateCreated(currentDateandTime);
        obj.setDateLastUpdated(currentDateandTime);
        obj.setIsRead(false);
        dao.insertMessage(obj);

//        JSONObject prefernceMessage = new JSONObject();
//        try {
//            prefernceMessage.put("dateCreated", obj.dateCreated);
//            prefernceMessage.put("dateLastUpdated", obj.dateLastUpdated);
//            prefernceMessage.put("messageId", obj.messageId);
//            prefernceMessage.put("imageUrl", obj.imageUrl);
//            prefernceMessage.put("isTestMessage", obj.isTestMessage);
//            prefernceMessage.put("body", obj.body);
//            prefernceMessage.put("title", obj.title);
//            prefernceMessage.put("notificationId", obj.notificationId);
//            prefernceMessage.put("latitude", obj.latitude);
//            prefernceMessage.put("longitude", obj.longitude);
//            prefernceMessage.put("messageData", obj.messageData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("LAST_MSG", prefernceMessage.toString());
//        editor.commit();

        BoardActive mBoardActive = new BoardActive(getApplicationContext());
        mBoardActive.postEvent(new BoardActive.PostEventCallback() {
            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "Received Event: " + value.toString());
            }
        }, "received", obj.messageId, obj.notificationId);

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(EXTRA_MESSADE_ID, obj.getId());
        intent.putExtra(EXTRA_OBJECT, obj);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_VIEW);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(obj.title)
                        .setContentText(obj.body)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private String getSharedPreference(String name) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String value = settings.getString(name,"");
        return value;
    }

}
