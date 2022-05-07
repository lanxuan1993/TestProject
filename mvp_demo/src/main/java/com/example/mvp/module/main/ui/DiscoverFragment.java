package com.example.mvp.module.main.ui;

import android.util.Log;

import com.example.mvp.R;
import com.example.mvp.base.BaseFragment;

public class DiscoverFragment extends BaseFragment {
    private static final String TAG = DiscoverFragment.class.getSimpleName();

    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
        Log.i("TAG", "initData: DiscoverFragment");
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void business() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: DiscoverFragment");
    }
}