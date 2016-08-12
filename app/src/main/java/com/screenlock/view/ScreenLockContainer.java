package com.screenlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.FrameLayout;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/7/5.
 */
public class ScreenLockContainer extends FrameLayout {

    public ScreenLockContainer(Context context) {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.text_color_yellow));
    }

    private float mOffsetX;

    public void setOffsetX(float offsetX) {
        mOffsetX = offsetX;
        invalidate();
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, 0);
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, 0);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
