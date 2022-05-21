package com.example.mvp.module.main.ui;

import android.util.Log;

import com.example.mvp.R;
import com.example.net.OkHttpManager;
import com.example.tools.utils.FileUtils;
import com.mvp.base.BaseFragment;

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
    protected void initView() {
        super.initView();
    }

    @Override
    protected void requestData() {
        String url = "https://files.cnblogs.com/pingyangcst/SrcSamples%E4%BF%AE%E6%94%B9%E8%BF%87.rar";
        String path = getActivity().getExternalFilesDir(null)+"/test";
        File file = FileUtils.createFile(path,"test.rar");
        OkHttpManager.getInstance().downloadFile(url, null, null, file, new OkHttpManager.FileCallBack() {
            @Override
            public void success(okhttp3.Response response) throws IOException {

            }

            @Override
            public void failed(String error) {

            }

            @Override
            public void progress(String progress) {
                Log.d(TAG, "progress: "+progress);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: HomeFragment");
    }
}