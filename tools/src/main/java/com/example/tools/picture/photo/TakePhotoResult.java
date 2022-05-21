package com.example.tools.picture.photo;

/**
 * @author : zhaobeibei
 * createDate   : 2020-06-11
 * desc   : 获取图片后的返回结果回调
 */
public interface TakePhotoResult {

    void takeSuccess(String response);

    void takeFailure(String code, String msg);
}
