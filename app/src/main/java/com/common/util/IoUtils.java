package com.common.util;

import android.database.Cursor;

import java.io.Closeable;

/**
 * Created by MHL on 2016/6/29.
 */
public class IoUtils {

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }

    public static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {

            }
        }
    }
}
