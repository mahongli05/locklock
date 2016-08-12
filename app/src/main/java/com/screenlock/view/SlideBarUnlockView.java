package com.screenlock.view;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.common.util.UiUtil;
import com.screenlock.ScreenContent;
import com.screenlock.ScreenLockHelper;

import ma.com.locklock.R;

/**
 * Created by MHL on 2016/8/12.
 */
public class SlideBarUnlockView extends ScreenContent {

    private SlideBar mSlideView;

    public SlideBarUnlockView(Context context) {
        super(context);
        setupView(context);
    }

    private void setupView(Context context) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                context.getResources().getDimensionPixelOffset(R.dimen.slide_bar_width),
                context.getResources().getDimensionPixelOffset(R.dimen.slide_bar_height));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.slide_bar_margin_bottom);

        int r = getResources().getDimensionPixelOffset(R.dimen.slide_bar_bg_radiu);
        float[] outerR = new float[] { r, r, r, r, r, r, r, r };
        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        drawable.getPaint().setColor(0);
        drawable.getPaint().setAlpha(25);

        mSlideView = new SlideBar(context);
        UiUtil.setBackground(mSlideView, drawable);
        addView(mSlideView, params);
        mSlideView.setOnTriggerListener(new SlideBar.OnTriggerListener() {
            @Override
            public void onTrigger() {
                ScreenLockHelper.getInstance().stopLock(true);
            }
        });
    }
}
