package com.screenlock.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.RelativeLayout;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/6/12.
 */

public class SlideBar extends RelativeLayout {

    private static final String TAG = "SlideBar";
    private static final boolean DEBUG = false;

    private GradientView mGradientView ;
    private int gradientViewStartX;
    private float mEventDownX;
    private float mGradientViewIndicateLeft;
    private OnTriggerListener mOnTriggerListener;
    private VelocityTracker mVelocityTracker = null;
    private int mMinVelocityXToUnlock;
    private int mMinDistanceToUnlock;
    private int mLeftAnimationDuration;
    private int mRightAnimationDuration;
    private ObjectAnimator animLeftMoveAnimator;
    private ObjectAnimator animRightMoveAnimator;

    private static final int MaxDistance = 400;
    private int mMoveDirection;
    private float mMoveStartOffset;
    private float mMoveSpeed;
    private long mMoveStartTime;

    private static final int DIRECTION_NONE = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_RIGHT = 2;

    public interface OnTriggerListener {
        void onTrigger();
    }

    public SlideBar(Context context) {
        this(context, null);
    }
    public SlideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
//        gradientViewStartX = UtilDisplay.dip2px(context, 0) + 8;
        mMinVelocityXToUnlock = 2000;
        mMinDistanceToUnlock = context.getResources().getDimensionPixelOffset(R.dimen.slidebar_unlock_distance);
        mLeftAnimationDuration = 250 ;
        mRightAnimationDuration = 300 ;
        mGradientView = new GradientView(context);
        addView(mGradientView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getActionMasked();
        boolean handled = false;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                if (DEBUG) Log.v(TAG, "*** DOWN ***");
                handleDown(event);
                handled = true;
                mMoveDirection = DIRECTION_NONE;
                break;

            case MotionEvent.ACTION_MOVE:
                if (DEBUG) Log.v(TAG, "*** MOVE ***");
                handleMove(event);
                handled = true;
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                if (DEBUG) Log.v(TAG, "*** UP ***");
                handleUp(event);
                handled = true;
                break;

            case MotionEvent.ACTION_CANCEL:
                if (DEBUG) Log.v(TAG, "*** CANCEL ***");
                handled = true;
                break;

        }
        invalidate();
        return handled ? true : super.onTouchEvent(event);
    }


    private void handleUp(MotionEvent event) {

        Log.v(TAG, "handleUp,mIndicateLeft:" + mGradientViewIndicateLeft);
        //1. if user slide some distance, unlock
        if(mGradientViewIndicateLeft >= mMinDistanceToUnlock){
            unlockSuccess();
            return;
        }
        //2. if user slide very fast, unlock
        if(velocityTrigUnlock()){
            return;
        }
        //otherwise reset the controls
        resetControls();
    }

    /**
     * another way to unlock, if user slide very fast
     */
    private boolean velocityTrigUnlock() {
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000);

        int velocityX = (int) velocityTracker.getXVelocity();

        Log.v(TAG, "velocityX:" + velocityX);

        if(velocityX > mMinVelocityXToUnlock){
            unlockSuccess();
            return true;
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        return false;
    }

    private void unlockSuccess() {
        if (mOnTriggerListener != null) {
            mOnTriggerListener.onTrigger();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animRightMoveAnimator = ObjectAnimator.ofFloat(mGradientView, "x",mGradientView.getX(), MaxDistance)
                    .setDuration(mRightAnimationDuration);
            animRightMoveAnimator.start();
        } else {
            mMoveDirection = DIRECTION_RIGHT;
            mMoveStartOffset = mGradientView.getOffset();
            mMoveSpeed = (MaxDistance - mMoveStartOffset) / (float)mRightAnimationDuration;
            mMoveStartTime = System.currentTimeMillis();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void handleMove(MotionEvent event) {

        mGradientViewIndicateLeft = event.getX() - mEventDownX + gradientViewStartX;
        if(mGradientViewIndicateLeft <= gradientViewStartX){
            mGradientViewIndicateLeft = gradientViewStartX;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mGradientView.setX(mGradientViewIndicateLeft);
        } else {
            mGradientView.updateOffset(mGradientViewIndicateLeft);
        }
    }

    private void handleDown(MotionEvent event) {
        mEventDownX = event.getX();
        mGradientView.stopAnimatorAndChangeColor();

    }

    public void setOnTriggerListener(OnTriggerListener listener) {
        mOnTriggerListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void resetControls(){

        mGradientView.startAnimator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animLeftMoveAnimator = ObjectAnimator.ofFloat(mGradientView, "x",
                    mGradientView.getX() ,gradientViewStartX).setDuration(mLeftAnimationDuration);
            animLeftMoveAnimator.start();
        } else {
            mMoveDirection = DIRECTION_LEFT;
            mMoveStartOffset = mGradientView.getOffset();
            mMoveSpeed = (mMoveStartOffset - gradientViewStartX) / (float)mLeftAnimationDuration;
            mMoveStartTime = System.currentTimeMillis();
        }
    }

    public void updateView() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            if (mMoveDirection == DIRECTION_LEFT) {
                float offset = mGradientView.getOffset();
                if (offset > gradientViewStartX) {
                    int moveOffset = (int)(mMoveSpeed * (System.currentTimeMillis() - mMoveStartTime));
                    offset = mMoveStartOffset - moveOffset;
                }
                if (offset <= gradientViewStartX) {
                    mMoveDirection = DIRECTION_NONE;
                    mGradientView.updateOffset(offset);
                }
            } else if (mMoveDirection == DIRECTION_RIGHT) {
                float offset = mGradientView.getOffset();
                if (offset < MaxDistance) {
                    int moveOffset = (int)(mMoveSpeed * (System.currentTimeMillis() - mMoveStartTime));
                    offset = mMoveStartOffset + moveOffset;
                }
                if (offset >= MaxDistance) {
                    mMoveDirection = DIRECTION_NONE;
                    mGradientView.updateOffset(offset);
                }
            }
            mGradientView.updateView();
        }
    }
}

