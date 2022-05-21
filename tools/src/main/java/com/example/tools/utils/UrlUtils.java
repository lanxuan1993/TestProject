package com.example.tools.utils;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * @author: created by ZhaoBeibei on 2021/1/7 18:11
 * @describe: 链接处理类
 */
public class UrlUtils {

    /**
     * url拼接时间戳
     *
     * @param context
     * @param url
     * @return
     */
    public static String resetUrl(Context context, String url) {
        String newUrl;
        String[] arr = url.split("#");
        if (arr.length > 1) { // 有#
            String s = splitQuestionMark(context, arr[0]);
            String hashPart = url.substring(url.indexOf("#"));
            newUrl = s + hashPart;
        } else { // 没有#
            newUrl = splitQuestionMark(context, url);
        }
        return newUrl;
    }

    /**
     * 分割问号"?"
     *
     * @param context
     * @param url
     * @return
     */
    public static String splitQuestionMark(Context context, String url) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String newUrl;
        String[] arr = url.split("\\?");
        if (arr.length > 1) { //有问号
            boolean contains = arr[1].contains("t=");
            if (contains) {
                newUrl = url;
            } else {
                newUrl = url + "&t=" + timeStamp;
            }
        } else { //没有问号
            newUrl = url + "?t=" + timeStamp;
        }
        return newUrl;
    }


    /**
     * url解密
     *
     * @param url
     * @return
     */
    public static String decodeUrl(String url) {
        String result = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                url = url.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                result = URLDecoder.decode(url, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * query2json
     *
     * @return
     */
    public static JSONObject getBhfaeQueryJsonObject(String str) {
        JSONObject param = new JSONObject();
        try {
            if (str != null) {
                String[] arr = str.split("\\&");
                for (int i = 0; i < arr.length; i++) {
                    String[] tmpArr = arr[i].split("\\=");
                    if (tmpArr.length == 1) {
                        param.put(tmpArr[0], "");
                    } else if (tmpArr.length == 2) {
                        param.put(tmpArr[0], tmpArr[1]);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return param;
    }

    /**
     * 参数拼接成url格式
     *
     * @param map
     * @return
     */
    public static String buildMapUrl(Map<String, String> map) {
        StringBuffer sb = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                sb.append(key + "=");
                String value = map.get(key);
                if (TextUtils.isEmpty(value)) {
                    sb.append("&");
                } else {
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value + "&");
                }
            }
        }

        String urlParam = sb.toString();
        if (urlParam != null && urlParam.endsWith("&")) {
            urlParam = sb.substring(0, urlParam.length() - 1);
        }
        return urlParam;
    }

}
