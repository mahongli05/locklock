package com.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by MHL on 2016/6/30.
 */
public class DESUtil {

    public static void decrypt(String s, InputStream inputstream, OutputStream outputstream) throws Exception{
        Cipher cipher = getCipher(s, Cipher.DECRYPT_MODE, "DES");
        CipherOutputStream cipheroutputstream = new CipherOutputStream(outputstream, cipher);
        write(inputstream, cipheroutputstream);
    }

    public static void encrypt(String s, InputStream inputstream, OutputStream outputstream) throws Exception{
        Cipher cipher = getCipher(s, Cipher.ENCRYPT_MODE, "DES");
        CipherInputStream cipherinputstream = new CipherInputStream(inputstream, cipher);
        write(cipherinputstream, outputstream);
    }

    private static Cipher getCipher(String s, int mode, String algorithm) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException {
        DESKeySpec deskeyspec = new DESKeySpec(s.getBytes());
        SecretKeyFactory secretkeyfactory = SecretKeyFactory.getInstance(algorithm);
        SecretKey secretkey = secretkeyfactory.generateSecret(deskeyspec);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(mode, secretkey);
        return cipher;
    }

    private static void write(InputStream inputstream, OutputStream outputstream) throws Exception {
        byte abyte0[] = new byte[64];
        int i;
        try {
            while ((i = inputstream.read(abyte0)) != -1) {
                outputstream.write(abyte0, 0, i);
            }
            outputstream.flush();
        } finally {
            IoUtils.close(outputstream);
            IoUtils.close(inputstream);
        }
    }
}
