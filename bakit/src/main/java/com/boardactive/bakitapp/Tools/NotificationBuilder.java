package com.boardactive.bakitapp.Tools;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.boardactive.bakitapp.R;
import com.boardactive.bakitapp.models.MessageModel;
import com.bumptech.glide.Glide;

public class NotificationBuilder extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "MyNotificationBuilder";
    private Bitmap mBitmap;

    private Context mContext;
    private MessageModel mObj;
    private PendingIntent mPendingIntent;
    private int mType;
    private Boolean isSilent;

    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";
    public static final int NOTIFICATION_BASIC = 0;
    public static final int NOTIFICATION_BIG_PIC = 1;
    public static final int NOTIFICATION_ACTION_BUTTON = 2;
    public static final int NOTIFICATION_BIG_TEXT = 3;
    public static final int NOTIFICATION_INBOX = 4;

    public NotificationBuilder(Context context, PendingIntent pendingIntent, MessageModel obj, int type,Boolean isSilent) {
        super();
        this.mContext = context;
        this.mObj = obj;
        this.mPendingIntent = pendingIntent;
        this.mType = type;
        this.isSilent = isSilent;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            mBitmap = Glide.
                    with(mContext).
                    load(mObj.getImageUrl()).
                    asBitmap().
                    into(100, 100). // Width and height
                    get();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        String  channelId = "BAKit";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        switch(mType) {
            case NOTIFICATION_BASIC:
                Log.e("Basic","");//Basic Notification
                if(isSilent){
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setSmallIcon(R.drawable.ba_logo)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                }else
                {
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setSound(null)
                                    .setSmallIcon(R.drawable.ba_logo)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                }

                break;
            case NOTIFICATION_BIG_PIC:
                Log.e("Basic","");//Big Pic Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.drawable.ba_logo)

                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(mBitmap)
                                        .bigLargeIcon(mBitmap));

                break;
            case NOTIFICATION_ACTION_BUTTON:
                Log.e("Action Button","");//Action Button Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.drawable.ba_logo)
                                .addAction(0, "Action Button",
                                        mPendingIntent);;
                break;
            case NOTIFICATION_BIG_TEXT:
                Log.e("Big Text","");//Big Text Notification
                String bigText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.drawable.ba_logo)

                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(bigText));
                break;
            case NOTIFICATION_INBOX: //Inbox Style Notification
                Log.e("Inbox","");//Big Text Notification

                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setSmallIcon(R.drawable.ba_logo)

                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine("Sample MessageModel #1")
                                        .addLine("Sample MessageModel #2")
                                        .addLine("Sample MessageModel #3"))
                                .setContentIntent(mPendingIntent);
                break;
            default:
                Log.e("default","");
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.ba_logo)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent);
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(isSilent){
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_LOW);
                channel.setSound(null,null);
                notificationManager.createNotificationChannel(channel);
            }else
            {
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

        }
        notificationManager.notify(mObj.getId() /* ID of notification */, notificationBuilder.build());
    }
}