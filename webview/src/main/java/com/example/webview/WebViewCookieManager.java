package com.example.webview;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.example.tools.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class WebViewCookieManager {

    private static final String TAG = "WebViewCookieManager";

    /**
     * 访问动态站需携带APP特殊cookie
     */
    public static void addAppCookie() {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
//            cookieManager.setCookie(ConnectionHelper.WEB_COOKIE_DOMAIN, statSessionIdCookie);

            CookieSyncManager.getInstance().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getCartNumFromCookie() {
//        String number = getCookie(ConnectionHelper.getWebViewCookieDomain(), "xm_user_in_num");
        return 0;
    }

    public static void showAllCookie(String url) {
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            String cookies = cookieManager.getCookie(url);
            if (cookies == null) {
                LogUtils.d(TAG, "cookie is null");
            } else {
                LogUtils.d(TAG, "all cookie:" + cookies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAllCookie(String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(url);

//        seviceToken = getCookieString(HostManager.Parameters.Keys.SERVICE_TOKEN, seviceToken, Constants.Cookie.MAIN_SITE_1, "/" + ConnectionHelper.APP_LOCAL, null);
        return cookie;
    }

    public static String getCookie(String url, String key) {
        LogUtils.d(TAG, "get Cookie key:" + key + " from:" + url);
        String ret = "";
        CookieManager cookieManager = null;
        try {
            cookieManager = CookieManager.getInstance();
        } catch (Exception e) {
            return ret;
        }

        String cookies = cookieManager.getCookie(url);
        if (cookies == null) return ret;
        String[] cs = cookies.split(";");

        for (String c : cs) {
            String[] kv = c.split("=");
            if (kv.length == 2) {
                if (kv[0].trim().equals(key)) {
                    return kv[1];
                }
            }
        }
        LogUtils.d(TAG, "get Cookie val:" + ret + " from:" + url);
        return ret;
    }


    public static void removeLoginCookie(Context context) {
        LogUtils.d(TAG, "remove login cookie in:" + context.toString());

//        removeCookie(context, HostManager.Parameters.Keys.USER_ID);

    }


    /**
     * remove cookie by default domain: .mi.com .xiaomi.com
     */
    public static void removeCookie(Context context, String name) {
//        removeCookie(context, name, Constants.Cookie.MAIN_SITE_1, "/");
    }

    private static String getCookieString(String name, String value, String domain, String path, String expires) {
        StringBuilder cookieString = new StringBuilder();
        cookieString.append(name);
        cookieString.append("=");
        cookieString.append(value);
        cookieString.append(";domain=");
        cookieString.append(domain);
        cookieString.append(";path=");
        cookieString.append(path);
        if (expires != null) {
            cookieString.append(";expires=");
            cookieString.append(expires);
        } else {
            cookieString.append(";");
        }

        return cookieString.toString();
    }

    /**
     * set cookie for webView.
     *
     * @see  "http://zlping.iteye.com/blog/1633213 webview管理cookies在各版本中的区别"
     */
    public static void setCookie(Context context, String name, String value, String domain, String path) {
        CookieManager cookieManager;
        try {
            CookieSyncManager.createInstance(context);
            cookieManager = CookieManager.getInstance();
            if (cookieManager == null) {
                return;
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return;
        } catch (Exception exception) {
            return;
        }
        String cookieString = getCookieString(name, value, domain, path, null);
        cookieManager.setCookie(domain, cookieString);
        LogUtils.d(TAG, "set Cookie: " + cookieString);
        CookieSyncManager.getInstance().sync();

    }

    private static void removeAllCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    private static void removeCookie(Context context, String name, String domain, String path) {
        LogUtils.d(TAG, "remove Cookie: " + domain + ": " + name + "; path is : " + path);
        CookieManager cookieManager;
        try {
            CookieSyncManager.createInstance(context);
            cookieManager = CookieManager.getInstance();
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return;
        } catch (Exception e) {
            return;
        }
        String cookies = cookieManager.getCookie(domain + path);
        LogUtils.d(TAG, "Get from Domain:" + domain + path + " result is:" + cookies);
        if (cookies == null) {
            LogUtils.d(TAG, "no cookie in domain " + domain + path);
            return;
        }
        for (String cookie : cookies.split(";")) {
            //	LogUtil.d(TAG, "process cookie:" + cookie);
            String[] cookieValues = cookie.split("=");
            if (cookieValues.length < 2) {
                continue;
            }
            if (TextUtils.equals(cookieValues[0].trim(), name)) {
                String expires = new Date(1).toGMTString();
                String cookieString = getCookieString(name, "", domain, path, expires);
                cookieManager.setCookie(domain, cookieString);
                LogUtils.d(TAG, "remove succeed");
                cookieManager.removeExpiredCookie();
                CookieSyncManager.getInstance().sync();
                return;
            }
        }
        LogUtils.d(TAG, "cookie name not found");
    }

    public static void clearCustomCookies(Context context) {
//        String jsonCookies = MMKVManager.getInstance().getStringPref(Constants.Prefence.PREF_KEY_CUSTOM_COOKIES, null);
//
//        if (jsonCookies != null && !jsonCookies.equals("")) {
//            MMKVManager.getInstance().removePref(Constants.Prefence.PREF_KEY_CUSTOM_COOKIES);
//            WebViewCookieManager.clearCustomCookies(context, jsonCookies);
//        }
    }

    public static void updateCustomCookies(Context context) {
//        String jsonCookies = MMKVManager.getInstance().getStringPref(Constants.Prefence.PREF_KEY_CUSTOM_COOKIES, null);
//        if (jsonCookies != null && !jsonCookies.equals("")) {
//            WebViewCookieManager.addCustomCookies(context, jsonCookies);
//        }
    }

    public static void updateCustomCookies(Context context, String jsonCookies) {
        if (jsonCookies != null && !jsonCookies.equals("")) {
            WebViewCookieManager.addCustomCookies(context, jsonCookies);
        }
    }

    public static void addCustomCookies(Context context, String jsonCookies) {
        if (context == null)
            return;

        JSONArray cookiesJson = null;

        try {
            cookiesJson = new JSONArray(jsonCookies);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < cookiesJson.length(); i++) {
            JSONObject cookiObj = cookiesJson.optJSONObject(i);
            String key = cookiObj.optString("key");
            String value = cookiObj.optString("value");
            String domain = cookiObj.optString("domain");
            String path = cookiObj.optString("path");

            if (key != null && value != null && domain != null && path != null) {
                WebViewCookieManager.setCookie(context, key, value, domain, path);
            }
        }
    }

    private static void clearCustomCookies(Context context, String jsonCookies) {
        if (context == null)
            return;

        JSONArray cookiesJson = null;

        try {
            cookiesJson = new JSONArray(jsonCookies);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        for (int i = 0; i < cookiesJson.length(); i++) {
            JSONObject cookiObj = cookiesJson.optJSONObject(i);
            String key = cookiObj.optString("key");
            String value = cookiObj.optString("value");
            String domain = cookiObj.optString("domain");
            String path = cookiObj.optString("path");

            if (key != null && value != null && domain != null && path != null) {
                WebViewCookieManager.removeCookie(context, key, domain, path);
            }
        }
    }

}
