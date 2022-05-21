package com.example.tools.picture.photo.bridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.tools.picture.photo.TakePhotoManager;
import com.example.tools.picture.photo.utils.IntentUtils;

/**
 * @author: created by ZhaoBeibei on 2020-06-05 15:27
 * @describe: 中间透明类
 */
public class BridgeActivity extends Activity {
    private static final String TAG = BridgeActivity.class.getName();
    private ActivityLifecycle lifecycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData(intent);
    }

    /**
     * 初始化数据
     * @param intent
     */
    public void initData(Intent intent){
        lifecycle = TakePhotoManager.lifecycle;
        int type = intent.getIntExtra("type", TakePhotoManager.TYPE_CAMERA);
        if (type == TakePhotoManager.TYPE_CAMERA) {
            openCamera();
        } else {
            openAlbum();
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        startActivityForResult(IntentUtils.getCameraIntent(TakePhotoManager.mCameraUri), TakePhotoManager.REQUEST_CODE_CAMERA);
    }

    /**
     * 打开相册选取图片
     */
    private void openAlbum() {
        startActivityForResult(IntentUtils.getAlbumIntent(), TakePhotoManager.REQUEST_CODE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        lifecycle.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
    }
}