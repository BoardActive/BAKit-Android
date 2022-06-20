package com.boardactive.bakit.Tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.boardactive.bakit.models.Coordinate;
import com.boardactive.bakit.models.LocationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class
SharedPreferenceHelper {

    private static final String TAG = SharedPreferenceHelper.class.getSimpleName();

    public static boolean getBoolean(Context context, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Log.d(TAG, "putBoolean: [" + key + ":" + value + "]");
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public static String getString(Context context, String key,
                                   String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        Log.d(TAG, "putString: [" + key + ":" + value + "]");
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void putInt(Context context, String key, int value) {
        Log.d(TAG, "putInt: [" + key + ":" + value + "]");
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void clear(Context context){
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Log.d(TAG, "clear all data");
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public static void clearData(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Log.d(TAG, "clear data: [" + key + "]");
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }


    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    public static void putLong(Context context, String key, long value) {
        Log.d(TAG, "putInt: [" + key + ":" + value + "]");
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(key, value);
        edit.commit();
    }


    public static void putArrayList(Context context, ArrayList<Coordinate> locationModelArrayList) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(locationModelArrayList);

        edit.putString("locationList",json);
        edit.commit();
    }

    public static ArrayList<Coordinate> getArrayList(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        Type type = new TypeToken<ArrayList<Coordinate>>() {}.getType();
        return gson.fromJson(json, type);
    }
}