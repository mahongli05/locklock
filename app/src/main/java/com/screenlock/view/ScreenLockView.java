package com.screenlock.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.util.LogHelper;
import com.screenlock.ScreenContent;
import com.screenlock.ScreenLockHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/6/13.
 */
public class ScreenLockView extends ScreenContent {

    private static final String TAG = "ScreenLockView";

    private static final String TIME_2_FORMAT = "MMMM d EEEE";
    private static final String TIME_1_FORMAT = "%s:%02d";

    private TextView mTimeText1;
    private TextView mTimeText2;
    private TextView mLeftText;
    private TextView mRightText;

    private ImageView mUnLockButton;

    private float mUnlockDistance;

    private boolean mUnlockMode;
    private boolean mAnimationRun;
    private float mXStart;
    private float mYStart;

    public ScreenLockView(Context context) {
        super(context);
        setupView(context);
    }

    private void setupView(Context context) {
        inflate(context, R.layout.view_screen_lock, this);

        mTimeText1 = (TextView) findViewById(R.id.time);
        mTimeText2 = (TextView) findViewById(R.id.day);
        mUnLockButton = (ImageView) findViewById(R.id.scroll_btn);
        mLeftText = (TextView) findViewById(R.id.left_text);
        mRightText = (TextView) findViewById(R.id.right_text);
        mUnlockDistance = getResources().getDimensionPixelOffset(R.dimen.unlock_distance);

        updateTime();
    }

    public void updateTime() {
        Calendar calendar = Calendar.getInstance();
        Date today = new Date(System.currentTimeMillis());
        calendar.setTime(today);
        mTimeText1.setText(String.format(TIME_1_FORMAT,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        SimpleDateFormat format = new SimpleDateFormat(TIME_2_FORMAT);
        mTimeText2.setText(format.format(today));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean result = false;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {// 0
                mXStart = event.getX();
                mYStart = event.getY();
                float left = mUnLockButton.getLeft();
                float right = mUnLockButton.getRight();
                float top = mUnLockButton.getTop();
                float bottom = mUnLockButton.getBottom();
                if (mXStart > left && mXStart < right
                        && mYStart > top && mYStart < bottom && !mAnimationRun) {
                    mUnlockMode = true;
                    mUnLockButton.setPressed(true);
                    result = true;
                }
                LogHelper.d(TAG, String.format("down [%s, %s] [%s, %s, %s, %s]", mXStart, mYStart, left, top, right, bottom));
            }
            break;

            case MotionEvent.ACTION_MOVE: { // 2
                if (mUnlockMode) {
                    float xOffset = event.getX() - mXStart;
                    float yOffset = event.getY() - mYStart;
                    mUnLockButton.setTranslationX(xOffset);
                    mUnLockButton.setTranslationY(yOffset);
                    invalidate();
                    tryUnlock();
                    LogHelper.d(TAG, String.format("move [%s, %s]", xOffset, yOffset));
                }
                result = true;
            }
            break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: { // 1
                mUnlockMode = false;
                mUnLockButton.setPressed(false);
                animateBack();
                result = true;
            }
            break;

            default:
                break;
        }

        return result;
    }

    private void animateBack() {
        final float translationX = mUnLockButton.getTranslationX();
        final float translationY = mUnLockButton.getTranslationY();
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float tx = translationX * value;
                float ty = translationY * value;
                mUnLockButton.setTranslationX(tx);
                mUnLockButton.setTranslationY(ty);
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationRun = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationRun = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mAnimationRun = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mAnimationRun = true;
            }
        });
        animator.start();
    }

    private boolean tryUnlock() {
        float translationX = mUnLockButton.getTranslationX();
        float translationY = mUnLockButton.getTranslationY();
        RectF unlockButtonRect = getViewRect(mUnLockButton, translationX, translationY);
        RectF leftUnLockRect = getViewRect(mLeftText, 0, 0);
        RectF rightUnlockRect = getViewRect(mRightText, 0, 0);
        if (getCenterDistance(unlockButtonRect, leftUnLockRect) < mUnlockDistance) {
            onUnlockLeft();
            mUnLockButton.setPressed(false);
            mUnlockMode = false;
            return true;
        } else if (getCenterDistance(unlockButtonRect, rightUnlockRect) < mUnlockDistance) {
            onUnlockRight();
            mUnLockButton.setPressed(false);
            mUnlockMode = false;
            return true;
        }
        return false;
    }

    private RectF getViewRect(View view, float offsetX, float offsetY) {
        return new RectF(view.getLeft() + offsetX, view.getTop() + offsetY,
                view.getRight() + offsetX, view.getBottom() + offsetY);
    }

    private float getCenterDistance(RectF rectA, RectF rectB) {
        float x = rectA.centerX() - rectB.centerX();
        float y = rectA.centerY() - rectB.centerY();
        return (float)Math.sqrt(x * x + y * y);
    }

    protected void onUnlockLeft() {
        ScreenLockHelper.getInstance().stopLock(true);
        if (mScreenLockCallback != null) {
            mScreenLockCallback.onUnlocked();
        }
    }

    protected void onUnlockRight() {
        ScreenLockHelper.getInstance().stopLock(true);
        if (mScreenLockCallback != null) {
            mScreenLockCallback.onUnlocked();
        }
    }

    @Override
    public void onLockStart() {
        mUpdateHandler.removeMessages(MSG_UPDATE);
        mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
    }

    @Override
    public void onLockStop() {
        mUpdateHandler.removeMessages(MSG_UPDATE);
    }

    private static final int MSG_UPDATE = 0x1;
    private static final long UPDATE_DURATION = 30L * 1000;

    private Handler mUpdateHandler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                updateViews();
                sendEmptyMessageDelayed(MSG_UPDATE, UPDATE_DURATION);
            }
        }
    };

    private void updateViews() {
        updateTime();
    }
}
