package com.common.util;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MHL on 2016/7/1.
 */
public class SecurityUtil {

    private static final int CACHE_SIZE = 8192;

    public static byte[] getDigest(InputStream is, String algorithm)
            throws NoSuchAlgorithmException, IOException {

        if (is == null || TextUtils.isEmpty(algorithm)) {
            return null;
        }

        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        byte[] bytes = new byte[CACHE_SIZE];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            md.update(bytes, 0, numBytes);
        }
        return md.digest();
    }

    public static byte[] getDigest(String message, String algorithm)
            throws NoSuchAlgorithmException, IOException {

        if (TextUtils.isEmpty(message) || TextUtils.isEmpty(algorithm)) {
            return null;
        }

        byte[] bytes = message.getBytes(Charset.forName("UTF-8"));
        return getDigest(bytes, algorithm);
    }

    public static byte[] getDigest(byte[] message, String algorithm)
            throws NoSuchAlgorithmException, IOException {

        if (message == null || TextUtils.isEmpty(algorithm)) {
            return null;
        }

        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.reset();
        md.update(message);
        return md.digest();
    }
}
