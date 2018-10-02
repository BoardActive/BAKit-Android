package com.boardactive.sdk.adapters;
import android.support.annotation.Nullable;

import java.lang.reflect.Field;

public class BuildConfigHelper {

    private static final String BUILD_CONFIG = "com.boardactive.sdk.adapters.BuildConfigHelper";

    public static final String APP_ID = (String) getBuildConfigValue("APP_ID");
    public static final String ENVIRONMENT = (String) getBuildConfigValue("ENVIRONMENT");


    private static boolean getDebug() {
        Object o = getBuildConfigValue("DEBUG");
        if (o != null && o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return false;
        }
    }

    private static int getVersionCode() {
        Object o = getBuildConfigValue("VERSION_CODE");
        if (o != null && o instanceof Integer) {
            return (Integer) o;
        } else {
            return Integer.MIN_VALUE;
        }
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
