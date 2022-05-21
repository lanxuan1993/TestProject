package com.example.tools.picture.photo.utils;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * @author: created by ZhaoBeibei on 2020-06-09 18:27
 * @describe: Intent工具类
 */
public class IntentUtils {
    private static final String TAG = IntentUtils.class.getName();

    /**
     * 获取拍照的Intent
     *
     * @return
     */
    public static Intent getCameraIntent(Uri uri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        return intent;
    }

    /**
     * 获取选择照片的Intent
     *
     * @return
     */
    public static Intent getAlbumIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }

}
