package com.example.mvp.module.main.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mvp.R;
import com.example.mvp.base.BaseFragment;

public class ServiceFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ServiceFragment() {
        // Required empty public constructor
    }

    public static ServiceFragment newInstance(String param1, String param2) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_service;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.i("TAG", "initData: ServiceFragment");
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
        Log.i("TAG", "onResume: ServiceFragment");
    }
}