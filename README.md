<img src="https://avatars0.githubusercontent.com/u/38864287?s=200&v=4" width="96" height="96"/>

# BAKit-android
BoardActive's Android SDK

### Retain and engage mobile users
Connect with customers using BoardActive's proprietary location-based marketing technology.

## Installation

BoardActive for Android supports APK 15 and greater. 

### SDK
Download and add the BoardActive SDK into your project with JitPack repository.
Building with Android Studio is required.

Download and include [bakit-android.aar](https://github.com/BoardActive/BAKit-android/tree/master/aar) and import into you android project. 

### Dependencies
You app must include Google Play Services.  Our [Google Play Services Overview](https://developers.google.com/android/guides/overview) contains full setup and initialisation instructions.

We use JitPack as a repository service, you will add a few lines to the gradle files to import our SDK into your project. (Instructions for Maven, sbt, and leiningen available upon request)

```javascript
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.4'
        //Add the following to your Top-Level build.gradle
        classpath 'com.google.gms:google-services:4.1.0'
    }
}
// Add JitPack repository to top level build.gradle
allprojects {
    repositories {
        google()
        jcenter()
        //Add the code block below for JitPack
        maven {
            url "https://jitpack.io"
            credentials { username authToken }
        }
    }
}
```

Include the following to your App-level build.gradle
```javascript
android {
    compileSdkVersion 27
    ...
    defaultConfig {
        ...
        // Add these two lines to reference what you will set in gradle.properties
        buildConfigField "String", "APP_ID", app_id
        buildConfigField "String", "ENVIRONMENT", environment
        ...
    }
...
...
dependencies {
    ...
    // This line imports the BoardActive Android SDK to your project, it acts as if you added our source code to your project
    implementation 'com.github.BoardActive:BAKit-Android:6b2d5b1c62'
    ...
}
```
Include your Advertiser ID (aka App_ID) and environment to gradle.properties:
```javascript
authToken=jp_9b9qh8q80k35hirka5j7vndfqp // This is used for JitPack 
app_id="330" //Put your app_id here
environment="dev" //Set this to dev when you are testing your app, this avoids test data being pushed to our Production API
//Switch it to prod when ready to switch our SDK to production.
```

### Include Location permissions to your android project
In your MainActivity.java include the following: 

```javascript
Global Variables: 
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

OnCreate() check for location permissions if not prompt user for locationn permissions (the collowing code snippet will accomplish this request.) 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }

Add the following functions to your MainActivity.java:
    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(com.boardactive.sdk.R.id.activity_main),
                    com.boardactive.sdk.R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(com.boardactive.sdk.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
```

## How to use this SDK
### Setup and Launch BAKit JobService with our Transparent Activity
Our Transparent Activity needs to be called to start our location service
Include the following to your MainActivity.java
```javascript
import com.boardactive.sdk.adapters.BuildConfigReader;
import com.boardactive.sdk.ui.AdDropMainActivity;
import com.boardactive.sdk.ui.TransparentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }
        
        // In your first Activity, send our gradle.properties Reader your App's package name 
        BuildConfigReader.setPackage(getApplicationContext().getPackageName());
        
        // This will launch our Transparent activity which starts our service, the activity will immediately be destroyed and 
        // user will remain on whichever page they were on before the intent was called
        Intent addrop_boot = new Intent(MainActivity.this, TransparentActivity.class);
        startActivity(addrop_boot);
        
    }
```


###  Launch BAKit Activity - AdDrops List and Favorites Views
Include the following to your MainActivity.java
```javascript
import com.boardactive.sdk.adapters.BuildConfigReader;
import com.boardactive.sdk.ui.AdDropMainActivity;
import com.boardactive.sdk.ui.TransparentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }
        
        // Make sure packageName is set before any SDK code is called, we need packageName to send/receive HTTP requests
        BuildConfigReader.setPackage(getApplicationContext().getPackageName());
        
        Intent addrop_activity = new Intent(MainActivity.this, AdDropMainActivity.class);
        startActivity(addrop_activity);

        
    }
```

## Example app
There is an example app provided [here](https://github.com/BoardActive/BAKit-android/tree/master/Example) for Android.

## Setup and Configuration

* [TODO] Our [installation guide](https://developers.boardactive.com) contains full setup and initialisation instructions.
* [TODO] Read ["Configuring BoardActive for Android"](https://developers.boardactive.com).
* [TODO] Read our guide on [Push Notifications](https://developers.boardactive.com).
* Please contact us on [BoardActive](https://boardactive.com) with any questions you may have, we're only a click away!

## Customer Support

👋 [TODO] Contact us with any issues at our [BoardActive Developer Hub available here](https://developers.boardactive.com). If you bump into any problems or need more support, just start a conversation using Intercom there and it will be immediately routed to our Customer Support Engineers.

## Cordova/Phonegap Support
[TODO] Looking for Cordova/Phonegap support? We have a [Cordova Plugin](https://github.com/BoardActive/BAKit-cordova) for BoardActive 🎉

## What about X, Y, or Z?

BoardActive for Android has support for all these things. For full details please read our [documentation](https://developers.boardactive.com).
