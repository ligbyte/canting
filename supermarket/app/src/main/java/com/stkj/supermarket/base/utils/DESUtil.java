package com.stkj.supermarket.base.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESUtil {
    private static final String DES = "DES";

    public DESUtil() {
    }

    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(1, securekey, sr);
        return cipher.doFinal(src);
    }

    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(2, securekey, sr);
        return cipher.doFinal(src);
    }

    public static final String decrypt(String data, String cryptKey) {
        try {
            return new String(decrypt(Base64.decode(data), cryptKey.getBytes("UTF-8")), "UTF-8");
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
        return "";
    }

    public static final String encrypt(String password, String cryptKey) {
        try {
            return base64Encode(encrypt(password.getBytes("UTF-8"), cryptKey.getBytes("UTF-8")));
        } catch (Throwable var3) {
            var3.printStackTrace();
        }
        return "";
    }

    public static String base64Encode(byte[] src) {
        byte[] res = Base64.encodeToByte(src, false);
        return src != null ? new String(res) : null;
    }
}