package com.example.tools.utils;

import android.content.Intent;

public class JumpActivityUtils {
    /**
     *跳转顺序是：由本SplashActivity直接跳转至CommonWebActivity,返回的时候会经过MainActivity.
     *
     * 顺序是从Intent[]里面从后往前运行  ，就是栈的先进后出的原则
     */
    public static void jumpActivities(){
//        Intent[] intents = new Intent[2];
//        intents[0] = new Intent(SplashActivity.this, MainActivity.class);
//        intents[1] = new Intent(SplashActivity.this, CommonWebActivity.class);
//        intents[1].putExtra("otherurl", htmlBaseUrl + "?fid=" + advertBean.getFid());
//        startActivities(intents);
    }


}
