package com.example.webview;

import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.tools.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class BaseWebViewClient extends WebViewClient {

    //读取文件流
    public InputStream fileToInputStream(String filePath) {
        InputStream is = null;
        try {

            File file = new File(filePath);
            if (file.exists()) {
                is = new FileInputStream(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    //资源请求处理
    public WebResourceResponse baseInterceptRequest(String TAG, WebView view,
                                                    String url, String[] httpRes) {
        LogUtils.d(TAG, "Resource Request PATH:" + httpRes[1]);
        InputStream is = fileToInputStream(httpRes[1]);
        if (is == null)
            return super.shouldInterceptRequest(view, url);
        LogUtils.d(TAG, "Resource Request file exists!");
        return new WebResourceResponse(httpRes[0], "UTF-8", is);
    }


}
