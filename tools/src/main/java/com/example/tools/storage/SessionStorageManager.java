package com.example.tools.storage;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * @author: created by ZhaoBeibei on 2020-10-09 10:28
 * @describe: 临时存储核心类
 */
public class SessionStorageManager {
    private static SessionStorageManager instance = null;
    public static JSONObject mJSONObject = new JSONObject();

    public static SessionStorageManager getInstance() {
        if (instance == null) {
            synchronized (SessionStorageManager.class) {
                if (instance == null) {
                    instance = new SessionStorageManager();
                }
            }
        }
        return instance;
    }

    /**
     * 设置临时存储
     *
     * @param data
     */
    public void nativeSetSessionStorage(JSONObject data) {
        try {
            String key = data.optString("storageKey");
            mJSONObject.put(key, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取临时存储
     *
     * @param data
     */
    public JSONObject getStorage(JSONObject data) {
        String key = data.optString("storageKey");
        if (!TextUtils.isEmpty(key)) {
            JSONObject result = mJSONObject.optJSONObject(key);
            if (result != null) {
                result.remove("storageKey");
                return result;
            }
        }
        return null;
    }

    /**
     * 删除临时存储
     *
     * @param data
     */
    public void deleteStorage(JSONObject data) {
        String key = data.optString("storageKey");
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mJSONObject.remove(key);
    }


}
