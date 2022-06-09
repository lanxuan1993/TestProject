package com.example.tools.wechat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.URLUtil;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXEmojiObject;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author: created by ZhaoBeibei on 2022-06-09
 * @describe: 集成微信sdk--核心
 *
 * 1.包名相应目录下新建一个 wxapi目录，并在该 wxapi目录下新增一个WXEntryActivity类，
 * 并在 manifest 文件里面加上exported、taskAffinity及 launchMode 属性，
 * 其中 exported 设置为true，taskAffinity设置为你的包名，launchMode设置为singleTask
 *        <activity
 *             android:name=".wxapi.WXEntryActivity"
 *             android:label="@string/app_name"
 *             android:theme="@android:style/Theme.Translucent.NoTitleBar"
 *             android:exported="true"
 *             android:taskAffinity="${applicationId}"
 *             android:launchMode="singleTask">
 *         </activity>
 *
 * 2.申请微信的AppId
 *
 *
 */
public class WechatUtils {
    public static final String TAG = WechatUtils.class.getName();
//    微信测试AppId
    public static final String WECHAT_APP_ID = "wxeafdb35d04e3713c";

    private static final String ERROR_WECHAT_OPEN_APP_ERROR = "跳转微信失败";
    private static final String ERROR_WECHAT_NOT_INSTALLED = "未安装微信";
    private static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    private static final String ERROR_SEND_REQUEST_FAILED = "发送请求失败";
    private static final String ERROR_JSON_EXCEPTION = "解析异常";
    private static final String ERROR_WECHAT_RESPONSE_COMMON = "普通错误";
    private static final String ERROR_WECHAT_RESPONSE_USER_CANCEL = "用户点击取消并返回";
    private static final String ERROR_WECHAT_RESPONSE_SENT_FAILED = "发送失败";
    private static final String ERROR_WECHAT_RESPONSE_AUTH_DENIED = "授权失败";
    private static final String ERROR_WECHAT_RESPONSE_UNSUPPORT = "微信不支持";
    private static final String ERROR_WECHAT_RESPONSE_UNKNOWN = "未知错误";

    private static final String EXTERNAL_STORAGE_IMAGE_PREFIX = "external://";

    private static final String KEY_ARG_MESSAGE = "message";
    private static final String KEY_ARG_SCENE = "scene";
    private static final String KEY_ARG_TEXT = "text";
    private static final String KEY_ARG_MESSAGE_TITLE = "title";
    private static final String KEY_ARG_MESSAGE_DESCRIPTION = "description";
    private static final String KEY_ARG_MESSAGE_THUMB = "thumb";
    private static final String KEY_ARG_MESSAGE_MEDIA = "media";
    private static final String KEY_ARG_MESSAGE_MEDIA_TYPE = "type";
    private static final String KEY_ARG_MESSAGE_MEDIA_WEBPAGEURL = "webpageUrl";
    private static final String KEY_ARG_MESSAGE_MEDIA_IMAGE = "image";
    private static final String KEY_ARG_MESSAGE_MEDIA_MUSICURL = "musicUrl";
    private static final String KEY_ARG_MESSAGE_MEDIA_MUSICDATAURL = "musicDataUrl";
    private static final String KEY_ARG_MESSAGE_MEDIA_VIDEOURL = "videoUrl";
    private static final String KEY_ARG_MESSAGE_MEDIA_FILE = "file";
    private static final String KEY_ARG_MESSAGE_MEDIA_EMOTION = "emotion";
    private static final String KEY_ARG_MESSAGE_MEDIA_EXTINFO = "extInfo";
    private static final String KEY_ARG_MESSAGE_MEDIA_URL = "url";
    private static final String KEY_ARG_USER_NAME = "userName";
    private static final String KEY_ARG_PATH = "path";
    private static final String KEY_ARG_MP_TYPE = "miniProgramType";

    private static final int TYPE_WECHAT_SHARING_APP = 1;
    private static final int TYPE_WECHAT_SHARING_EMOTION = 2;
    private static final int TYPE_WECHAT_SHARING_FILE = 3;
    private static final int TYPE_WECHAT_SHARING_IMAGE = 4;
    private static final int TYPE_WECHAT_SHARING_MUSIC = 5;
    private static final int TYPE_WECHAT_SHARING_VIDEO = 6;
    private static final int TYPE_WECHAT_SHARING_WEBPAGE = 7;
    private static final int TYPE_WECHAT_SHARING_MINI_PROGRAM = 8;

