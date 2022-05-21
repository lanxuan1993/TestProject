package com.example.tools.picture;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.URLUtil;

import androidx.fragment.app.FragmentActivity;

import com.example.tools.permission.PermissionUtils;
import com.example.tools.picture.photo.TakePhotoManager;
import com.example.tools.picture.photo.TakePhotoResult;
import com.example.tools.picture.photo.utils.ImageUtils;
import com.example.tools.picture.photo.utils.UriUtils;
import com.example.tools.picture.save.ImageAlbum;
import com.example.tools.utils.CommonUtils;
import com.example.tools.utils.FileUtils;
import com.tbruyelle.rxpermissions3.Permission;
import com.tbruyelle.rxpermissions3.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: created by ZhaoBeibei on 2020-06-12 10:14
 * @describe: 选取图片核心类
 */
public class BHCorePicture {
    private static final String TAG = BHCorePicture.class.getName();
    public static final String SUCCESS_CODE = "0000";
    private static final String SUCCESS_MSG = "success";
    private static final String ERROR_CODE_DEF = "-9999";
    private static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    private static final String EXTERNAL_STORAGE_IMAGE_PREFIX = "external://";
    private BHPictureCallBack mCommonCallBack;
    private TakePhotoManager takePhotoManager;
    private Context mContext;


    public BHCorePicture(Context context) {
        mContext = context;
        takePhotoManager = new TakePhotoManager(context);
    }

    /**
     * 选取照片(照相,相册选取图片)
     *
     * @param jsonObject
     * @param callBack
     * @throws JSONException
     */
    public void chooseImage(JSONObject jsonObject, BHPictureCallBack callBack) throws JSONException {
        mCommonCallBack = callBack;
        int type = jsonObject.getInt("type");
        int quality = 50;
        int maxLength = 0;
        int width = 0;
        int height = 0;
        boolean correctOrientation = true;

        if (jsonObject.has("quality")) {
            quality = jsonObject.getInt("quality");
            if (quality > 100 || quality < 0) {
                quality = 50;
            }
        }
        if (jsonObject.has("maxLength")) {
            maxLength = jsonObject.getInt("maxLength");
        }
        if (jsonObject.has("width")) {
            width = jsonObject.getInt("width");
        }
        if (jsonObject.has("height")) {
            height = jsonObject.getInt("height");
        }

        // 1:相册,其他:相机
        if (type == 1) {
            takePhotoManager.openAlbum(quality, maxLength, width, height, correctOrientation)
                    .build(new TakePhotoResult() {
                        @Override
                        public void takeSuccess(String localId) {
                            successCallBack("localId", localId);
                        }

                        @Override
                        public void takeFailure(String code, String msg) {
                            failedCallBack(code, msg);
                        }
                    });
        } else {
            takePhotoManager.openCamera(quality, maxLength, width, height, correctOrientation)
                    .build(new TakePhotoResult() {
                        @Override
                        public void takeSuccess(String localId) {
                            successCallBack("localId", localId);
                        }

                        @Override
                        public void takeFailure(String code, String msg) {
                            failedCallBack(code, msg);
                        }
                    });
        }
    }


    /**
     * 根据图片localId(例如:"bhfile://img/a78ea3dc.jpg")获取Base64数据
     *
     * @param jsonObject
     * @param callBack
     * @throws JSONException
     */
    public void getLocalImageData(JSONObject jsonObject, BHPictureCallBack callBack) {
        mCommonCallBack = callBack;
        String localId = jsonObject.optString("localId");
        String fileName = localId.substring(localId.lastIndexOf("/"));
        String path = FileUtils.getFilePath(mContext, Environment.DIRECTORY_PICTURES) + "/img" + fileName;
        File file = new File(path);
        Uri uri = UriUtils.getUriFromFile(mContext, file);
        String base64Img = ImageUtils.imgToBase64(mContext, uri);
        successCallBack("img", base64Img);
    }


    /**
     * 保存图片到相册
     *
     * @param jsonObject
     * @param callBack
     */
    public void saveImage(JSONObject jsonObject, BHPictureCallBack callBack) {
        mCommonCallBack = callBack;
        String url = jsonObject.optString("url");
        if (TextUtils.isEmpty(url)) {
            failedCallBack(ERROR_CODE_DEF, ERROR_INVALID_PARAMETERS);
            return;
        }
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        FragmentActivity activity = (FragmentActivity) mContext;
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.requestEach(permissions)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {// 用户同意该权限
                            String fileName = "bhfae" + System.currentTimeMillis() + ".jpg";
                            InputStream inputStream = getInputStream(url);
                            boolean isSaveSuc = ImageAlbum.getInstance(mContext).savaImageInputStream(inputStream, fileName);
                            if (isSaveSuc) {
                                successCallBack("保存图片成功");
                            } else {
                                failedCallBack(ERROR_CODE_DEF, "保存图片失败");
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {//禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示

                        } else {// 用户拒绝了该权限，而且选中『不再询问』

                        }
                    }
                });
    }


    /**
     * Get input stream from a url
     *
     * @param url
     * @return
     */
    protected InputStream getInputStream(String url) {
        try {
            InputStream inputStream = null;

            if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                URL mURL = new URL(url);
                inputStream = mURL.openStream();

            } else if (url.startsWith("data:image")) {  // base64 image
                String imageDataBytes = url.substring(url.indexOf(",") + 1);
                byte imageBytes[] = Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT);
                inputStream = new ByteArrayInputStream(imageBytes);

            } else if (url.startsWith(EXTERNAL_STORAGE_IMAGE_PREFIX)) { // external path
                url = Environment.getExternalStorageDirectory().getAbsolutePath() + url.substring(EXTERNAL_STORAGE_IMAGE_PREFIX.length());
                inputStream = new FileInputStream(url);

            } else if (url.startsWith("bhfile://img/")) {  // localId
                String fileName = url.substring(url.lastIndexOf("/"));
                String path = FileUtils.getFilePath(mContext, Environment.DIRECTORY_PICTURES) + "/img" + fileName;
                File file = new File(path);
                if (file.exists()) {
                    inputStream = new FileInputStream(path);
                }

            } else if (!url.startsWith("/")) { // relative path
                inputStream = mContext.getApplicationContext().getAssets().open(url);

            } else {
                inputStream = new FileInputStream(url);
            }

            return inputStream;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 删除图片
     *
     * @param context
     */
    public void clearPhotoCache(Context context) {
        String filePath = FileUtils.getFilePath(context, Environment.DIRECTORY_PICTURES) + "/img/";
        FileUtils.delFile(filePath);
    }

    /*---------------------------------------返回数据封装---------------------------------------------*/

    private void successCallBack(String key, String params) {
        try {
            JSONObject data = new JSONObject();
            data.put(key, params);
            JSONObject result = getResultData(SUCCESS_CODE, SUCCESS_MSG, data);
            mCommonCallBack.getCallBack(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void successCallBack(String message) {
        JSONObject result = getResultData(SUCCESS_CODE, message, null);
        mCommonCallBack.getCallBack(result);
    }

    private void failedCallBack(String code, String message) {
        JSONObject result = getResultData(code, message, null);
        mCommonCallBack.getCallBack(result);
    }

    private JSONObject getResultData(String code, String message, JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        code = (code == null) ? ERROR_CODE_DEF : code;
        message = (message == null) ? "" : message;
        try {
            jsonObject.put("code", code);
            jsonObject.put("msg", message);
            if (data != null) {
                jsonObject.put("body", data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
