package com.example.mvp.module.main.ui;

import android.util.Log;
import android.view.View;

import com.jiongbook.evaluation.R;
import com.mvp.base.BaseFragment;


public class MineFragment extends BaseFragment {
    private static final String TAG = MineFragment.class.getSimpleName();

    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
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
        Log.i("TAG", "initData: MineFragment");
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "onResume: MineFragment");
    }
}