    private static final int SCENE_SESSION = 0;
    private static final int SCENE_TIMELINE = 1;
    private static final int SCENE_FAVORITE = 2;

    private static final int MAX_THUMBNAIL_SIZE = 320;

    protected static IWXAPI wxAPI;


    /**
     * 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
     *
     * @param mContext
     * @return
     */
    public static IWXAPI getWxAPI(Context mContext) {
        if (wxAPI == null) {
            wxAPI = WXAPIFactory.createWXAPI(mContext, WECHAT_APP_ID, true);
        }
        return wxAPI;
    }

    /**
     * 将应用的 appId 注册到微信
     */
    public static void registerApp(Context mContext) {
        IWXAPI api = getWxAPI(mContext);
        if (api != null) {
            api.registerApp(WECHAT_APP_ID);
        }
    }


    /**
     * 微信是否安装
     */
    public static boolean isWxInstalled(Context mContext) {
        boolean isWxInstalled = false;
        IWXAPI api = getWxAPI(mContext);
        if (api != null) {
            isWxInstalled = api.isWXAppInstalled();
        }
        return isWxInstalled;
    }


    /**
     * 打开微信
     */
    public static boolean openWx(Context mContext) {
        boolean isOpenWxSuc = false;
        IWXAPI api = getWxAPI(mContext);
        if (api != null && api.isWXAppInstalled()) {
            isOpenWxSuc = api.openWXApp();
        }
        return isOpenWxSuc;
    }


