package com.example.tools.encryption;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DESUtils {
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

}
