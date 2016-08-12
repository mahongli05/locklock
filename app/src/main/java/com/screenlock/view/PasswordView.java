package com.screenlock.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.common.ui.RoundBitmapDrawable;

/**
 * Created by MHL on 2016/8/12.
 */
public class PasswordView extends ViewGroup implements View.OnClickListener {

    private int mSpace = 20;
    private Bitmap[] mBitmaps;
    private ClickCallback mCallback;

    public interface ClickCallback {
        void onNodeClick(int number);
    }

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 构建node
        for (int n = 0; n < 10; n++) {
            NodeView node = new NodeView(getContext(), n);
            node.setOnClickListener(this);
            addView(node);
        }

        // 清除FLAG，否则 onDraw() 不会调用，原因是 ViewGroup 默认透明背景不需要调用 onDraw()
        setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = measureSize(widthMeasureSpec); // 测量宽度
        int height = size * 4 / 3;
        setMeasuredDimension(size, height);
    }

    /**
     * TODO 测量长度
     */
    private int measureSize(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec); // 得到模式
        int specSize = MeasureSpec.getSize(measureSpec); // 得到尺寸
        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                return specSize;
            default:
                return 0;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        float nodeWidth = (right - left - getPaddingLeft() - getPaddingRight() - mSpace * 2) / 3;
        float nodeHeight = (bottom - top - getPaddingTop() - getPaddingBottom() - mSpace * 3) / 4;
        for (int n = 1; n < 10; n++) {
            NodeView node = (NodeView) getChildAt(n);
            // 获取3*3宫格内坐标
            int row = (n - 1) / 3;
            int col = (n - 1) % 3;
            // 计算实际的坐标，要包括内边距和分割边距
            int l = (int) (getPaddingLeft() + col * (nodeWidth + mSpace));
            int t = (int) (getPaddingTop() + row * (nodeHeight + mSpace));
            int r = (int) (l + nodeWidth);
            int b = (int) (t + nodeHeight);
            node.layout(l, t, r, b);
        }
        NodeView nodeView = (NodeView) getChildAt(0);
        int l = (int) (getPaddingLeft() + 1 * (nodeWidth + mSpace));
        int t = (int) (getPaddingTop() + 3 * (nodeHeight + mSpace));
        int r = (int) (l + nodeWidth);
        int b = (int) (t + nodeHeight);
        nodeView.layout(l, t, r, b);
    }

    @Override
    public void onClick(View view) {
        if (mCallback != null && view instanceof NodeView) {
            mCallback.onNodeClick(((NodeView) view).getNum());
        }
    }

    public void setClickCallback(ClickCallback clickCallback) {
        mCallback = clickCallback;
    }

    public void setBitmaps(Bitmap[] bitmaps) {
        if (bitmaps == null || bitmaps.length < 10) {
            throw new IllegalArgumentException("bitmaps is invalid");
        }
        mBitmaps = bitmaps;
        for (int i = 0; i < 10; i ++) {
            NodeView nodeView = (NodeView) getChildAt(i);
            nodeView.setImageDrawable(new RoundBitmapDrawable(getResources(), mBitmaps[i]));
        }
    }

    /**
     * 节点描述类
     */
    private class NodeView extends ImageView {

        private int num;
        private boolean highLighted = false;

        @SuppressWarnings("deprecation")
        public NodeView(Context context, int num) {
            super(context);
            this.num = num;
//            UiUtil.setBackground(this, getResources().getDrawable(R.color.bg_green));
        }

        public boolean isHighLighted() {
            return highLighted;
        }

        @SuppressWarnings("deprecation")
        public void setHighLighted(boolean highLighted, boolean isMid) {
            if (this.highLighted != highLighted) {
                this.highLighted = highLighted;
            }
        }

        public int getCenterX() {
            return (getLeft() + getRight()) / 2;
        }

        public int getCenterY() {
            return (getTop() + getBottom()) / 2;
        }

        public int getNum() {
            return num;
        }

    }
}
