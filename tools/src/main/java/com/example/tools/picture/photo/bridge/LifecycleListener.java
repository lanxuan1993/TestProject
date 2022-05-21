package com.example.tools.picture.photo.bridge;

import android.content.Intent;

/**
 * @author : zhaobeibei
 * createDate   : 2020-06-11
 * desc   : 生命周期监听
 */
public interface LifecycleListener {

    void onDestroy();

    void onActivityResult(int requestCode, int resultCode, Intent data);
}
