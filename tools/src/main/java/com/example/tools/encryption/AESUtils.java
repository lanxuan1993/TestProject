package com.example.tools.encryption;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

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
            byte[] result = cipher.doFinal(Base64.decode(data, Base64.NO_WRAP | Base64.NO_PADDING));
            return new String(result, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }

    }

}
