package com.example.tools.storage;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: created by ZhaoBeibei on 2020-10-09 10:28
 * @describe: 长期存储核心类
 */
public class LongStorageManager {

    private static LongStorageManager instance = null;
    private SharedPreferences sp;

    public static LongStorageManager getInstance() {
        if (instance == null) {
            synchronized (LongStorageManager.class) {
                if (instance == null) {
                    instance = new LongStorageManager();
                }
            }
        }
        return instance;
    }


    private LongStorageManager() {
    }


    /**
     * 设置长期存储
     *
     * @param data
     */
    public void nativeSetLocalStorage(JSONObject data) {
        String key = data.optString("storageKey");
        if (!TextUtils.isEmpty(key)) {
            sp.edit().putString(key, data.toString()).commit();
        }
    }


    /**
     * 获取长期存储
     *
     * @param data
     */
    public JSONObject nativeGetLocalStorage(JSONObject data) {
        String key = data.optString("storageKey");
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        String value = sp.getString(key, null);
        if (value == null) {
            return null;
        } else {
            try {
                JSONObject result = new JSONObject(value);
                if (result != null) {
                    result.remove("storageKey");
                    return result;
                } else {
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 删除长期存储
     *
     * @param data
     */
    public void nativeDeleteLocalStorage(JSONObject data) {
        String key = data.optString("storageKey");
        if (!TextUtils.isEmpty(key)) {
            sp.edit().remove(key).commit();
        }
    }
}
