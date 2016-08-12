package com.screenlock;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MHL on 2016/7/12.
 */
public class ScreenLockStore {

    private static final String FILE_NAME = "preference_store";

    private static final String PREF_SCREEN_ADS = "screen_ads";
    private static final String PREF_SCREEN_ADS_REQUEST_TIME = "screen_ads_request_time";
    private static final String PREF_SCREEN_PASSWORD = "screen_lock_password";

    public static String getScreenLockPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_SCREEN_PASSWORD, null);
    }

    public static void saveScreenLockPassword(Context context, String password) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_SCREEN_PASSWORD, password).commit();
    }

    public static long getScreenAdsRequestTime(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getLong(PREF_SCREEN_ADS_REQUEST_TIME, 0);
    }

    public static void saveScreenAdsRequestTime(Context context, long time) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putLong(PREF_SCREEN_ADS_REQUEST_TIME, time).commit();
    }

    public static String getScreenAds(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_SCREEN_ADS, null);
    }

    public static void saveScreenAds(Context context, String ads) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_SCREEN_ADS, ads).commit();
    }
}
