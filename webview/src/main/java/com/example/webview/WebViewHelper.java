package com.example.webview;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.tools.utils.LogUtils;

import java.io.File;

public class WebViewHelper {
    public static final String TAG = "WebViewHelper";
    public static String mUrl;

    public static void initWebSetting(WebView webView) {
        WebSettings settings = webView.getSettings();
        if (settings == null) { // 无法获得配置
            return;
        }

        settings.setJavaScriptEnabled(true);
        // 开启混合模式，允许 https 的链接加载 http 的资源
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //允许js弹出窗口
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        String defaultAgent = settings.getUserAgentString();
        settings.setUserAgentString(defaultAgent);
        settings.setUseWideViewPort(true);
        settings.setTextZoom(100);
        configureAppCache(webView);
        configureDOMStorage(webView);
        settings.setBuiltInZoomControls(false);
//        webView.addJavascriptInterface(new WebEvent(webView), "WE");
//        webView.addJavascriptInterface(new AndroidtoJs(webView), "YTPLayer");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                CookieManager cookieManager1 = CookieManager.getInstance();
                cookieManager1.setAcceptThirdPartyCookies(webView, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setUrl(String url) {
        mUrl = url;
    }

    public static void clearHistory(WebView webView) {
        webView.clearHistory();
    }

    public static void clearCache(WebView webView) {
        webView.clearCache(true);
    }


    // 设置APP cache
    protected static void configureAppCache(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setAppCacheEnabled(true);
        String cacheDir = webView.getContext().getApplicationContext()
                .getDir("cache", Context.MODE_PRIVATE).getPath();
        LogUtils.d(TAG,"app cacheDir:" + cacheDir);
        ensureExistence(cacheDir);
        settings.setAppCachePath(cacheDir);
    }

    // 设置DOM存储
    protected static void configureDOMStorage(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        String dir = webView.getContext().getApplicationContext()
                .getDir("database", Context.MODE_PRIVATE).getPath();
        LogUtils.d(TAG,"dom storageDir:" + dir);
        settings.setDatabasePath(dir);
    }

    private static void ensureExistence(String cacheDir) {
        File file = new File(cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * webview返回上一页
     */
    public void back(WebView webView){
        if (webView.canGoBack()){
            webView.goBack();
        }
    }

//        //  防止跳浏览器
//       wv.setWebViewClient(new WebViewClient() {
//
//        //<span style="" font-size:11.0pt;"="">覆盖shouldOverrideUrlLoading 方法
//
//        @Override
//
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//            view.loadUrl(url);
//
//            return true;
//
//        }
//
//     });
}

