package com.screenlock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.screenlock.ScreenContent;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/8/12.
 */
public class PasswordContainer extends ScreenContent implements PasswordView.ClickCallback {

    PasswordView mPasswordView;

    public PasswordContainer(Context context) {
        super(context);
        View.inflate(context, R.layout.view_password_container, this);
        mPasswordView = (PasswordView) findViewById(R.id.password_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_tapjoy);
        mPasswordView.setBitmaps(new Bitmap[]{bitmap, bitmap, bitmap,
                bitmap, bitmap, bitmap,
                bitmap, bitmap, bitmap,
                bitmap});
        mPasswordView.setClickCallback(this);
    }

    @Override
    public void onNodeClick(int number) {
        Log.e("onNodeClick", String.valueOf(number));
    }
}
