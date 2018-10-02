package com.boardactive.sdk.adapters;
import android.support.annotation.Nullable;

import com.boardactive.sdk.BuildConfig;

import java.lang.reflect.Field;

public class BuildConfigReader {

    private static String BUILD_CONFIG;

    public static final String APP_ID = (String) getBuildConfigValue("APP_ID");
    public static final String ENVIRONMENT = (String) getBuildConfigValue("ENVIRONMENT");
    public static  void setPackage (String packageName) {
        BUILD_CONFIG = packageName;
    }


    @Nullable
    private static Object getBuildConfigValue(String fieldName) {
        try {
            Class c = Class.forName(BUILD_CONFIG);
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
