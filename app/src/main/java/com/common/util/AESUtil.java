package com.common.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by MHL on 2016/6/30.
 */
public class AESUtil {

    private static final String TAG = "AESUtil";

    public static String decrypt(String sSrc, String sKey) {

        if (sSrc == null || sKey == null || sKey.length() != 16) {
            return null;
        }

        byte[] encrypted1 = ByteUtil.hex2byte(sSrc);
        byte[] original = decrypt(encrypted1, sKey);
        return original != null ? new String(original) : null;
    }

    public static byte[] decrypt(byte[] sSrc, String sKey) {

        if (sSrc == null || sKey == null || sKey.length() != 16) {
            return null;
        }

        try {
            byte[] raw = sKey.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(sSrc);
        } catch (Exception ex) {
            LogHelper.e(TAG, "decrypt", ex);
        }

        return null;
    }

    public static String encrypt(String sSrc, String sKey) throws Exception {

        if (sKey == null || sKey.length() != 16) {
            return null;
        }

        byte[] encrypted = encrypt(sSrc.getBytes(), sKey);
        return ByteUtil.byte2hex(encrypted).toLowerCase();
    }

    public static byte[] encrypt(byte[] sSrc, String sKey) throws Exception {

        if (sKey == null || sKey.length() != 16) {
            return null;
        }

        try {
            byte[] raw = sKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(sSrc);
        } catch (Exception e) {
            LogHelper.e(TAG, "encrypt", e);
        }

        return null;
    }

}
