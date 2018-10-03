<img src="https://avatars0.githubusercontent.com/u/38864287?s=200&v=4" width="96" height="96"/>

# BAKit-android
BoardActive\'s Android SDK

### Retain and engage mobile users
Connect with customers using BoardActive's proprietary location-based marketing technology.

## Installation

BoardActive for Android supports APK 15 and greater. 

### AAR Library
Download and add the BoardActive AAR into your project.
Building with Android Studio is required.

Download and include [bakit-android.aar](https://github.com/BoardActive/BAKit-android/tree/master/aar) and import into you android project. 

### Dependencies
You app must include Google Play Services
[Learn More] Our [Google Play Services Overview](https://developers.google.com/android/guides/overview) contains full setup and initialisation instructions.

```javascript
// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.1.4'
        //Add the following to you Top-Level build.gradle
        classpath 'com.google.gms:google-services:4.1.0'
    }
}

Include the following to you App-level build.gradle
dependencies {
    implementation project(':bakit-sdk')
}
```

### Include Location permissions to your android project
In you MainActivity.java include the following: 

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
### Setup and Launch BAKit Activity
Include the following to your MainActivity.java
```javascript
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent addrop_boot = new Intent(MainActivity.this, AdDropBootActivity.class);
        addrop_boot.putExtra(AdDropBootActivity.APP_ID, "[Add Your BA App Id]");
        startActivity(addrop_boot);

        // Check if the user revoked runtime permissions.
        if (!checkPermissions()) {
            requestPermissions();
        }
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
