package com.example.webview;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class BaseWebChromeClient extends WebChromeClient {

    //jsAlert弹出
    @Override
    public boolean onJsAlert(WebView view, String url, String message,
                             final JsResult result) {
        if (message != null) {
            Toast.makeText(view.getContext(), message,
                    Toast.LENGTH_LONG).show();
        }
        result.cancel();
        return true;
    }
}
