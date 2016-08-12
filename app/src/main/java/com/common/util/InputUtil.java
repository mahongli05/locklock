package com.common.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MHL on 2016/7/21.
 */
public class InputUtil {

    // http://www.regular-expressions.info/email.html
    private static final String EMAIL_REGULAR = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static boolean isInputNumber(String input) {
        boolean result = false;
        try {
            Long.valueOf(input);
            result = true;
        } catch (Exception e) {

        }
        return result;
    }

    public static boolean isInputEmail(String input) {
        Pattern p = Pattern.compile(EMAIL_REGULAR);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static boolean isInputText(String input) {
        return !TextUtils.isEmpty(input);
    }
}
