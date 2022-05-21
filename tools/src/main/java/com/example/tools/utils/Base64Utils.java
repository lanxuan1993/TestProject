package com.example.tools.utils;


import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author: created by ZhaoBeibei on 2020-09-24 10:21
 * @describe: Base64工具类
 */
public class Base64Utils {

    /**
     * 将文件转换成Base64编码
     *
     * @param uri 待处理图片uri
     * @return
     */
    public static String FileToBase64(Context context, Uri uri) {
        try {
            if (uri != null) {
                return FileToBase64(context.getContentResolver().openInputStream(uri));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将文件转换成Base64编码
     * 将文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 待处理图片地址(例如:/storage/emulated/0/相机/IMG_20200429_123409.jpg)
     * @return
     */
    public static String FileToBase64(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                return FileToBase64(new FileInputStream(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将文件转换成Base64编码
     * 将文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param
     * @return
     */
    public static String FileToBase64(InputStream is) {
        byte[] data = null;
        String result = null;
        try {
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
