# BAKit-Android
<img src="https://avatars0.githubusercontent.com/u/38864287?s=200&v=4" width="96" height="96"/>
___

### Location-Based Engagement
Enhance your app. Empower your marketing.

It's not about Advertising... It's about "PERSONALIZING"

BoardActive's platform connects brands to consumers using location-based engagement. Our international patent-pending Visualmatic™ software is a powerful marketing tool allowing brands to set up a virtual perimeter around any location, measure foot-traffic, and engage users with personalized messages when they enter geolocations… AND effectively attribute campaign efficiency by seeing where users go after the impression! 

Use your BoardActive account to create Places (geo-fenced areas) and Messages (notifications) to deliver custom messages to your app users. 

[Click Here to get a BoardActive account](https://app.boardactive.com/login)

The BAKit SDK will use a device's location to know when an app user passes into a geo-fence. Passing into a geo-fence can trigger an event allowing you to deliver notifications to your app users.  

## Create a Firebase Project 
To use Firebase Cloud Messaging you must have a Firebase project. 

[Click Here to see the Firebase tutorial](https://firebase.google.com/docs/android/setup)

[Click Here to go to the Firebase Console](https://console.firebase.google.com/u/0/)

Once you create a related Firebase project you can download the google-service.json which you need to include with your android project.

## Installing the BAKit SDK

BoardActive for Android supports APK 15 and greater. 

To get the most our of the BAKit SDK your app will need to allow location permissions. 

To use the BAKit SDK your will need to implement our library. 

### SDK
Include the BAKit SDK into your project with JitPack repository.

### Dependencies
Your app must include Google Play Services.  Our [Google Play Services Overview](https://developers.google.com/android/guides/overview) contains full setup and initialisation instructions.

We use JitPack as a repository service, you will add a few lines to the gradle files to import our SDK into your project. (Instructions for Maven, sbt, and leiningen available upon request)

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.4'
        //Add the following to your Top-Level build.gradle
        classpath 'com.google.gms:google-services:4.0.0'        
    }
}
// Add JitPack repository to top level build.gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
			
		}
	}
```

Include the following to your App-level build.gradle

```java
...
dependencies {
    ...
    // This line imports the BAKit-Android to your project.
    implementation 'com.github.BoardActive:BAKit-Android:1.0.3'
    ...
}
```

Include the following to your gradle.properties

```java
...
# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE *will override*
# any settings specified in this file.
# For more details on how to configure your build environment visit
# http://www.gradle.org/docs/current/userguide/build_environment.html
# Specifies the JVM arguments used for the daemon process.
# The setting is particularly useful for tweaking memory settings.
android.enableJetifier=true
android.useAndroidX=true
org.gradle.jvmargs=-Xmx1536m
# When configured, Gradle will run in incubating parallel mode.
# This option should only be used with decoupled projects. More details, visit
# http://www.gradle.org/docs/current/userguide/multi_project_builds.html#sec:decoupled_projects
# org.gradle.parallel=true}
```

### Include Location permissions to your android project
The BAKit SDK will check if permissions are allowed. If not, the SDK will automatically ask for location permissions. 

## Add Firebase Messaging to your app

### Add Gradle Dependencies
If you app does not already support Firebase messaging you can follow these instructions to add to your app.

```javascript
...
dependencies {
    ...
    // This line imports the Firebase Support to your project.
    implementation 'com.google.firebase:firebase-core:16.0.8'
    implementation 'com.google.firebase:firebase-iid:17.1.2'
    implementation 'com.google.firebase:firebase-messaging:17.6.0'
    implementation 'android.arch.work:work-runtime:1.0.1'
    ...
}
// Include Google Play Services
apply plugin: 'com.google.gms.google-services'
```

### Add the Firebase Messaging Service and Worker Classes
If you app does not already support Firebase messaging you can follow these instructions to add to your app.

#### Notification Builder Class
```javascript
public class NotificationBuilder extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "MyNotificationBuilder";
    private Bitmap mBitmap;

    private Context mContext;
    private MessageModel mObj;
    private PendingIntent mPendingIntent;
    private int mType;

    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";
    public static final int NOTIFICATION_BASIC = 0;
    public static final int NOTIFICATION_BIG_PIC = 1;
    public static final int NOTIFICATION_ACTION_BUTTON = 2;
    public static final int NOTIFICATION_BIG_TEXT = 3;
    public static final int NOTIFICATION_INBOX = 4;

    public NotificationBuilder(Context context, PendingIntent pendingIntent, MessageModel obj, int type) {
        super();
        this.mContext = context;
        this.mObj = obj;
        this.mPendingIntent = pendingIntent;
        this.mType = type;
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

        String channelId = mContext.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        switch(mType) {
            case NOTIFICATION_BASIC: //Basic Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent);
                break;
            case NOTIFICATION_BIG_PIC: //Big Pic Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(mBitmap)
                                        .bigLargeIcon(mBitmap));

                break;
            case NOTIFICATION_ACTION_BUTTON: //Action Button Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .addAction(R.drawable.ic_notification, "Action Button",
                                        mPendingIntent);;
                break;
            case NOTIFICATION_BIG_TEXT: //Big Text Notification
                String bigText = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(bigText));
                break;
            case NOTIFICATION_INBOX: //Inbox Style Notification
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent)
                                .setLargeIcon(mBitmap)
                                .setStyle(new NotificationCompat.InboxStyle()
                                        .addLine("Sample MessageModel #1")
                                        .addLine("Sample MessageModel #2")
                                        .addLine("Sample MessageModel #3"))
                                .setContentIntent(mPendingIntent);
                break;
            default:
                notificationBuilder =
                        new NotificationCompat.Builder(mContext, channelId)
                                .setSmallIcon(R.drawable.ic_notification)
                                .setContentTitle(mObj.getTitle())
                                .setContentText(mObj.getBody())
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                .setContentIntent(mPendingIntent);
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(mObj.getId() /* ID of notification */, notificationBuilder.build());
    }
}
```

#### Messaging Service Class
```javascript
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
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";
    public static final String EXTRA_OBJECT = "key.EXTRA_OBJECT";
    public static final String EXTRA_MESSADE_ID = "key.EXTRA_MESSADE_ID";
    private static final String MESSAGE_ID = "MESSAGE_ID";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

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
            Log.d(TAG, "[BAAdDrop] MessageModel data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage);

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
            Log.d(TAG, "[BAAdDrop] MessageModel Notification Body: " + remoteMessage);
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
        MessageModel obj = new MessageModel();

        int id = 1;
        obj.setId(id);
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

        BoardActive mBoardActive = new BoardActive(getApplicationContext());
        mBoardActive.postEvent(new BoardActive.PostEventCallback() {
            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "Received Event: " + value.toString());
            }
        }, "received", obj.getMessageId(), obj.getNotificationId());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSADE_ID, id);
