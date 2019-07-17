# BAKit-Android
<img src="https://avatars0.githubusercontent.com/u/38864287?s=200&v=4" width="96" height="96"/>
___

### Location-Based Engagement
Enhance your app. Empower your marketing.

It's not about Advertising... It's about "PERSONALIZING"

BoardActive's platform connects brands to consumers using location-based engagement. Our international patent-pending Visualmatic™ software is a powerful marketing tool allowing brands to set up a virtual perimeter around any location, measure foot-traffic, and engage users with personalized messages when they enter geolocations… AND effectively attribute campaign efficiency by seeing where users go after the impression! 

Use your BoardActive account to create Places (geo-fenced areas) and Messages (notifications) to deliver custom messages to your app users. 

[Click Here to get a BoardActive account](https://www.boardactive.com)

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
    implementation 'com.github.BoardActive:BAKit-Android:Tag'
    ...
}
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
    ...
}
// Include Google Play Services
apply plugin: 'com.google.gms.google-services'
```

### Add the Firebase Messaging Service and Worker Classes
If you app does not already support Firebase messaging you can follow these instructions to add to your app.

#### Messaging Service Class
```javascript
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

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
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]
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

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(getString(R.string.fcm_message))
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

	//Add the BoardActive Object
	private BoardActive mBoardActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create an instant of BoardActive
        mBoardActive = new BoardActive(getApplicationContext());
		
		 // Add URL to point to BoardActive REST API
	    mBoardActive.setAppUrl("https://api.boardactive.com/mobile/v1/");

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
                        mBoardActive.RegisterDevice(new BoardActive.PostRegisterCallback() {
                            @Override
                            public void onResponse(Object value) {
                                Log.d("[BAkit]", value.toString());
                                onResume();
                            }
                        });
                    }
                });
    }
```

## Download Example App Source Code
There is an example app provided [here](https://github.com/BoardActive/BAAdDrop-Android) for Android.

## Ask for Help

Our team wants to help. Please contact us 
* Call us: [678) 383-2200](tel:+6494461709)
* Email Us [support@boardactive.com](mailto:info@boardactive.com)
* Online Support [Web Site](https://www.boardactive.com/)


