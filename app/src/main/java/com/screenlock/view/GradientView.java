package com.screenlock.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/6/12.
 */

public class GradientView extends View {

    private static final  String  TAG = "GradientView";
    private static final boolean DEBUG = false;
    private float 	mIndex = 0;
    private Shader mShader;
    private int 	mTextSize;
    private static final int mUpdateStep = 15;
    private static final int mMaxWidth = 40 * mUpdateStep; // 26*25
    private static final int mMinWidth = 6 * mUpdateStep;  // 5*25
    int 			mDefaultColor;
    int             mSlideColor;
    private ValueAnimator animator;
    private int mWidth,mHeight;
    private String mStringToShow;
    private Paint mTextPaint;
    private float mTextHeight;
    private float mTextWidth;

    private Drawable mSlideIcon;
    private int mSlideIconHeight;
    private int mSlideIconWidth;
    private int mSlideIconMarginleft;

    private static final int mSlideIconOffSetTop = 2;

    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    private float mDrawOffset;

    public GradientView(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public GradientView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mStringToShow = "Slide to unlock" ;
        mTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_big);
        mDefaultColor = getResources().getColor(R.color.slide_text_color);
        mSlideColor = Color.WHITE;
        mSlideIcon = getResources().getDrawable(R.mipmap.ic_slide_arrow);
        mSlideIconHeight = ((BitmapDrawable) mSlideIcon).getBitmap().getHeight();
        mSlideIconWidth = ((BitmapDrawable) mSlideIcon).getBitmap().getWidth();
        mSlideIconMarginleft = getResources().getDimensionPixelOffset(R.dimen.slide_margin_left);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mIndex = Float.parseFloat(animation.getAnimatedValue().toString());
                    // RadialGradient SweepGradient
                    mShader = new LinearGradient(mIndex - 20 * mUpdateStep, 100,
                            mIndex, 100, new int[] { mDefaultColor, mDefaultColor, mDefaultColor,mSlideColor,
                            mSlideColor, mDefaultColor, mDefaultColor, mDefaultColor }, null,
                            Shader.TileMode.CLAMP);

                    postInvalidate();
                }
            };

            animator = ValueAnimator.ofFloat(mMinWidth,mMaxWidth);
            animator.setDuration(1800);
            animator.addUpdateListener(mAnimatorUpdateListener);
            animator.setRepeatCount(Animation.INFINITE);//repeat animation
            animator.start();
        } else {
            mStartAnimate = true;
        }

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mSlideColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mTextHeight = mTextPaint.ascent();
        mTextWidth = mTextPaint.measureText(mStringToShow);

        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(DEBUG)
            Log.w(TAG, "b onDraw()");

        mTextPaint.setShader(mShader);

        canvas.drawText(mStringToShow,
                mDrawOffset + (mWidth - mSlideIconWidth - mSlideIconMarginleft) / 2,
                mHeight / 2 - mTextHeight / 2 - mSlideIconOffSetTop, mTextPaint); // slide_unlock

        canvas.drawBitmap(((BitmapDrawable) mSlideIcon).getBitmap(),
                mDrawOffset + (mWidth - mTextWidth - mSlideIconWidth) / 2 + mTextWidth + mSlideIconMarginleft,
                mHeight / 2 - mSlideIconHeight / 2 + mSlideIconOffSetTop, mTextPaint);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void stopAnimatorAndChangeColor() {
        //if(DEBUG)
        Log.w(TAG, "stopGradient");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animator.cancel();
        } else {
            mStartAnimate = false;
            mLastUpdateTime = 0;
        }

        //reset
        mShader = new LinearGradient(0, 100, mIndex, 100,
                new int[] {mSlideColor, mSlideColor},
                null, Shader.TileMode.CLAMP);
        invalidate();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void startAnimator() {
        if(DEBUG) {
            Log.w(TAG, "startGradient");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animator.start();
        } else {
            mStartAnimate = true;
            mLastUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void updateOffset(float offset) {
        mDrawOffset = offset;
        invalidate();
    }

    public float getOffset() {
        return mDrawOffset;
    }

    public void updateView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        if (mStartAnimate) {
            long currentTime = System.currentTimeMillis();
            if (mLastUpdateTime != 0) {
                int width = mMaxWidth - mMinWidth;
                mIndex += width * (currentTime - mLastUpdateTime) / 1800;
                if (mIndex > mMaxWidth) {
                    mIndex %= (mMaxWidth - mMinWidth);
                    mIndex += mMinWidth;
                }
            }

            mLastUpdateTime = currentTime;

            mShader = new LinearGradient(mIndex - 20 * mUpdateStep, 100,
                    mIndex, 100, new int[] { mDefaultColor, mDefaultColor, mDefaultColor,mSlideColor,
                    mSlideColor, mDefaultColor, mDefaultColor, mDefaultColor }, null,
                    Shader.TileMode.CLAMP);

            invalidate();
        }
    }

    private long mLastUpdateTime;
    private boolean mStartAnimate;
}

