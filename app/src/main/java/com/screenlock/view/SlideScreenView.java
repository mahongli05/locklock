package com.screenlock.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.screenlock.ScreenContent;
import com.screenlock.ScreenLockHelper;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/8/12.
 */
public class SlideScreenView extends ScreenContent {

    private int mDeviceWidth = 0;
    private int mDevideDeviceWidth = 0;
    private ScreenLockContainer mContainer;

    public SlideScreenView(Context context, ScreenLockContainer container) {
        super(context);
        mContainer = container;
        setBackgroundColor(getResources().getColor(R.color.text_color_blue));
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mDeviceWidth = displayMetrics.widthPixels;
        mDevideDeviceWidth = (mDeviceWidth * 4 / 5);
        setOnTouchListener(mViewTouchListener);
    }

    private final int LOCK_OPEN_OFFSET_VALUE = 50;

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {

        private float firstTouchX = 0;
        private float layoutPrevX = 0;
        private float lastLayoutX = 0;
        private boolean isLockOpen = false;
        private int touchMoveX = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {// 0
                    firstTouchX = event.getX();
                    lastLayoutX = mContainer.getOffsetX();
                    if (firstTouchX <= LOCK_OPEN_OFFSET_VALUE) {
                        isLockOpen = true;
                    }
                }
                break;

                case MotionEvent.ACTION_MOVE: { // 2
                    if (isLockOpen) {
                        touchMoveX = (int) (event.getRawX() - firstTouchX);
                        if (mContainer.getOffsetX() >= 0) {
                            mContainer.setOffsetX(layoutPrevX + touchMoveX);
                            if (mContainer.getOffsetX() < 0) {
                                mContainer.setOffsetX(0);
                            }
                            lastLayoutX = mContainer.getOffsetX();
                        }
                    } else {
                        return false;
                    }
                }
                break;
                case MotionEvent.ACTION_UP: { // 1
                    if (isLockOpen) {
                        mContainer.setOffsetX(lastLayoutX);
                        optimizeForground(lastLayoutX);
                    }
                    isLockOpen = false;
                    firstTouchX = 0;
                    layoutPrevX = 0;
                    touchMoveX = 0;
                    lastLayoutX = 0;
                }
                break;
                default:
                    break;
            }

            return true;
        }
    };

    private void optimizeForground(float forgroundX) {

        if (forgroundX < mDevideDeviceWidth) {
            int startPostion = 0;
            for (startPostion = mDevideDeviceWidth; startPostion >= 0; startPostion--) {
                mContainer.setOffsetX(startPostion);
            }
        } else {
            TranslateAnimation animation = new TranslateAnimation(0, mDevideDeviceWidth, 0, 0);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mContainer.setOffsetX(mDevideDeviceWidth);
                    ScreenLockHelper.getInstance().stopLock(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            startAnimation(animation);
        }
    }
}
