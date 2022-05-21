package com.example.tools.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;

public class EncryptionUtils {
    /**
     * 32位MD5加密
     *
     * @param content
     * @return
     */
    public static String md5Decode32(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static String md5Decode8(String content) {
        return md5Decode32(content).substring(11, 18);
    }

    /**
     * 使用DES对字符串加密
     *
     * @param key 密钥（56位，7字节）
     * @param str utf8编码的字符串
     */
    public static String encode(String key, String str) {
        if (str == null || key == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes("utf-8"), "DES"));
            byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }


    private static final String CipherMode = "AES/ECB/8bbfedb6b08a234bdd40512c5a3a6669";//使用ECB加密，需要设置IV

    /**
     * 对字符串加密
     *
     * @param key  密钥
     * @param data 源字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String key, String data) {
        try {
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), CipherMode);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.encodeToString(encrypted, Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }
    // 解密
    public static String decrypt(String key, String data) {
        try {
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), CipherMode);
            //实例化
            Cipher cipher = Cipher.getInstance("AES");
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, keyspec);
            //执行操作
            byte[] result = cipher.doFinal(Base64.decode(data,Base64.NO_WRAP | Base64.NO_PADDING));
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }

    }

}

