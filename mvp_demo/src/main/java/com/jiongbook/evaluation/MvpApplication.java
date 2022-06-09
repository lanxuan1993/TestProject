package com.jiongbook.evaluation;

import android.app.Application;

import com.example.tools.wechat.WechatUtils;

public class MvpApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        WechatUtils.registerApp(getApplicationContext());
    }
}
