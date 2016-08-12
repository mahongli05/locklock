package com.common.util;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by MHL on 2016/7/18.
 */
public class SystemUtil {

    public static void copyText(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Service.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }
}
