package com.example.mvp.module.main.ui;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.net.OkHttpManager;
import com.example.tools.utils.FileUtils;
import com.example.tools.wechat.ParamUtils;
import com.example.tools.wechat.WechatUtils;
import com.jiongbook.evaluation.R;
import com.mvp.base.BaseFragment;


import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class HomeFragment extends BaseFragment {
    private static final String TAG = HomeFragment.class.getSimpleName();

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.i("TAG", "initData: HomeFragment");
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);

        TextView downloadTv = rootView.findViewById(R.id.tv_download);
        TextView shareWxTv = rootView.findViewById(R.id.tv_share_wx);
        downloadTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

        shareWxTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", ParamUtils.TEXT);
                    jsonObject.put("text", "你好，世界");
                    jsonObject.put("scene", ParamUtils.SESSION);
                    JSONObject shareParam = ParamUtils.WXShareText(jsonObject);
                    WechatUtils.shareWx(getActivity(),shareParam);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: HomeFragment");
    }

    /**
     * 下载文件
     */
    public void downloadFile(){
        String url = "https://files.cnblogs.com/pingyangcst/SrcSamples%E4%BF%AE%E6%94%B9%E8%BF%87.rar";
        String path = getActivity().getExternalFilesDir(null) + "/test";
        File file = FileUtils.createFile(path, "test.rar");
        OkHttpManager.getInstance().downloadFile(url, null, null, file, new OkHttpManager.FileCallBack() {
            @Override
            public void success(okhttp3.Response response) throws IOException {
            }

            @Override
            public void failed(String error) {
            }

            @Override
            public void progress(String progress) {
                Log.d(TAG, "progress: " + progress);
            }
        });

    }
}