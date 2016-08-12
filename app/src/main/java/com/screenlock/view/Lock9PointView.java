package com.screenlock.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.screenlock.ScreenContent;
import com.screenlock.ScreenLockHelper;
import com.screenlock.ScreenLockStore;
import com.takwolf.android.lock9.Lock9View;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/8/12.
 */
public class Lock9PointView extends ScreenContent {

    private Lock9View mLock9View;

    public Lock9PointView(Context context) {
        super(context);
        inflate(context, R.layout.view_9_point, this);
        mLock9View = (Lock9View) findViewById(R.id.lock_9_view);
        mLock9View.setCallBack(new Lock9View.CallBack() {
            @Override
            public void onFinish(String password) {
                Log.e("onFinish", password);
                String savedPassword = ScreenLockStore.getScreenLockPassword(getContext());
                if (TextUtils.isEmpty(savedPassword) && !TextUtils.isEmpty(password)) {
                    ScreenLockStore.saveScreenLockPassword(getContext(), password);
                } else if (TextUtils.equals(password, savedPassword)) {
                    ScreenLockHelper.getInstance().stopLock(true);
                }
            }
        });
    }
}
