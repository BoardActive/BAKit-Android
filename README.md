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

```java
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
    classpath 'com.android.tools.build:gradle:4.2.2'
     //Add the following to your Top-Level build.gradle
    classpath 'com.google.gms:google-services:4.3.3'
    }
}
// Add JitPack repository to top level build.gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }

	We use JitPack as a repository service, you will add a few lines to the gradle files to import our SDK into your project. (Instructions for Maven, sbt, and leiningen available upon request)

	```java
	// Top-level build file where you can add configuration options common to all sub-projects/modules.
	buildscript {
	    repositories {
		google()
		jcenter()
	    }
	    dependencies {
	    classpath 'com.android.tools.build:gradle:4.2.2'
	     //Add the following to your Top-Level build.gradle
	    classpath 'com.google.gms:google-services:4.3.3'  
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
	    implementation 'com.github.BoardActive:BAKit-Android:2.0.3'
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
	    implementation 'com.google.firebase:firebase-core:21.1.1'
	    implementation 'com.google.firebase:firebase-iid:21.1.0'
	    implementation 'com.google.firebase:firebase-messaging:23.1.1'
	    implementation 'androidx.work:work-runtime:2.7.1'
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
	    private Boolean isSilent;

	    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";
	    public static final int NOTIFICATION_BASIC = 0;
	    public static final int NOTIFICATION_BIG_PIC = 1;
	    public static final int NOTIFICATION_ACTION_BUTTON = 2;
	    public static final int NOTIFICATION_BIG_TEXT = 3;
	    public static final int NOTIFICATION_INBOX = 4;

	    public  NotificationBuilder(Context context, PendingIntent pendingIntent, MessageModel obj, int type,Boolean isSilent) {
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
			    into(300, 300). // Width and height
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
			if(mObj.getAllowToDownloadNotificationImage()) {

			    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
				    .setContentTitle(mObj.getTitle())
				    .setContentText(mObj.getBody())
				    .setAutoCancel(true)
				    .setContentIntent(mPendingIntent)
				    .addAction(0,"Download Image",mPendingIntent)
				    .setSound(defaultSoundUri)
				    .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
					    .bigPicture(mBitmap).bigLargeIcon(null))
				    .setSmallIcon(R.drawable.ba_logo)
				    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
			}else
			{
			    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
				    .setContentTitle(mObj.getTitle())
				    .setContentText(mObj.getBody())
				    .setAutoCancel(true)
				    .setContentIntent(mPendingIntent)
				    .setSound(defaultSoundUri)
				    .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
					    .bigPicture(mBitmap).bigLargeIcon(null))
				    .setSmallIcon(R.drawable.ba_logo)
				    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
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
					.addAction(0,"Download Image",mPendingIntent)
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
					.addAction(0,"Download Image",mPendingIntent)
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
					.addAction(0,"Download Image",mPendingIntent)
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
					.addAction(0,"Download Image",mPendingIntent)
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
			if(mObj.getAllowToDownloadNotificationImage()) {
			    notificationBuilder =
				    new NotificationCompat.Builder(mContext, channelId)
					    .setContentTitle(mObj.getTitle())
					    .setContentText(mObj.getBody())
					    .setAutoCancel(true)
					    .setLargeIcon(mBitmap)
					    .addAction(0,"Download Image",mPendingIntent)
					    .setSmallIcon(R.drawable.ba_logo).setStyle(new NotificationCompat.BigPictureStyle()
						    .bigPicture(mBitmap).bigLargeIcon(null))
					    .setSound(defaultSoundUri)
					    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
					    .setContentIntent(mPendingIntent);
			}else {
			    notificationBuilder =
				    new NotificationCompat.Builder(mContext, channelId)
					    .setContentTitle(mObj.getTitle())
					    .setContentText(mObj.getBody())
					    .setAutoCancel(true)
					    .setLargeIcon(mBitmap)
					    .setSmallIcon(R.drawable.ba_logo).setStyle(new NotificationCompat.BigPictureStyle()
						    .bigPicture(mBitmap).bigLargeIcon(null))
					    .setSound(defaultSoundUri)
					    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
					    .setContentIntent(mPendingIntent);
			}

			break;
		}

		NotificationManager notificationManager =
			(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
		    NotificationChannel channel = new NotificationChannel(channelId,
				"Channel human readable title",
				NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);

		}
		int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
	      //  if(!isSilent){
		    notificationManager.notify(m /* ID of notification */, notificationBuilder.build());

		//  }
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
	    public static final String IMAGE_URL = "key.IMAGE_URL";
	    public static final String TITLE = "key.TITLE";
	    public static final String DESC = "key.DESC";

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


		Log.d(TAG, "on message received data");

		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "[BAAdDrop] From: " + remoteMessage.getFrom());
		Log.d(TAG, "notification data");

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
		OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FCMWorker.class)
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
	     * @param remoteMessage FCM notification.
	     */
	    private void sendNotification(final RemoteMessage remoteMessage) {
		Long currentDateandTime = System.currentTimeMillis();
		MessageModel obj = new MessageModel();
		Log.e("firebase message id",""+remoteMessage.getMessageId().trim());

		Log.e("firebase message", Objects.requireNonNull(remoteMessage.getMessageId().trim()));

		int id = 1;
		obj.setId(id);
		obj.setBaMessageId(remoteMessage.getData().get("baMessageId"));
		obj.setBaNotificationId(remoteMessage.getData().get("baNotificationId"));
		obj.setFirebaseNotificationId(remoteMessage.getMessageId());
		obj.setTitle(remoteMessage.getData().get("title"));
		obj.setBody(remoteMessage.getData().get("body"));
		obj.setImageUrl(remoteMessage.getData().get("imageUrl"));
		obj.setLatitude(remoteMessage.getData().get("latitude"));
		obj.setLongitude(remoteMessage.getData().get("longitude"));
		obj.setMessageData(remoteMessage.getData().get("messageData"));
		obj.setIsTestMessage(remoteMessage.getData().get("isTestMessage"));
		obj.setDateCreated(currentDateandTime);
		obj.setDateLastUpdated(currentDateandTime);
		Boolean isSilent = Boolean.valueOf(remoteMessage.getData().get("isSilent"));
		String action = remoteMessage.getData().get("action");
		String type = remoteMessage.getData().get("type");
		String placeId = remoteMessage.getData().get("placeId");
		Boolean isAllowToDownloadNotificationImage = Boolean.valueOf(remoteMessage.getData().get("isAllowToDownloadNotificationImage"));
		obj.setAllowToDownloadNotificationImage(isAllowToDownloadNotificationImage);
		Log.v(isSilent.toString(),"");
		Log.v(action,"action");
		obj.setAction(action);


		BoardActive mBoardActive = new BoardActive(getApplicationContext());
		mBoardActive.postEvent(new BoardActive.PostEventCallback() {

		    @Override
		    public void onResponse(Object value) {
			Log.d(TAG, "[BAKitApp] Received Event: " + value.toString());
		    }
		}, "received", obj.getBaMessageId(), obj.getBaNotificationId(), obj.getFirebaseNotificationId().trim());

		if(type != null)
		{
		    if(type.equals("app_status") && Objects.equals(placeId, "null"))
		{
		    if(action != null){
			if(action.equals("Enable")){
			    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
			}
			else
			{
			    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
			}
		    }



		}else if(type.equals("place_update") || type.equals("Campaign") || Objects.equals(placeId, "null")){
		   // SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
		    mBoardActive.setLocationArrayList(null);
		    mBoardActive.getLocationList();
		}

		}
		if(!isSilent){
		    Intent intent = new Intent(this, MainActivity.class);
		    intent.putExtra(EXTRA_MESSADE_ID, id);
		    intent.putExtra(TITLE,obj.getTitle());
		    intent.putExtra(DESC,obj.getBody());
		    intent.putExtra("MessageId",obj.getBaMessageId());
		    intent.putExtra("NotificationId",obj.getBaNotificationId());
		    intent.putExtra("FirebaseMessageId",obj.getFirebaseNotificationId());
		    if(obj.getImageUrl() != null){
			intent.putExtra(IMAGE_URL,obj.getImageUrl());

		    }
		    if(obj.getAllowToDownloadNotificationImage()){
			intent.putExtra("isAllowImage",true);

		    }else
		    {
			intent.putExtra("isAllowImage",false);

		    }
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
		    PendingIntent pendingIntent =null;
		    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
		       //  pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_MUTABLE);
			int requestCode = new Random().nextInt();
			pendingIntent = PendingIntent.getActivity(
				this, requestCode, intent,
				 PendingIntent.FLAG_MUTABLE);


	//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
			int notificationType = 0;
			new com.boardactive.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
		    }else
		    {
			 //pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
			int requestCode = new Random().nextInt();
			pendingIntent = PendingIntent.getActivity(
				this, requestCode, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

	//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
			int notificationType = 0;
			new com.boardactive.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
		    }
		}


	    }

	}
	```

	#### FCM Worker Class

	```java

	```
	public class FCMWorker extends Worker {

	    public static final String TAG = FCMWorker.class.getName();

	    public FCMWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
		super(appContext, workerParams);
	    }

	    @NonNull
	    @Override
	    public Result doWork() {
		Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
		// TODO(developer): add long running task here.
		return Result.success();
	    }
	}
	#### Message Model Class

	```java
	public class MessageModel implements Parcelable {

	    @SerializedName("id")
	    @Expose
	    private Integer id;

	    @SerializedName("baMessageId")
	    @Expose
	    private String baMessageId;

	    @SerializedName("baNotificationId")
	    @Expose
	    private String baNotificationId;

	    @SerializedName("firebaseNotificationId")
	    @Expose
	    private String firebaseNotificationId;

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

	    @SerializedName("action")
	    @Expose
	    private String action;

	    @SerializedName("isAllowToDownloadNotificationImage")
	    @Expose
	    private Boolean isAllowToDownloadNotificationImage;

	    /**
	     * No args constructor for use in serialization
	     */
	    public MessageModel() {
	    }

	    /**
	     * @param id
	     * @param baMessageId
	     * @param baNotificationId
	     * @param firebaseNotificationId
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
		    String baMessageId,
		    String baNotificationId,
		    String firebaseNotificationId,
		    String title,
		    String body,
		    String imageUrl,
		    String latitude,
		    String longitude,
		    String messageData,
		    String isTestMessage,
		    Boolean isRead,
		    Long dateCreated,
		    Long dateLastUpdated,
		    String action
	    ) {
		super();
		this.id = id;
		this.baMessageId = baMessageId;
		this.baNotificationId = baNotificationId;
		this.firebaseNotificationId = firebaseNotificationId;
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
		this.action =action;
	    }

	    protected MessageModel(Parcel in) {
		if (in.readByte() == 0) {
		    id = null;
		} else {
		    id = in.readInt();
		}
		baMessageId = in.readString();
		baNotificationId = in.readString();
		firebaseNotificationId = in.readString();
		body = in.readString();
		title = in.readString();
		imageUrl = in.readString();
		latitude = in.readString();
		longitude = in.readString();
		messageData = in.readString();
		isTestMessage = in.readString();
		byte tmpIsRead = in.readByte();
		isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
		if (in.readByte() == 0) {
		    dateCreated = null;
		} else {
		    dateCreated = in.readLong();
		}
		if (in.readByte() == 0) {
		    dateLastUpdated = null;
		} else {
		    dateLastUpdated = in.readLong();
		}
		action = in.readString();
		byte tmpIsAllowToDownloadNotificationImage = in.readByte();
		isAllowToDownloadNotificationImage = tmpIsAllowToDownloadNotificationImage == 0 ? null : tmpIsAllowToDownloadNotificationImage == 1;
	    }

	    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
		@Override
		public MessageModel createFromParcel(Parcel in) {
		    return new MessageModel(in);
		}

		@Override
		public MessageModel[] newArray(int size) {
		    return new MessageModel[size];
		}
	    };

	    public Integer getId() {
		return id;
	    }

	    public void setId(Integer id) {
		this.id = id;
	    }

	    public String getBaMessageId() {
		return baMessageId;
	    }

	    public void setBaMessageId(String messageId) {
		this.baMessageId = messageId;
	    }

	    public String getBaNotificationId() {
		return baNotificationId;
	    }

	    public void setBaNotificationId(String baNotificationId) {
		this.baNotificationId = baNotificationId;
	    }

	    public String getFirebaseNotificationId() {
		return firebaseNotificationId;
	    }

	    public void setFirebaseNotificationId(String firebaseNotificationId) {
		this.firebaseNotificationId = firebaseNotificationId;
	    }

	    public Boolean getAllowToDownloadNotificationImage() {
		return isAllowToDownloadNotificationImage;
	    }

	    public void setAllowToDownloadNotificationImage(Boolean allowToDownloadNotificationImage) {
		isAllowToDownloadNotificationImage = allowToDownloadNotificationImage;
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

	    public String getAction() {
		return action;
	    }

	    public void setAction(String action) {
		this.action = action;
	    }

	    @Override
	    public int describeContents() {
		return 0;
	    }

	    @Override
	    public void writeToParcel(@NonNull Parcel parcel, int i) {
		if (id == null) {
		    parcel.writeByte((byte) 0);
		} else {
		    parcel.writeByte((byte) 1);
		    parcel.writeInt(id);
		}
		parcel.writeString(baMessageId);
		parcel.writeString(baNotificationId);
		parcel.writeString(firebaseNotificationId);
		parcel.writeString(body);
		parcel.writeString(title);
		parcel.writeString(imageUrl);
		parcel.writeString(latitude);
		parcel.writeString(longitude);
		parcel.writeString(messageData);
		parcel.writeString(isTestMessage);
		parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
		if (dateCreated == null) {
		    parcel.writeByte((byte) 0);
		} else {
		    parcel.writeByte((byte) 1);
		    parcel.writeLong(dateCreated);
		}
		if (dateLastUpdated == null) {
		    parcel.writeByte((byte) 0);
		} else {
		    parcel.writeByte((byte) 1);
		    parcel.writeLong(dateLastUpdated);
		}
		parcel.writeString(action);
		parcel.writeByte((byte) (isAllowToDownloadNotificationImage == null ? 0 : isAllowToDownloadNotificationImage ? 1 : 2));
	    }
	}
```

Include the following to your App-level build.gradle

```java
...
dependencies {
    ...
    // This line imports the BAKit-Android to your project.
    implementation 'com.github.BoardActive:BAKit-Android:2.0.3'
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
    implementation 'com.google.firebase:firebase-core:21.1.1'
    implementation 'com.google.firebase:firebase-iid:21.1.0'
    implementation 'com.google.firebase:firebase-messaging:23.1.1'
    implementation 'androidx.work:work-runtime:2.7.1'
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
    private Boolean isSilent;

    public static final String NOTIFICATION_KEY = "NOTIFICATION_KEY";
    public static final int NOTIFICATION_BASIC = 0;
    public static final int NOTIFICATION_BIG_PIC = 1;
    public static final int NOTIFICATION_ACTION_BUTTON = 2;
    public static final int NOTIFICATION_BIG_TEXT = 3;
    public static final int NOTIFICATION_INBOX = 4;

    public  NotificationBuilder(Context context, PendingIntent pendingIntent, MessageModel obj, int type,Boolean isSilent) {
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
                    into(300, 300). // Width and height
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
                if(mObj.getAllowToDownloadNotificationImage()) {

                    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
                            .setContentTitle(mObj.getTitle())
                            .setContentText(mObj.getBody())
                            .setAutoCancel(true)
                            .setContentIntent(mPendingIntent)
                            .addAction(0,"Download Image",mPendingIntent)
                            .setSound(defaultSoundUri)
                            .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(mBitmap).bigLargeIcon(null))
                            .setSmallIcon(R.drawable.ba_logo)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                }else
                {
                    notificationBuilder = new NotificationCompat.Builder(mContext, channelId)
                            .setContentTitle(mObj.getTitle())
                            .setContentText(mObj.getBody())
                            .setAutoCancel(true)
                            .setContentIntent(mPendingIntent)
                            .setSound(defaultSoundUri)
                            .setLargeIcon(mBitmap).setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(mBitmap).bigLargeIcon(null))
                            .setSmallIcon(R.drawable.ba_logo)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
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
                                .addAction(0,"Download Image",mPendingIntent)
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
                                .addAction(0,"Download Image",mPendingIntent)
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
                                .addAction(0,"Download Image",mPendingIntent)
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
                                .addAction(0,"Download Image",mPendingIntent)
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
                if(mObj.getAllowToDownloadNotificationImage()) {
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setLargeIcon(mBitmap)
                                    .addAction(0,"Download Image",mPendingIntent)
                                    .setSmallIcon(R.drawable.ba_logo).setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(mBitmap).bigLargeIcon(null))
                                    .setSound(defaultSoundUri)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                }else {
                    notificationBuilder =
                            new NotificationCompat.Builder(mContext, channelId)
                                    .setContentTitle(mObj.getTitle())
                                    .setContentText(mObj.getBody())
                                    .setAutoCancel(true)
                                    .setLargeIcon(mBitmap)
                                    .setSmallIcon(R.drawable.ba_logo).setStyle(new NotificationCompat.BigPictureStyle()
                                            .bigPicture(mBitmap).bigLargeIcon(null))
                                    .setSound(defaultSoundUri)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setContentIntent(mPendingIntent);
                }

                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);

        }
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
      //  if(!isSilent){
            notificationManager.notify(m /* ID of notification */, notificationBuilder.build());

        //  }
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
    public static final String IMAGE_URL = "key.IMAGE_URL";
    public static final String TITLE = "key.TITLE";
    public static final String DESC = "key.DESC";

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


        Log.d(TAG, "on message received data");

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "[BAAdDrop] From: " + remoteMessage.getFrom());
        Log.d(TAG, "notification data");

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
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FCMWorker.class)
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
     * @param remoteMessage FCM notification.
     */
    private void sendNotification(final RemoteMessage remoteMessage) {
        Long currentDateandTime = System.currentTimeMillis();
        MessageModel obj = new MessageModel();
        Log.e("firebase message id",""+remoteMessage.getMessageId().trim());

        Log.e("firebase message", Objects.requireNonNull(remoteMessage.getMessageId().trim()));

        int id = 1;
        obj.setId(id);
        obj.setBaMessageId(remoteMessage.getData().get("baMessageId"));
        obj.setBaNotificationId(remoteMessage.getData().get("baNotificationId"));
        obj.setFirebaseNotificationId(remoteMessage.getMessageId());
        obj.setTitle(remoteMessage.getData().get("title"));
        obj.setBody(remoteMessage.getData().get("body"));
        obj.setImageUrl(remoteMessage.getData().get("imageUrl"));
        obj.setLatitude(remoteMessage.getData().get("latitude"));
        obj.setLongitude(remoteMessage.getData().get("longitude"));
        obj.setMessageData(remoteMessage.getData().get("messageData"));
        obj.setIsTestMessage(remoteMessage.getData().get("isTestMessage"));
        obj.setDateCreated(currentDateandTime);
        obj.setDateLastUpdated(currentDateandTime);
        Boolean isSilent = Boolean.valueOf(remoteMessage.getData().get("isSilent"));
        String action = remoteMessage.getData().get("action");
        String type = remoteMessage.getData().get("type");
        String placeId = remoteMessage.getData().get("placeId");
        Boolean isAllowToDownloadNotificationImage = Boolean.valueOf(remoteMessage.getData().get("isAllowToDownloadNotificationImage"));
        obj.setAllowToDownloadNotificationImage(isAllowToDownloadNotificationImage);
        Log.v(isSilent.toString(),"");
        Log.v(action,"action");
        obj.setAction(action);


        BoardActive mBoardActive = new BoardActive(getApplicationContext());
        mBoardActive.postEvent(new BoardActive.PostEventCallback() {

            @Override
            public void onResponse(Object value) {
                Log.d(TAG, "[BAKitApp] Received Event: " + value.toString());
            }
        }, "received", obj.getBaMessageId(), obj.getBaNotificationId(), obj.getFirebaseNotificationId().trim());

        if(type != null)
        {
            if(type.equals("app_status") && Objects.equals(placeId, "null"))
        {
            if(action != null){
                if(action.equals("Enable")){
                    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
                }
                else
                {
                    SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
                }
            }



        }else if(type.equals("place_update") || type.equals("Campaign") || Objects.equals(placeId, "null")){
           // SharedPreferenceHelper.putString(this, Constants.APP_STATUS, action);
            mBoardActive.setLocationArrayList(null);
            mBoardActive.getLocationList();
        }

        }
        if(!isSilent){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_MESSADE_ID, id);
            intent.putExtra(TITLE,obj.getTitle());
            intent.putExtra(DESC,obj.getBody());
            intent.putExtra("MessageId",obj.getBaMessageId());
            intent.putExtra("NotificationId",obj.getBaNotificationId());
            intent.putExtra("FirebaseMessageId",obj.getFirebaseNotificationId());
            if(obj.getImageUrl() != null){
                intent.putExtra(IMAGE_URL,obj.getImageUrl());

            }
            if(obj.getAllowToDownloadNotificationImage()){
                intent.putExtra("isAllowImage",true);

            }else
            {
                intent.putExtra("isAllowImage",false);

            }
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
            PendingIntent pendingIntent =null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
               //  pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_MUTABLE);
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                         PendingIntent.FLAG_MUTABLE);


