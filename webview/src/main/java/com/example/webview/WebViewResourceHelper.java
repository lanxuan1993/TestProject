/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.webview;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.text.TextUtils;

import com.example.tools.utils.LogUtils;

import java.lang.reflect.Method;


/**
 * 修改由于替换了Resource引起的，在Webview中长按弹窗菜单导致的crash问题
 *
 * @author RePlugin Team
 */
public class WebViewResourceHelper {
    public static final String TAG = "WebViewResourceHelper";
    private static boolean sInitialed = false;

    public static boolean addChromeResourceIfNeeded(Context context) {
        if (sInitialed) {
            return true;
        }

        String dir = getWebViewResourceDir(context);
        if (TextUtils.isEmpty(dir)) {
            return false;
        }

        try {
            Method m = getAddAssetPathMethod();
            if (m != null) {
                int ret = (int) m.invoke(context.getAssets(), dir);
                sInitialed = ret > 0;
                return sInitialed;
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "[init webview res] : invoke method error ! " + e);
        }

        return false;
    }

    private static Method getAddAssetPathMethod() {
        Method m = null;
        Class c = AssetManager.class;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                m = c.getDeclaredMethod("addAssetPathAsSharedLibrary", String.class);
                m.setAccessible(true);
            } catch (NoSuchMethodException e) {
                // Do Nothing
                e.printStackTrace();
            }
            return m;
        }

        try {
            m = c.getDeclaredMethod("addAssetPath", String.class);
            m.setAccessible(true);
        } catch (NoSuchMethodException e) {
            // Do Nothing
            e.printStackTrace();
        }

        return m;
    }

    private static String getWebViewResourceDir(Context context) {
        String pkgName = getWebViewPackageName();
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }

        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(getWebViewPackageName(), PackageManager.GET_SHARED_LIBRARY_FILES);
            return pi.applicationInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.d(TAG, "get webview application info failed! " + e);
        } catch (Exception e) {
            // Do Nothing
        }

        return null;
    }

    private static String getWebViewPackageName() {
        int sdk = Build.VERSION.SDK_INT;

        if (sdk <= Build.VERSION_CODES.KITKAT_WATCH) {
            return null;
        }

        switch (sdk) {
            case 21:
            case 22:
                return getWebViewPackageName4Lollipop();
            case 23:
                return getWebViewPackageName4M();
            case 24:
                return getWebViewPackageName4N();
            case 25:
            default:
                return getWebViewPackageName4More();
        }
    }

    private static String getWebViewPackageName4Lollipop() {
        try {
            return (String) invokeStaticMethod("android.webkit.WebViewFactory", "getWebViewPackageName", null);
        } catch (Throwable e) {
            //
        }
        return "com.google.android.webview";
    }

    private static String getWebViewPackageName4M() {
        return getWebViewPackageName4Lollipop();
    }

    private static String getWebViewPackageName4N() {
        try {
            Context c = (Context) invokeStaticMethod("android.webkit.WebViewFactory", "getWebViewContextAndSetProvider", null);
            return c.getApplicationInfo().packageName;
        } catch (Throwable e) {
            //
        }
        return "com.google.android.webview";
    }

    private static String getWebViewPackageName4More() {
        return getWebViewPackageName4N();
    }

    private static Object invokeStaticMethod(String clzName, String methodName, Class<?>[] methodParamTypes, Object... methodParamValues) {
        try {
            Class clz = Class.forName(clzName);
            Method med = clz.getDeclaredMethod(methodName, methodParamTypes);
            med.setAccessible(true);
            Object retObj = med.invoke(null, methodParamValues);
            return retObj;
        } catch (Exception e) {
            LogUtils.d(TAG, "invokeStaticMethod got Exception:" + e);
        }
        return null;
    }

}