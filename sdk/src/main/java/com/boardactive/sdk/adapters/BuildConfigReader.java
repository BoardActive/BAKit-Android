package com.boardactive.sdk.adapters;

import android.util.Log;

import java.lang.reflect.Field;

//This class is used to read the properties set in the Client's build.gradle / gradle.properties
public class BuildConfigReader {

    private static String BUILD_CONFIG;
    private static String TAG="BuildConfigReader";

    public static void setPackage (String packageName) {
        BUILD_CONFIG = packageName;
    }

    public static Object getBuildConfigValue( String fieldName) {
        try {
            Class<?> clazz = Class.forName(BUILD_CONFIG + ".BuildConfig");
            Field field = clazz.getField(fieldName);
            return field.get(null);
        } catch (ClassNotFoundException e) {
            Log.w(TAG, "Error setting build config values: Class not found");
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.w(TAG, "Error setting build config values: No such field");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.w(TAG, "Error setting build config values: Illegal Access");
            e.printStackTrace();
        }
        return null;
    }
}
