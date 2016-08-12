package com.common.util;

import android.content.res.Resources;

public class DisplayUtil {

    public final static float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    public static int dipToPixel(int dip){
        return (int) (dip * DENSITY + 0.5f);
    }

    public static int dipToPixel(double dip){
        return (int) (dip * DENSITY + 0.5f);
    }
}
