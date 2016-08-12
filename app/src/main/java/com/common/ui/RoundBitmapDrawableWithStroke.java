package com.common.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Mario on 2016/4/1.
 */
public class RoundBitmapDrawableWithStroke extends RoundBitmapDrawable {

    private Paint mStrokePaint;
    private float mStrokeWidth;
    private int mStrokeColor;

    public RoundBitmapDrawableWithStroke(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        mStrokeWidth = 1;
        mStrokeColor = 0xffffffff;
        mStrokePaint = new Paint();
        mStrokePaint.setAntiAlias(true);
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setColor(mStrokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
    }

    public void setStrokeWidth(float width) {
        mStrokeWidth = width;
    }

    public void setStrokeColor(int color) {
        mStrokeColor = color;
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);

        Rect rect = getBounds();
        float cx = rect.width() / 2f;
        float cy = rect.height() / 2f;
        canvas.drawCircle(cx, cy, Math.min(cx, cy) - mStrokeWidth/2f, mStrokePaint);
    }

}
