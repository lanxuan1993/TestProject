package com.example.tools.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

/**
 * @author: created by ZhaoBeibei on 2020-05-28 11:47
 * @describe: 设备信息
 */
public class DeviceUtils {
    private static final String ANDROID_PLATFORM = "Android";
    private static final String AMAZON_PLATFORM = "amazon-fireos";
    private static final String AMAZON_DEVICE = "Amazon";

    /**
     * 获取设备信息
     */
    public JSONObject getDeviceInfo(Context context) {
        JSONObject result = new JSONObject();
        try {
            result.put("uuid", getUuid(context));
            result.put("version", this.getOSVersion());
            result.put("platform", this.getPlatform());
            result.put("model", this.getModel());
            result.put("manufacturer", this.getManufacturer());
            result.put("isVirtual", this.isVirtual());
            result.put("serial", this.getSerialNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get the OS name.
     *
     * @return
     */
    private String getPlatform() {
        String platform;
        if (isAmazonDevice()) {
            platform = AMAZON_PLATFORM;
        } else {
            platform = ANDROID_PLATFORM;
        }
        return platform;
    }

    /**
     * Get the device's Universally Unique Identifier (UUID).
     *
     * @return
     */
    public static String getUuid(Context context) {
        String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return uuid;
    }

    /**
     * 型号 e.g (iOS: iPhone9,1 Android: FRD-AL10)
     *
     * @return
     */
    public static String getModel() {
        String model = android.os.Build.MODEL;
        return model;
    }

    private String getProductName() {
        String productname = android.os.Build.PRODUCT;
        return productname;
    }

    /**
     * 厂商 e.g (Apple,HUAWEI)
     *
     * @return
     */
    public static String getManufacturer() {
        String manufacturer = android.os.Build.MANUFACTURER;
        return manufacturer;
    }

    private String getSerialNumber() {
        String serial = android.os.Build.SERIAL;
        return serial;
    }


    /**
     * 操作系统版本 e.g (iOS: 11.1.2 Android: 7.0)
     *
     * @return
     */
    public static String getOSVersion() {
        String osversion = android.os.Build.VERSION.RELEASE;
        return osversion;
    }

    private String getSDKVersion() {
        @SuppressWarnings("deprecation")
        String sdkversion = android.os.Build.VERSION.SDK;
        return sdkversion;
    }

    private String getTimeZoneID() {
        TimeZone tz = TimeZone.getDefault();
        return (tz.getID());
    }

    /**
     * Function to check if the device is manufactured by Amazon
     *
     * @return
     */
    private boolean isAmazonDevice() {
        if (android.os.Build.MANUFACTURER.equals(AMAZON_DEVICE)) {
            return true;
        }
        return false;
    }

    private boolean isVirtual() {
        return android.os.Build.FINGERPRINT.contains("generic") ||
                android.os.Build.PRODUCT.contains("sdk");
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context mContext) {
        int statusBarHeight = (int) Math.ceil(20 * mContext.getResources().getDisplayMetrics().density);
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = mContext.getResources().getDisplayMetrics();
        int height = (int) (statusBarHeight / dm.density);
        return height;
    }

}

