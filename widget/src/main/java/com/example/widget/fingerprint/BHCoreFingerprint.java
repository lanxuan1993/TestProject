package com.example.widget.fingerprint;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.example.widget.fingerprint.callback.BHCoreFingerprintCallBack;
import com.example.widget.fingerprint.callback.FingerprintCallback;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: created by ZhaoBeibei on 2020-04-03 11:05
 * @describe: 指纹登录核心库
 */
public class BHCoreFingerprint {
    private static final String TAG = "BHCoreFingerprint";
    public static final String SUCCESS_CODE = "0000";
    public static final String ERROR_CODE_DEF = "-9999";
    private static final String DENIED_CODE = "-3001";
    private static final String ALWAYS_DENIED_CODE = "-3000";
    private static final String SUCCESS_MSG = "success";
    private BHCoreFingerprintCallBack mCallBack;
    private Context mContext;
    private FragmentActivity activity;

    public BHCoreFingerprint(Context context) {
        mContext = context;
    }

    /**
     * 设备指纹是否可用
     */
    public void availableFingerprint(BHCoreFingerprintCallBack callBack) {
        mCallBack = callBack;
        activity = (FragmentActivity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RxPermissions rxPermissions = new RxPermissions(activity);
                rxPermissions.request(Manifest.permission.USE_FINGERPRINT)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean granted) {
                                if (granted) {
                                    checkFingerprint();
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !activity.shouldShowRequestPermissionRationale(Manifest.permission.USE_FINGERPRINT)) {
                                    failedCallBack(ALWAYS_DENIED_CODE, "always permission denied");
                                } else {
                                    failedCallBack(DENIED_CODE, "permission denied");
                                }
                            }
                        });
            }
        });
    }

    private void checkFingerprint() {
        FingerprintAuthManager.getInstance(mContext).isAvailable(new FingerprintCallback() {
            @Override
            public void onSuccess(JSONObject resultJson) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("isAvailable", resultJson.optBoolean("isAvailable"));
                    successCallBack(SUCCESS_MSG, obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                failedCallBack(ERROR_CODE_DEF, message);
            }
        });
    }


    /**
     * 开启指纹 (成功后返回 fingerPrintToken)
     * authorizationCode: 开启指纹后,服务端返回的授权码
     */
    public void openFingerprint(JSONObject value, BHCoreFingerprintCallBack callBack) {
        mCallBack = callBack;
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientId", value.optString("clientId"));
        map.put("username", value.optString("userId"));
        map.put("password", value.optString("authorizationCode"));
        FingerprintAuthManager
                .getInstance(mContext)
                .initEncryptAndDecrypt(FingerprintAuthManager.ENCRYPT, map, new FingerprintCallback() {
                    @Override
                    public void onSuccess(JSONObject resultJson) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("fingerPrintToken", resultJson.optString("token"));
                            successCallBack(SUCCESS_MSG, obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        failedCallBack(ERROR_CODE_DEF, "开启失败:" + error);
                    }
                });
    }

    /**
     * 登录验证指纹
     *
     * @param value
     * @param callBack
     */
    public void verifyFingerprint(JSONObject value, BHCoreFingerprintCallBack callBack) {
        mCallBack = callBack;
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientId", value.optString("clientId"));
        map.put("username", value.optString("userId"));
        map.put("token", value.optString("fingerPrintToken"));
        FingerprintAuthManager
                .getInstance(mContext)
                .initEncryptAndDecrypt(FingerprintAuthManager.DECRYPT, map, new FingerprintCallback() {
                    @Override
                    public void onSuccess(JSONObject resultJson) {
                        boolean withFingerprint = resultJson.optBoolean("withFingerprint");
                        String password = resultJson.optString("password");
                        if (withFingerprint && !TextUtils.isEmpty(password)) {
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("withFingerprint", withFingerprint);
                                obj.put("password", password);
                                successCallBack(SUCCESS_MSG, obj);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            failedCallBack(ERROR_CODE_DEF, "验证失败: 非指纹验证或者本地获取授权码失败");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        failedCallBack(ERROR_CODE_DEF, "验证失败:" + error);
                    }
                });
    }


    /**
     * 关闭指纹
     */
    public void closeFingerprint(JSONObject value, BHCoreFingerprintCallBack callBack) {
        mCallBack = callBack;
        HashMap<String, Object> map = new HashMap<>();
        map.put("clientId", value.optString("clientId"));
        map.put("username", value.optString("userId"));
        FingerprintAuthManager
                .getInstance(mContext)
                .delete(map, new FingerprintCallback() {
                    @Override
                    public void onSuccess(JSONObject resultJson) {
                        Log.i(TAG, "onSuccess: 关闭成功");
                        successCallBack(SUCCESS_MSG, null);
                    }

                    @Override
                    public void onError(String error) {
                        failedCallBack(ERROR_CODE_DEF, "关闭失败:" + error);
                    }
                });
    }



    /*---------------------------------------返回数据封装---------------------------------------------*/

    private void successCallBack(String message, JSONObject data) {
        JSONObject result = getResultData(SUCCESS_CODE, message, data);
        mCallBack.getCallBack(result);
    }

    private void failedCallBack(String code, String message) {
        JSONObject result = getResultData(code, message, null);
        mCallBack.getCallBack(result);
    }

    private JSONObject getResultData(String code, String message, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        code = (code == null) ? ERROR_CODE_DEF : code;
        message = (message == null) ? "" : message;
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", message);
            if (data != null) {
                jsonObject.put("body", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