//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
                int notificationType = 0;
                new com.boardactive.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }else
            {
                 //pendingIntent = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
                int requestCode = new Random().nextInt();
                pendingIntent = PendingIntent.getActivity(
                        this, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

//        int notificationType = Tools.getSharedPrecerenceInt(this, NotificationBuilder.NOTIFICATION_KEY);
                int notificationType = 0;
                new com.boardactive.addrop.firebase.NotificationBuilder(this,pendingIntent, obj, notificationType,isSilent).execute();
            }
        }


    }

}
```

#### FCM Worker Class

```java

```
public class FCMWorker extends Worker {

    public static final String TAG = FCMWorker.class.getName();

    public FCMWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "[BAAdDrop] Performing long running task in scheduled job");
        // TODO(developer): add long running task here.
        return Result.success();
    }
}
#### Message Model Class

```java
public class MessageModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("baMessageId")
    @Expose
    private String baMessageId;

    @SerializedName("baNotificationId")
    @Expose
    private String baNotificationId;

    @SerializedName("firebaseNotificationId")
    @Expose
    private String firebaseNotificationId;

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

    @SerializedName("action")
    @Expose
    private String action;

    @SerializedName("isAllowToDownloadNotificationImage")
    @Expose
    private Boolean isAllowToDownloadNotificationImage;

    /**
     * No args constructor for use in serialization
     */
    public MessageModel() {
    }

    /**
     * @param id
     * @param baMessageId
     * @param baNotificationId
     * @param firebaseNotificationId
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
            String baMessageId,
            String baNotificationId,
            String firebaseNotificationId,
            String title,
            String body,
            String imageUrl,
            String latitude,
            String longitude,
            String messageData,
            String isTestMessage,
            Boolean isRead,
            Long dateCreated,
            Long dateLastUpdated,
            String action
    ) {
        super();
        this.id = id;
        this.baMessageId = baMessageId;
        this.baNotificationId = baNotificationId;
        this.firebaseNotificationId = firebaseNotificationId;
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
        this.action =action;
    }

    protected MessageModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        baMessageId = in.readString();
        baNotificationId = in.readString();
        firebaseNotificationId = in.readString();
        body = in.readString();
        title = in.readString();
        imageUrl = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        messageData = in.readString();
        isTestMessage = in.readString();
        byte tmpIsRead = in.readByte();
        isRead = tmpIsRead == 0 ? null : tmpIsRead == 1;
        if (in.readByte() == 0) {
            dateCreated = null;
        } else {
            dateCreated = in.readLong();
        }
        if (in.readByte() == 0) {
            dateLastUpdated = null;
        } else {
            dateLastUpdated = in.readLong();
        }
        action = in.readString();
        byte tmpIsAllowToDownloadNotificationImage = in.readByte();
        isAllowToDownloadNotificationImage = tmpIsAllowToDownloadNotificationImage == 0 ? null : tmpIsAllowToDownloadNotificationImage == 1;
    }

    public static final Creator<MessageModel> CREATOR = new Creator<MessageModel>() {
        @Override
        public MessageModel createFromParcel(Parcel in) {
            return new MessageModel(in);
        }

        @Override
        public MessageModel[] newArray(int size) {
            return new MessageModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaMessageId() {
        return baMessageId;
    }

    public void setBaMessageId(String messageId) {
        this.baMessageId = messageId;
    }

    public String getBaNotificationId() {
        return baNotificationId;
    }

    public void setBaNotificationId(String baNotificationId) {
        this.baNotificationId = baNotificationId;
    }

    public String getFirebaseNotificationId() {
        return firebaseNotificationId;
    }

    public void setFirebaseNotificationId(String firebaseNotificationId) {
        this.firebaseNotificationId = firebaseNotificationId;
    }

    public Boolean getAllowToDownloadNotificationImage() {
        return isAllowToDownloadNotificationImage;
    }

    public void setAllowToDownloadNotificationImage(Boolean allowToDownloadNotificationImage) {
        isAllowToDownloadNotificationImage = allowToDownloadNotificationImage;
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

	```

	#### Add to your AndroidManifest.xml

	```xml
	     <service
		    android:name="com.boardactive.addrop.firebase.FCMService"
		    android:exported="false">
		    <intent-filter>
			<action android:name="com.google.firebase.MESSAGING_EVENT" />
		    </intent-filter>
		</service>
	```


	## How to use the BAKit SDK

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(baMessageId);
        parcel.writeString(baNotificationId);
        parcel.writeString(firebaseNotificationId);
        parcel.writeString(body);
        parcel.writeString(title);
        parcel.writeString(imageUrl);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(messageData);
        parcel.writeString(isTestMessage);
        parcel.writeByte((byte) (isRead == null ? 0 : isRead ? 1 : 2));
        if (dateCreated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateCreated);
        }
        if (dateLastUpdated == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(dateLastUpdated);
        }
        parcel.writeString(action);
        parcel.writeByte((byte) (isAllowToDownloadNotificationImage == null ? 0 : isAllowToDownloadNotificationImage ? 1 : 2));
    }
}

```
	### Use BAKit SDK in your Launch Activity

	```java
	import com.boardactive.addrop.BoardActive;

	public class MainActivity extends AppCompatActivity {

```xml
     <service
            android:name="com.boardactive.addrop.firebase.FCMService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```
	    private static final String TAG = "MainActivity";

	    //Add the BoardActive Object
	    private BoardActive mBoardActive;
	    Boolean isAllowImage = false;
	    String imageUrl = "";
	    HashMap<String,Object> updatedCustomAttributes;

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

				//location permission
				mBoardActive.checkLocationPermissions();

				// Initialize BoardActive
				mBoardActive.initialize();

				mBoardActive.setIsForeground(true);
				mBoardActive.StartWorker(getResources().getString(R.string.app_name)+" ");

				// Register the device with BoardActive

				try {
				    mBoardActive.registerDevice(new BoardActive.PostRegisterCallback() {
					@Override
					public void onResponse(Object value) {
					    Log.d(TAG, value.toString());
					    Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setLenient().create();
					    //   Me me = gson.fromJson(value.toString(), Me.class);
					    try {
						JsonParser parser = new JsonParser();
						JsonElement je = parser.parse(value.toString());

						Log.d(TAG, gson.toJson(je));
					    } catch (Exception e) {
						e.printStackTrace();
					    }

					}
				    });
				} catch (Exception e) {
				    e.printStackTrace();
				}
				// update custom attributes stuff

				updatedCustomAttributes = new HashMap<>();
				updatedCustomAttributes.put("braves_fan", true);
				mBoardActive.putCustomAtrributes(new BoardActive.PutMeCallback() {
				    @Override
				    public void onResponse(Object value) {
				    }
				}, updatedCustomAttributes);
					    }
					});

		if (getIntent().getExtras() != null) {
		    isAllowImage = getIntent().getBooleanExtra("isAllowImage", false);
		    if (isAllowImage) {

			imageUrl = getIntent().getStringExtra("key.IMAGE_URL");
			new ImageAsync(this, imageUrl).execute();
			showAlert("Download Image", "Do you want to download this image?", this);

		    } else {
			title = getIntent().getStringExtra("key.TITLE");
			desc = getIntent().getStringExtra("key.DESC");
			imageUrl = getIntent().getStringExtra("key.IMAGE_URL");

		    }

		}
	    }

	    private void showAlert(String title, String message, Context context) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton("Yes",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
				try {
				    saveImage(bitmap, "downloadedImage", context);
				    dialog.dismiss();
				} catch (IOException e) {
				    e.printStackTrace();
				}
			    }
			});
		alertDialog.setNegativeButton("No",
			new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			    }
			});

		alertDialog.show();
	    }

	    public static void saveImage(Bitmap bitmap, @NonNull String name, Context context) throws IOException {
		boolean saved;
		OutputStream fos;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
		    ContentResolver resolver = context.getContentResolver();
		    ContentValues contentValues = new ContentValues();
		    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
		    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
		    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "images");
		    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		    fos = resolver.openOutputStream(imageUri);
		} else {
		    String imagesDir = Environment.getExternalStoragePublicDirectory(
			    Environment.DIRECTORY_DCIM).toString() + File.separator + "images";

		    File file = new File(imagesDir);

		    if (!file.exists()) {
			file.mkdir();
		    }

		    File image = new File(imagesDir, name + ".png");
		    fos = new FileOutputStream(image);

		}
		saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		fos.flush();
		fos.close();
	    }
	}

	    class ImageAsync extends AsyncTask<String, Void, Bitmap> {

		Context mContext;
		String imageUrl;

		ImageAsync(Context context,String imageUrl)
		{
		    this.imageUrl =imageUrl;
		    this.mContext=context;
		}
		@Override
		protected Bitmap doInBackground(String... strings) {
		    try {
			MainActivity.bitmap = Glide.
				with(mContext).
				asBitmap().load(imageUrl).
				into(300, 300). // Width and height
					get();

		    } catch (Exception e) {
			Log.d("TAG", e.toString());
		    }
		    return null;
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
