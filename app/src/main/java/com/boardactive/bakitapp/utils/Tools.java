package com.boardactive.bakitapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Tools {

//    public static void setSystemBarColor(AppCompatActivity act) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = act.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
//        }
//    }

//    public static void setSystemBarColor(AppCompatActivity act, @ColorRes int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = act.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(act.getResources().getColor(color));
//        }
//    }

//    public static void setSystemBarColorInt(AppCompatActivity act, @ColorInt int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = act.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(color);
//        }
//    }

//    public static void setSystemBarColorDialog(Context act, Dialog dialog, @ColorRes int color) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = dialog.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(act.getResources().getColor(color));
//        }
//    }

//    public static void setSystemBarLight(AppCompatActivity act) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            View view = act.findViewById(android.R.id.content);
//            int flags = view.getSystemUiVisibility();
//            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            view.setSystemUiVisibility(flags);
//        }
//    }

//    public static void setSystemBarLightDialog(Dialog dialog) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            View view = dialog.findViewById(android.R.id.content);
//            int flags = view.getSystemUiVisibility();
//            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
//            view.setSystemUiVisibility(flags);
//        }
//    }

//    public static void clearSystemBarLight(AppCompatActivity act) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Window window = act.getWindow();
//            window.setStatusBarColor(ContextCompat.getColor(act, R.color.colorPrimaryDark));
//        }
//    }

    /**
     * Making notification bar transparent
     */
//    public static void setSystemBarTransparent(AppCompatActivity act) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = act.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//        }
//    }

//    public static void displayImageOriginal(Context ctx, ImageView img, @DrawableRes int drawable) {
//        try {
//            Glide.with(ctx).load(drawable)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(img);
//        } catch (Exception e) {
//        }
//    }

//    public static void displayImageRound(final Context ctx, final ImageView img, @DrawableRes int drawable) {
//        try {
//            Glide.with(ctx)
//                    .load(drawable)
//                    .asBitmap()
//                    .centerCrop()
//                    .into(new BitmapImageViewTarget(img) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    img.setImageDrawable(circularBitmapDrawable);
//                }
//            });
//        } catch (Exception e) {
//        }
//    }

//    public static void displayImageOriginal(Context ctx, ImageView img, String url) {
//        try {
//            Glide.with(ctx).load(url)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(img);
//        } catch (Exception e) {
//        }
//    }

//    public static String getFormattedDateShort(Long dateTime) {
//        SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy");
//        return newFormat.format(new Date(dateTime));
//    }

    public static String getFormattedDateSimple(Long dateTime) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        df.setTimeZone(tz);
        String nowAsISO = df.format(dateTime);
        return nowAsISO;
//        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        return newFormat.format(new Date(dateTime));
    }

    public static String getFormattedDateSimple(String dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return newFormat.format(new Date(dateTime));
    }

    public static Date getFormattedDate(String dateTime) {

        SimpleDateFormat newFormat = new SimpleDateFormat("mm/dd/yyyy");
        try {
            Date date = newFormat.parse(dateTime);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //    public static String getFormattedDateEvent(Long dateTime) {
//        SimpleDateFormat newFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
//        return newFormat.format(new Date(dateTime));
//    }

//    public static String getFormattedTimeEvent(Long time) {
//        SimpleDateFormat newFormat = new SimpleDateFormat("h:mm a");
//        return newFormat.format(new Date(time));
//    }

//    public static String getEmailFromName(String name) {
//        if (name != null && !name.equals("")) {
//            String email = name.replaceAll(" ", ".").toLowerCase().concat("@mail.com");
//            return email;
//        }
//        return name;
//    }

//    public static int dpToPx(Context c, int dp) {
//        Resources r = c.getResources();
//        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
//    }

//    public static GoogleMap configActivityMaps(GoogleMap googleMap) {
//        // set map type
//        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        // Enable / Disable zooming controls
//        googleMap.getUiSettings().setZoomControlsEnabled(false);
//
//        // Enable / Disable Compass icon
//        googleMap.getUiSettings().setCompassEnabled(true);
//        // Enable / Disable Rotate gesture
//        googleMap.getUiSettings().setRotateGesturesEnabled(true);
//        // Enable / Disable zooming functionality
//        googleMap.getUiSettings().setZoomGesturesEnabled(true);
//
//        googleMap.getUiSettings().setScrollGesturesEnabled(true);
//        googleMap.getUiSettings().setMapToolbarEnabled(true);
//
//        return googleMap;
//    }

//    public static void copyToClipboard(Context context, String data) {
//        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("clipboard", data);
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
//    }

//    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
//        nested.post(new Runnable() {
//            @Override
//            public void run() {
//                nested.scrollTo(500, targetView.getBottom());
//            }
//        });
//    }

//    public static int dip2px(Context context, float dpValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
//    }

//    public static int px2dip(Context context, float pxValue) {
//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
//    }

//    public static boolean toggleArrow(View view) {
//        if (view.getRotation() == 0) {
//            view.animate().setDuration(200).rotation(180);
//            return true;
//        } else {
//            view.animate().setDuration(200).rotation(0);
//            return false;
//        }
//    }

//    public static boolean toggleArrow(boolean show, View view) {
//        return toggleArrow(show, view, true);
//    }

//    public static boolean toggleArrow(boolean show, View view, boolean delay) {
//        if (show) {
//            view.animate().setDuration(delay ? 200 : 0).rotation(180);
//            return true;
//        } else {
//            view.animate().setDuration(delay ? 200 : 0).rotation(0);
//            return false;
//        }
//    }

//    public static void changeNavigateionIconColor(Toolbar toolbar, @ColorInt int color) {
//        Drawable drawable = toolbar.getNavigationIcon();
//        drawable.mutate();
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//    }

//    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable == null) continue;
//            drawable.mutate();
//            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//        }
//    }

//    public static void changeOverflowMenuIconColor(Toolbar toolbar, @ColorInt int color) {
//        try {
//            Drawable drawable = toolbar.getOverflowIcon();
//            drawable.mutate();
//            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
//        } catch (Exception e) {
//        }
//    }

//    public static int getScreenWidth() {
//        return Resources.getSystem().getDisplayMetrics().widthPixels;
//    }

//    public static int getScreenHeight() {
//        return Resources.getSystem().getDisplayMetrics().heightPixels;
//    }

//    public static String toCamelCase(String input) {
//        input = input.toLowerCase();
//        StringBuilder titleCase = new StringBuilder();
//        boolean nextTitleCase = true;
//
//        for (char c : input.toCharArray()) {
//            if (Character.isSpaceChar(c)) {
//                nextTitleCase = true;
//            } else if (nextTitleCase) {
//                c = Character.toTitleCase(c);
//                nextTitleCase = false;
//            }
//
//            titleCase.append(c);
//        }
//
//        return titleCase.toString();
//    }


    /**
     * For device info parameters
     */
//    public static String getDeviceName() {
//        String manufacturer = Build.MANUFACTURER;
//        String model = Build.MODEL;
//        if (model.startsWith(manufacturer)) {
//            return model;
//        } else {
//            return manufacturer + " " + model;
//        }
//    }

//    public static String getAndroidVersion() {
//        return Build.VERSION.RELEASE + "";
//    }

//    public static int getVersionCode(Context ctx) {
//        try {
//            PackageManager manager = ctx.getPackageManager();
//            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
//            return info.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            return -1;
//        }
//    }


//    public static String getDeviceID(Context context) {
//        String deviceID = Build.SERIAL;
//        if (deviceID == null || deviceID.trim().isEmpty() || deviceID.equals("unknown")) {
//            try {
//                deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//            } catch (Exception e) {
//            }
//        }
//        return deviceID;
//    }

//    public static String getFormattedDateOnly(Long dateTime) {
//        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yy");
//        return newFormat.format(new Date(dateTime));
//    }

//    public static void directLinkToBrowser(AppCompatActivity activity, String url) {
//        try {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            activity.startActivity(intent);
//        } catch (Exception e) {
//            Toast.makeText(activity, "Ops, Cannot open url", Toast.LENGTH_LONG).show();
//        }
//    }

//    public static void setSharedPreferenceString(Context context, String key, String value){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(key, value);
//        editor.commit();
//    }

//    public static void setSharedPreferenceBoolean(Context context, String key, Boolean value){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean(key, value);
//        editor.commit();
//    }

//    public static void setSharedPreferenceInt(Context context, String key, int value){
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt(key, value);
//        editor.commit();
//    }

//    public static String getSharedPrecerenceString(Context context, String name) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        final String value = settings.getString(name,"");
//        return value;
//    }

//    public static Boolean getSharedPrecerenceBoolean(Context context, String key) {
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
//        final Boolean value = settings.getBoolean(key, false);
//        return value;
//    }

    public static int getSharedPrecerenceInt(Context context, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        final Integer value = settings.getInt(key, 0);
        return value;
    }

}
