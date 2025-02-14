package com.stkj.infocollect.base.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * @description: RSA加密工具类
 * @author: admin
 */
public class RSAUtil {
    /**
     * 随机生成密钥对
     */
    public static Map<Integer, String> genKeyPair() {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = null;

        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 初始化密钥对生成器，密钥大小为96-1024位
        assert keyPairGen != null;
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.encodeToString(publicKey.getEncoded(), false);
        // 得到私钥字符串
        String privateKeyString = Base64.encodeToString(privateKey.getEncoded(), false);
        // 将公钥和私钥保存到Map
        Map<Integer, String> keyMap = new HashMap<>();
        // 0表示公钥
        keyMap.put(0, publicKeyString);
        // 1表示私钥
        keyMap.put(1, privateKeyString);
        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param str 加密字符串
     * @return 密文
     */
    public static String encrypt(String str, String publicKey) {
        String outStr = null;
        try {
            // base64编码的公钥
            byte[] decoded = Base64.decodeFast(publicKey.getBytes(StandardCharsets.UTF_8));
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)), false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        // RSA加密
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str 加密字符串
     * @return 明文
     */
    public static String decrypt(String str, String privateKey) {
        String outStr = null;
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.decodeFast(str.getBytes(StandardCharsets.UTF_8));
            //base64编码的私钥
            byte[] decoded = Base64.decodeFast(privateKey.getBytes());
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            outStr = new String(cipher.doFinal(inputByte));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return outStr;
    }

}