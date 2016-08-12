package com.screenlock;

import android.content.Context;

import com.screenlock.view.Lock9PointView;
import com.screenlock.view.PasswordContainer;
import com.screenlock.view.PasswordView;
import com.screenlock.view.ScreenLockContainer;
import com.screenlock.view.SlideBarUnlockView;
import com.screenlock.view.SlideScreenView;

/**
 * Created by MHL on 2016/8/4.
 */
public class ScreenContentFactory {

    public static ScreenContent createScreenContent(Context context, ScreenLockContainer container) {
//        return new ScreenLockView(context);
//        return new SlideBarUnlockView(context);
//        return new SlideScreenView(context, container);
//        return new Lock9PointView(context);
        return new PasswordContainer(context);
    }
}
