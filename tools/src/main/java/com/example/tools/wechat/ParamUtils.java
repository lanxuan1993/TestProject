package com.example.tools.wechat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;

/**
 * @author: created by ZhaoBeibei on 2020-05-21 18:30
 * @describe: 微信分享解析参数工具
 */
public class ParamUtils {
    //Scene
    public static final int SESSION = 0;// 聊天界面
    public static final int TIMELINE = 1;// 朋友圈
    public static final int FAVORITE = 2;// 收藏

    //  Type
    public static final int APP = 1;
    public static final int EMOTION = 2;
    public static final int FILE = 3;
    public static final int IMAGE = 4;
    public static final int MUSIC = 5;
    public static final int VIDEO = 6;
    public static final int WEBPAGE = 7;
    public static final int MINI_PROGRAM = 8;
    public static final int TEXT = 9;


    /**
     * 微信分享文本内容
     *
     * @param jsonParams
     * @return
     * @throws JSONException
     */
    public static JSONObject WXShareText(JSONObject jsonParams) throws JSONException {
        int scene = jsonParams.optInt("scene",SESSION);
        String text = jsonParams.optString("text");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", TEXT);
        jsonObject.put("text", URLDecoder.decode(text));
        jsonObject.put("scene", scene);
        return jsonObject;
    }

    /**
     * 微信分享链接
     *
     * @param jsonParams
     * @return
     * @throws JSONException
     */
    public static JSONObject WXShareLink(JSONObject jsonParams) throws JSONException {
        int scene = jsonParams.optInt("scene",TIMELINE);
        String title = jsonParams.optString("title");
        String description = jsonParams.optString("description");
        String webPageUrl = jsonParams.optString("webPageUrl");
        String thumb = jsonParams.optString("thumb");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("type", WEBPAGE);
        jsonObject2.put("webpageUrl", URLDecoder.decode(webPageUrl));
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", URLDecoder.decode(title));
        jsonObject1.put("description", URLDecoder.decode(description));
        jsonObject1.put("thumb", thumb);
        jsonObject1.put("media", jsonObject2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", jsonObject1);
        jsonObject.put("scene", scene);

        return jsonObject;
    }

    /**
     * 微信分享图片 base64
     *
     * @param jsonParams
     * @return
     * @throws JSONException
     */
    public static JSONObject WXShareImage(JSONObject jsonParams) throws JSONException {
        int scene = jsonParams.optInt("scene",TIMELINE);
        String title = "";
        String description = "";
        String image = jsonParams.optString("image");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("type", IMAGE);
        jsonObject2.put("image", image);
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", title);
        jsonObject1.put("description", description);
        jsonObject1.put("thumb", "");
        jsonObject1.put("media", jsonObject2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", jsonObject1);
        jsonObject.put("scene", scene);

        return jsonObject;
    }


    /**
     * 微信分享小程序
     *
     * @param jsonParams
     * @return
     * @throws JSONException
     */
    public static JSONObject WXShareMiniProgram(JSONObject jsonParams) throws JSONException {
        String title = jsonParams.optString("title");
        String description = jsonParams.optString("description");
        String webPageUrl = jsonParams.optString("webPageUrl");
        String userName = jsonParams.optString("userName");
        String image = jsonParams.optString("image");
        String path = jsonParams.optString("path");
        String type = jsonParams.optString("type");

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("type", MINI_PROGRAM);
        jsonObject2.put("webpageUrl", URLDecoder.decode(webPageUrl));
        jsonObject2.put("userName", userName);
        jsonObject2.put("path", URLDecoder.decode(path));
        jsonObject2.put("image", image);
        jsonObject2.put("miniProgramType", Integer.valueOf(type));
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("title", URLDecoder.decode(title));
        jsonObject1.put("description", URLDecoder.decode(description));
        jsonObject1.put("thumb", image);
        jsonObject1.put("media", jsonObject2);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", jsonObject1);
        jsonObject.put("scene", SESSION);

        return jsonObject;
    }






    /**
     * 跳转小程序
     *
     * @param jsonParams
     * @return
     * @throws JSONException
     */
    public static JSONObject WXLaunchMiniProgram(JSONObject jsonParams) throws JSONException {
        String userName = jsonParams.optString("userName");
        String path = jsonParams.optString("path");
        String miniProgramType = jsonParams.optString("miniProgramType");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userName", userName);
        jsonObject.put("path", URLDecoder.decode(path));
        jsonObject.put("miniProgramType", Integer.valueOf(miniProgramType));
        return jsonObject;
    }
}
