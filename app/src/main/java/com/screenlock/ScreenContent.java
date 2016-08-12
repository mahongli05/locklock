package com.screenlock;

import android.content.Context;
import android.widget.FrameLayout;

/**
 * Created by MHL on 2016/8/4.
 */
public class ScreenContent extends FrameLayout {

    protected ScreenLockCallback mScreenLockCallback;

    public interface ScreenLockCallback {
        void onUnlocked();
    }

    public ScreenContent(Context context) {
        super(context);
    }

    public void setScreenLockCallback(ScreenLockCallback callback) {
        mScreenLockCallback = callback;
    }

    public void onLockStart() {

    }

    public void onLockStop() {

    }
}