//        intent.putExtra(EXTRA_OBJECT, obj);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NO_HISTORY
        );
        intent.setAction(Intent.ACTION_VIEW);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);


        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
        new NotificationBuilder(this,pendingIntent, obj, notificationType).execute();

    }

}
```

#### Schedule Worker Class

```java
public class MyWorker extends Worker {

    private static final String TAG = "MyWorker";

    public MyWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return Result.success();
    }
}
```

#### Message Model Class

```java
public class MessageModel {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("messageId")
    @Expose
    private String messageId;

    @SerializedName("notificationId")
    @Expose
    private String notificationId;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("messageData")
    @Expose
    private String messageData;

    @SerializedName("isTestMessage")
    @Expose
    private String isTestMessage;

    @SerializedName("isRead")
    @Expose
    private Boolean isRead;

    @SerializedName("dateCreated")
    @Expose
    private Long dateCreated;

    @SerializedName("dateLastUpdated")
    @Expose
    private Long dateLastUpdated;


    /**
     * No args constructor for use in serialization
     */
    public MessageModel() {
    }

    /**
     * @param id
     * @param messageId
     * @param notificationId
     * @param title
     * @param body
     * @param imageUrl
     * @param latitude
     * @param longitude
     * @param messageData
     * @param isTestMessage
     * @param isRead
     * @param dateCreated
     * @param dateLastUpdated
     */
    public MessageModel(
            Integer id,
            String messageId,
            String notificationId,
            String title,
            String body,
            String imageUrl,
            String latitude,
            String longitude,
            String messageData,
            String isTestMessage,
            Boolean isRead,
            Long dateCreated,
            Long dateLastUpdated
    ) {
        super();
        this.id = id;
        this.messageId = messageId;
        this.notificationId = notificationId;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.messageData = messageData;
        this.isTestMessage = isTestMessage;
        this.isRead = isRead;
        this.dateCreated = dateCreated;
        this.dateLastUpdated = dateLastUpdated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getIsTestMessage() {
        return isTestMessage;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsTestMessage(String isTestMessage) {
        this.isTestMessage = isTestMessage;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateLastUpdated() {
        return dateLastUpdated;
    }

    public void setDateLastUpdated(Long dateLastUpdated) {
        this.dateLastUpdated = dateLastUpdated;
    }


}
```

#### Add to your AndroidManifest.xml

```xml
    <service
        android:name=".MyFirebaseMessagingService"
        android:exported="false">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>
```


## How to use the BAKit SDK

### Use BAKit SDK in your Launch Activity

```java
import com.boardactive.bakit.BoardActive;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Add the BoardActive Object
    private BoardActive mBoardActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());

        // Add URL to point to BoardActive REST API
        mBoardActive.setAppUrl(BoardActive.APP_URL_PROD);

        // Add AppID provided by BoardActive
        mBoardActive.setAppId("ADD_APP_ID");

        // Add AppKey provided by BoardActive
        mBoardActive.setAppKey("ADD_APP_KEY");

        // Add the version of your App
        mBoardActive.setAppVersion("1.0.0");

        // Get Firebase Token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String fcmToken = task.getResult().getToken();

                        // Add Firebase Token to BoardActive
                        mBoardActive.setAppToken(fcmToken);

                        // Initialize BoardActive
                        mBoardActive.initialize();

                        // Register the device with BoardActive
                        mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {
                                Log.d("[BAkit]", value.toString());
                                onResume();
                            }
                        });
                    }
                });
    }
}
```

## Download Example App Source Code
There is an example app provided [here](https://github.com/BoardActive/BAAdDrop-Android) for Android.

## Ask for Help

Our team wants to help. Please contact us 

* Help Documentation: [http://help.boardactive.com/en/](http://help.boardactive.com/en/)
* Call us: [(657)229-4669](tel:+6572294669)
* Email Us [taylor@boardactive.com](mailto:taylor@boardactive.com)
* Online Support [Web Site](https://www.boardactive.com/)


