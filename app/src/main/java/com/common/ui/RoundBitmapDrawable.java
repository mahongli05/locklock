package com.common.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

public class RoundBitmapDrawable extends BitmapDrawable {

    private Paint mPaint;
    protected Bitmap mBitmap;
    private BitmapShader mShader;
    private Matrix mMatrix;

    public RoundBitmapDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        mBitmap = bitmap;
        mShader =  new BitmapShader(bitmap, Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mShader);
        mMatrix = new Matrix();
    }

    @Override
    public void draw(Canvas canvas) {

        Rect rect = getBounds();
        float scaleX = (float)rect.width() / mBitmap.getWidth();
        float scaleY = (float)rect.height() / mBitmap.getHeight();
        mMatrix.setScale(scaleX, scaleY);
        mShader.setLocalMatrix(mMatrix);

        canvas.save();
        float cx = rect.width() / 2f;
        float cy = rect.height() / 2f;
        canvas.drawCircle(cx, cy, Math.min(cx, cy), mPaint);
        canvas.restore();
    }
}
