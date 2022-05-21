package com.example.widget.fingerprint.callback;

import org.json.JSONObject;

/**
 * @author: created by ZhaoBeibei on 2020-04-03 17:43
 * @describe:
 */
public interface FingerprintCallback {
    void onSuccess(JSONObject resultJson);
    void onError(String Message);
}