    /**
     * 微信分享
     *
     * @param mContext
     * @param params
     * @throws JSONException
     */
    public static void shareWx(Context mContext, final JSONObject params) {
        if (!isWxInstalled(mContext)) {
            return;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction();
        req.scene = SendMessageToWX.Req.WXSceneTimeline;

        try {
            if (params.has(KEY_ARG_SCENE)) {
                switch (params.getInt(KEY_ARG_SCENE)) {
                    case SCENE_FAVORITE: //分享到收藏
                        req.scene = SendMessageToWX.Req.WXSceneFavorite;
                        break;
                    case SCENE_SESSION: //分享到对话
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        break;
                    case SCENE_TIMELINE: //分享到朋友圈
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // run in background
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    req.message = buildSharingMessage(mContext, params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean isSendSuc = getWxAPI(mContext).sendReq(req);
                Log.i(TAG, "Message has been sent state:" + isSendSuc);
            }
        }).start();

    }


    /**
     * transaction字段用与唯一标示一个请求
     *
     * @return
     */
    private static String buildTransaction() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * transaction字段用与唯一标示一个请求
     *
     * @return
     */
    private static String buildTransaction(String type) {
        return type + System.currentTimeMillis();
    }

    /**
     * 分享信息
     *
     * @param params
     * @return
     * @throws JSONException
     */
    protected static WXMediaMessage buildSharingMessage(Context mContext, JSONObject params) throws JSONException {
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        // media parameters
        WXMediaMessage.IMediaObject mediaObject = null;
        if (params.has(KEY_ARG_TEXT)) {
            WXTextObject textObject = new WXTextObject();
            textObject.text = params.getString(KEY_ARG_TEXT);
            mediaObject = textObject;
            wxMediaMessage.description = textObject.text;
        } else {
            JSONObject message = params.getJSONObject(KEY_ARG_MESSAGE);
            JSONObject media = message.getJSONObject(KEY_ARG_MESSAGE_MEDIA);
            wxMediaMessage.title = message.getString(KEY_ARG_MESSAGE_TITLE);
            wxMediaMessage.description = message.getString(KEY_ARG_MESSAGE_DESCRIPTION);

            // thumbnail
            Bitmap thumbnail = getThumbnail(mContext, message, KEY_ARG_MESSAGE_THUMB);
            if (thumbnail != null) {
                wxMediaMessage.setThumbImage(thumbnail);
                thumbnail.recycle();
            }

            // check types
            int type = media.has(KEY_ARG_MESSAGE_MEDIA_TYPE) ?
                    media.getInt(KEY_ARG_MESSAGE_MEDIA_TYPE) : TYPE_WECHAT_SHARING_WEBPAGE;
            switch (type) {
                case TYPE_WECHAT_SHARING_APP:
                    WXAppExtendObject appObject = new WXAppExtendObject();
                    appObject.extInfo = media.getString(KEY_ARG_MESSAGE_MEDIA_EXTINFO);
                    appObject.filePath = media.getString(KEY_ARG_MESSAGE_MEDIA_URL);
                    mediaObject = appObject;
                    break;
                case TYPE_WECHAT_SHARING_EMOTION:
                    WXEmojiObject emoObject = new WXEmojiObject();
                    InputStream emoji = getFileInputStream(mContext, media.getString(KEY_ARG_MESSAGE_MEDIA_EMOTION));
                    if (emoji != null) {
                        try {
                            emoObject.emojiData = Util.readBytes(emoji);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaObject = emoObject;
                    break;
                case TYPE_WECHAT_SHARING_FILE:
                    WXFileObject fileObject = new WXFileObject();
                    fileObject.filePath = media.getString(KEY_ARG_MESSAGE_MEDIA_FILE);
                    mediaObject = fileObject;
                    break;
                case TYPE_WECHAT_SHARING_IMAGE:
                    Bitmap image = getBitmap(mContext, message.getJSONObject(KEY_ARG_MESSAGE_MEDIA), KEY_ARG_MESSAGE_MEDIA_IMAGE, 0);
                    mediaObject = new WXImageObject(image);
                    if (image != null) {
                        image.recycle();
                    }
                    break;
                case TYPE_WECHAT_SHARING_MUSIC:
                    WXMusicObject musicObject = new WXMusicObject();
                    musicObject.musicUrl = media.getString(KEY_ARG_MESSAGE_MEDIA_MUSICURL);
                    musicObject.musicDataUrl = media.getString(KEY_ARG_MESSAGE_MEDIA_MUSICDATAURL);
                    mediaObject = musicObject;
                    break;
                case TYPE_WECHAT_SHARING_VIDEO:
                    WXVideoObject videoObject = new WXVideoObject();
                    videoObject.videoUrl = media.getString(KEY_ARG_MESSAGE_MEDIA_VIDEOURL);
                    mediaObject = videoObject;
                    break;
                case TYPE_WECHAT_SHARING_MINI_PROGRAM:
                    WXMiniProgramObject mpObject = new WXMiniProgramObject();
                    mpObject.webpageUrl = media.getString(KEY_ARG_MESSAGE_MEDIA_WEBPAGEURL); //低版本网页链接
                    mpObject.userName = media.getString(KEY_ARG_USER_NAME); //小程序username
                    mpObject.path = media.getString(KEY_ARG_PATH); //小程序页面的路径
                    mpObject.withShareTicket = true; //是否使用带 shareTicket 的转发
                    mpObject.miniprogramType = media.getInt(KEY_ARG_MP_TYPE); // 分享小程序的版本（正式，开发，体验）
                    mediaObject = mpObject;
                    break;
                case TYPE_WECHAT_SHARING_WEBPAGE:
                default:
                    mediaObject = new WXWebpageObject(media.getString(KEY_ARG_MESSAGE_MEDIA_WEBPAGEURL));
            }
        }

        wxMediaMessage.mediaObject = mediaObject;
        return wxMediaMessage;
    }

    protected static Bitmap getThumbnail(Context mContext, JSONObject message, String key) {
        return getBitmap(mContext, message, key, MAX_THUMBNAIL_SIZE);
    }

    protected static Bitmap getBitmap(Context mContext, JSONObject message,
                                      String key, int maxSize) {
        Bitmap bmp = null;
        String url = null;

        try {
            if (!message.has(key)) {
                return null;
            }

            url = message.getString(key);

            // get input stream
            InputStream inputStream = getFileInputStream(mContext, url);
            if (inputStream == null) {
                return null;
            }

            // decode it
            // @TODO make sure the image is not too big, or it will cause out of memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            bmp = BitmapFactory.decodeStream(inputStream, null, options);

            // scale
            if (maxSize > 0 && (options.outWidth > maxSize || options.outHeight > maxSize)) {
                Log.d(TAG, String.format("Bitmap was decoded, dimension: %d x %d, max allowed size: %d.",
                        options.outWidth, options.outHeight, maxSize));

                int width = 0;
                int height = 0;

                if (options.outWidth > options.outHeight) {
                    width = maxSize;
                    height = width * options.outHeight / options.outWidth;
                } else {
                    height = maxSize;
                    width = height * options.outWidth / options.outHeight;
                }

                Bitmap scaled = Bitmap.createScaledBitmap(bmp, width, height, true);
                bmp.recycle();

                bmp = scaled;
            }

            inputStream.close();
        } catch (JSONException e) {
            bmp = null;
            e.printStackTrace();
        } catch (IOException e) {
            bmp = null;
            e.printStackTrace();
        }

        return bmp;
    }

    /**
     * Get input stream from a url
     *
     * @param url
     * @return
     */
    protected static InputStream getFileInputStream(Context mContext, String url) {
        try {
            InputStream inputStream = null;

            if (URLUtil.isHttpUrl(url) || URLUtil.isHttpsUrl(url)) {
                File file = Util.downloadAndCacheFile(mContext, url);
                if (file == null) {
                    Log.d(TAG, String.format("File could not be downloaded from %s.", url));
                    return null;
                }

                url = file.getAbsolutePath();
                inputStream = new FileInputStream(file);

                Log.d(TAG, String.format("File was downloaded and cached to %s.", url));
            } else if (url.startsWith("data:image")) {  // base64 image

                String imageDataBytes = url.substring(url.indexOf(",") + 1);
                byte imageBytes[] = Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT);
                inputStream = new ByteArrayInputStream(imageBytes);

                Log.d(TAG, "Image is in base64 format.");

            } else if (url.startsWith(EXTERNAL_STORAGE_IMAGE_PREFIX)) { // external path

                url = Environment.getExternalStorageDirectory().getAbsolutePath() + url.substring(EXTERNAL_STORAGE_IMAGE_PREFIX.length());
                inputStream = new FileInputStream(url);

                Log.d(TAG, String.format("File is located on external storage at %s.", url));

            } else if (!url.startsWith("/")) { // relative path
                inputStream = mContext.getApplicationContext().getAssets().open(url);
                Log.d(TAG, String.format("File is located in assets folder at %s.", url));
            } else {
                inputStream = new FileInputStream(url);
                Log.d(TAG, String.format("File is located at %s.", url));
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
     * 微信-跳转小程序
     *
     * @param params
     */
    public static void launchMiniProgram(Context mContext, JSONObject params) {
        if (!isWxInstalled(mContext)) {
            return;
        }

        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        try {
            req.userName = params.getString(KEY_ARG_USER_NAME); // 填小程序原始id
            req.path = params.getString(KEY_ARG_PATH);                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
            req.miniprogramType = params.getInt(KEY_ARG_MP_TYPE);// （0:正式，1:开发，2:体验）
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                IWXAPI api = getWxAPI(mContext);
                if (api.sendReq(req)) {
                    Log.i(TAG, "Message has been sent successfully.");
                } else {
                    Log.i(TAG, "Message has been sent unsuccessfully.");
                }
            }
        }).start();
    }


    /**
     * 微信响应结果封装
     *
     * @param resp
     */
    public static JSONObject onWechatResp(BaseResp resp) {
        Log.d(TAG, "微信响应结果: " + resp.toString());
        JSONObject jsonObject = null;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM:
                        WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) resp;
                        jsonObject = setResultJSON("00", launchMiniProResp.extMsg);
                        break;
                    default:
                        jsonObject = setResultJSON("00", "成功");
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_USER_CANCEL);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_AUTH_DENIED);
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_SENT_FAILED);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_UNSUPPORT);
                break;
            case BaseResp.ErrCode.ERR_COMM:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_COMMON);
                break;
            default:
                jsonObject = setResultJSON("01", ERROR_WECHAT_RESPONSE_UNKNOWN);
                break;
        }

        return jsonObject;
    }


    public static JSONObject setResultJSON(String code, String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}

