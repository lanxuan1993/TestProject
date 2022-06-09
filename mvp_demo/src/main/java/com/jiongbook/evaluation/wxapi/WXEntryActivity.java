package com.jiongbook.evaluation.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.tools.wechat.WechatUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONObject;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IWXAPI api = WechatUtils.getWxAPI(this);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        IWXAPI api = WechatUtils.getWxAPI(this);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        finish();
    }


    @Override
    public void onResp(BaseResp resp) {
        JSONObject jsonObject = WechatUtils.onWechatResp(resp);
        finish();
    }
}
