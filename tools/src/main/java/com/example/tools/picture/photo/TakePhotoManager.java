package com.example.tools.picture.photo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.tools.picture.photo.bridge.ActivityLifecycle;
import com.example.tools.picture.photo.bridge.BridgeActivity;
import com.example.tools.picture.photo.bridge.LifecycleListener;
import com.example.tools.picture.photo.utils.ImageUtils;
import com.example.tools.picture.photo.utils.UriUtils;
import com.example.tools.utils.CommonUtils;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import io.reactivex.rxjava3.functions.Consumer;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * @author: created by ZhaoBeibei on 2020-06-11 15:07
 * @describe: 图片管理(拍照 - 相册选取图片 - 压缩)
 */
public class TakePhotoManager implements LifecycleListener {
    private static final String TAG = TakePhotoManager.class.getName();
    private static final String ERROR_CODE_DEF = "-9999";
    private static final String CANCEL_CODE_DEF = "-1000";
    private static final String DENIED_CODE = "-3001";
    private static final String ALWAYS_DENIED_CODE = "-3000";
    public static final int REQUEST_CODE_CAMERA = 100;
    public static final int REQUEST_CODE_ALBUM = 200;
    /**
     * 是否是Android 10以上手机
     */
    private boolean isAndroidQ = Build.VERSION.SDK_INT > Build.VERSION_CODES.P;
    /**
     * 类型:拍照
     */
    public static final int TYPE_CAMERA = 0;
    /**
     * 类型:从相册选择
     */
    public static final int TYPE_ALBUM = 1;
    /**
     * 类型
     */
    private int type;
    /**
     * quality: 0~100
     * 质量压缩图片,数值越小质量越差
     */
    private int quality = 50;
    /**
     * 质量压缩图片,指定大小(默认kb)
     */
    private int maxLength = 0;
    /**
     * 压缩图片指定的宽度
     */
    private int width = 0;
    /**
     * 压缩图片指定的高度
     */
    private int height = 0;
    /**
     * 在返回原图的时候，也就是不进行压缩和裁剪的处理直接返回原图的时候，是否需要旋转图片
     * 压缩和裁剪会自动处理旋转角度
     */
    private boolean correctOrientation = true;
    /**
     * 用于保存拍照图片的uri
     */
    public static Uri mCameraUri;
    private Context mContext;
    public static ActivityLifecycle lifecycle;
    private TakePhotoResult mTakePhotoResult;
    private static final String[] PERMISSION_CAMERAS = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] PERMISSION_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private int total;
    private int grantCount;
    private int alwaysDeniedCount;


    public TakePhotoManager(Context context) {
        this.mContext = context;
        lifecycle = new ActivityLifecycle();
        lifecycle.setLifecycleListener(this);
    }

    /**
     * 打开相机
     *
     * @param quality
     * @param maxLength
     * @param width
     * @param height
     * @return
     */
    public TakePhotoManager openCamera(int quality, int maxLength, int width, int height, boolean correctOrientation) {
        type = TYPE_CAMERA;
        this.quality = quality;
        this.maxLength = maxLength;
        this.width = width;
        this.height = height;
        this.correctOrientation = correctOrientation;
        return this;
    }

    public TakePhotoManager openCamera() {
        return openCamera(quality, maxLength, width, height, correctOrientation);
    }


    /**
     * 打开相册
     *
     * @param quality
     * @param maxLength
     * @param width
     * @param height
     * @return
     */
    public TakePhotoManager openAlbum(int quality, int maxLength, int width, int height, boolean correctOrientation) {
        type = TYPE_ALBUM;
        this.quality = quality;
        this.maxLength = maxLength;
        this.width = width;
        this.height = height;
        this.correctOrientation = correctOrientation;
        return this;
    }

    public TakePhotoManager openAlbum() {
        return openAlbum(quality, maxLength, width, height, correctOrientation);
    }


    public void build(TakePhotoResult takePhotoResult) {
        mTakePhotoResult = takePhotoResult;
        total = 0;
        grantCount = 0;
        alwaysDeniedCount = 0;
        String[] PERMISSIONS = type == TYPE_CAMERA ? PERMISSION_CAMERAS : PERMISSION_STORAGE;
        FragmentActivity activity = (FragmentActivity) mContext;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final RxPermissions rxPermissions = new RxPermissions(activity);
                    rxPermissions.requestEach(PERMISSIONS)
                            .subscribe(new Consumer<Permission>() {
                                @Override
                                public void accept(Permission permission) throws Exception {
                                    if (permission.granted) {// 用户同意该权限
                                        grantCount++;
                                    } else if (!permission.shouldShowRequestPermissionRationale) {// 用户拒绝了该权限，而且选中『不再询问』
                                        alwaysDeniedCount++;
                                    }

                                    total++;
                                    if (PERMISSIONS.length == total) {
                                        if (grantCount == total) {
                                            captureImage();
                                        } else {
                                            if (alwaysDeniedCount > 0) {
                                                mTakePhotoResult.takeFailure(ALWAYS_DENIED_CODE, "永久拒绝授权");
                                            } else {
                                                mTakePhotoResult.takeFailure(DENIED_CODE, "拒绝授权");
                                            }
                                        }
                                    }
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    captureImage();
                }
            }
        });
    }

    /**
     * 捕获图片
     */
    public void captureImage() {
        if (type == TYPE_CAMERA) {
            creatCameraUri();
        }
        Intent intent = new Intent(mContext, BridgeActivity.class);
        intent.putExtra("type", type);
        mContext.startActivity(intent);
    }

    /**
     * 创建拍照后存放图片的uri
     */
    private void creatCameraUri() {
        Uri photoUri = null;
        if (isAndroidQ) {
            photoUri = createImageUri();
        } else {
            photoUri = UriUtils.getTempSchemeContentUri(mContext, null);
        }
        mCameraUri = photoUri;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return mContext.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_ALBUM:
                if (data != null) {
                    Uri albumUri = data.getData();
                    setLocalId(albumUri);
                } else {
                    if (resultCode == RESULT_CANCELED) {
                        mTakePhotoResult.takeFailure(CANCEL_CODE_DEF, "取消选择图片");
                    } else {
                        mTakePhotoResult.takeFailure(ERROR_CODE_DEF, "失败");
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK) {
                    setLocalId(mCameraUri);
                } else if (resultCode == RESULT_CANCELED) {
                    mTakePhotoResult.takeFailure(ERROR_CODE_DEF, "用户取消操作");
                } else {
                    mTakePhotoResult.takeFailure(ERROR_CODE_DEF, "失败");
                }
                break;
        }
    }

    /**
     * 设置回传LocalId
     *
     * @param oldUri
     */
    public void setLocalId(Uri oldUri) {
        Uri compressUri = UriUtils.getCompressUri(mContext, null);
        Uri uri = ImageUtils.compressImage(mContext, oldUri, compressUri, quality, maxLength, width, height, correctOrientation);
        if (uri != null) {
            String path = uri.getPath();
            String localId = "bhfile://" + path.substring(path.lastIndexOf("img/"));
            mTakePhotoResult.takeSuccess(localId);
        } else {
            mTakePhotoResult.takeFailure(ERROR_CODE_DEF, "失败");
        }
    }


    @Override
    public void onDestroy() {
    }
}
