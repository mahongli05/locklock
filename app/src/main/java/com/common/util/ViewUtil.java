package com.zhh.common.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by MHL on 2016/7/18.
 */
public class ViewUtil {

//    public static Rect getRelativeRect(View srcView, View dstView) {
//        Rect srcRect = new Rect();
//        Rect dstRect = new Rect();
//        if (srcView != null && dstView != null) {
//            srcView.getGlobalVisibleRect(srcRect);
//            dstView.getGlobalVisibleRect(dstRect);
//            return new Rect(dstRect.left - srcRect.left,
//                    dstRect.top - srcRect.top,
//                    dstRect.right - srcRect.left,
//                    dstRect.bottom - srcRect.top);
//        }
//        return null;
//    }

    public static Rect getRelativeRect(View srcView, View dstView) {
        int[] srcP = new int[2];
        int[] dstP = new int[2];
        if (srcView != null && dstView != null) {
            srcView.getLocationOnScreen(srcP);
            dstView.getLocationOnScreen(dstP);
            return new Rect(dstP[0] - srcP[0],
                    dstP[1] - srcP[1],
                    dstP[0] - srcP[0] + dstView.getWidth(),
                    dstP[1] - srcP[1] + dstView.getHeight());
        }
        return null;
    }

    public static View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public static ViewGroup getActivityRootView(Activity activity) {

        if (activity == null) {
            return null;
        }

        return (ViewGroup)((ViewGroup)activity.findViewById(android.R.id.content)).getChildAt(0);
    }
}
