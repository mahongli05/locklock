package com.screenlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MHL on 2016/7/14.
 */
public class ScreenLockReceiver extends BroadcastReceiver {

    public static final String SCREEN_LOCK_ACTION = "com.ma.locklock.screenlock";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, ScreenLockService.class);
        context.startService(service);
    }
}
