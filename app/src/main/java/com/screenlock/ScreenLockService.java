package com.screenlock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

/**
 * Created by MHL on 2016/7/9.
 */
public class ScreenLockService extends Service {

    private ScreenBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        init(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void init(Service service) {
        mReceiver = new ScreenBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mIntentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(service);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(this);
        restartIfNeeded();
        super.onDestroy();
    }

    private void restartIfNeeded() {
        updateAlarm(this);
    }

    private static void updateAlarm(Context context) {
        try {
            Intent intent = new Intent(ScreenLockReceiver.SCREEN_LOCK_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20L * 1000, pendingIntent);
        } catch (Exception e) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleReceiverIntent(context, intent);
        }
    }

    private void handleReceiverIntent(Context context, Intent intent) {

        if (context != null && intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
//                ScreenLockHelper.getInstance().startLock(getApplicationContext());
                Intent lockIntent = new Intent(this, ScreenLockActivity.class);
                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lockIntent);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                ScreenLockHelper.getInstance().stopLock(true);
                ScreenLockHelper.getInstance().prepareScreenAds(getApplicationContext());
            } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)
                        || TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                    ScreenLockHelper.getInstance().stopLock(true);
                }
            }
        }
    }

    private void registerReceiver(Context context) {
        try {
            context.getApplicationContext().registerReceiver(mReceiver, mIntentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterReceiver(Context context) {
        try {
            context.getApplicationContext().unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
