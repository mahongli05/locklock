package com.screenlock;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.common.util.LogHelper;
import com.common.util.PackageUtil;

/**
 * Created by MHL on 2016/8/2.
 */
public class ScreenLockActivity extends Activity implements ScreenLockHelper.ScreenLockListener {

    private static final String TAG = "ScreenLockActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_NoDisplay);
        ScreenLockHelper.getInstance().stopLock(true);
        ScreenLockHelper.getInstance().setScreenLockListener(this);
        if (PackageUtil.isFloatWindowAllowed(this)) {
            ScreenLockHelper.getInstance().startLock(getApplicationContext());
            LogHelper.d("permission", "true");
            finish();
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            ScreenLockHelper.getInstance().startLock(this);
            LogHelper.d("permission", "false");
        }
    }

    @Override
    protected void onDestroy() {
        ScreenLockHelper.getInstance().setScreenLockListener(null);
        super.onDestroy();
        LogHelper.d(TAG, "onDestroy");
    }

    //使back键，音量加减键失效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return disableKeycode(keyCode, event);
    }

    private boolean disableKeycode(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        switch (key) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLockStart() {
        LogHelper.d(TAG, "onLockStart");
    }

    @Override
    public void onLockStop() {
        LogHelper.d(TAG, "onLockStop");
        finish();
    }
}
