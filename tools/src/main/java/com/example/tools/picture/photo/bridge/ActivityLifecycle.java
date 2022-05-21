package com.example.tools.picture.photo.bridge;

import android.content.Intent;

/**
 * @author : zhaobeibei
 * createDate   : 2020-06-11
 * desc   : 生命周期接口的管理
 */
public class ActivityLifecycle {

    private LifecycleListener lifecycleListener;

    public void setLifecycleListener(LifecycleListener listener) {
        this.lifecycleListener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        lifecycleListener.onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy() {
        lifecycleListener.onDestroy();
    }
}
