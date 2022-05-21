package com.mvp.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * author: ZhaoBeiBei
 * created on: 2022/04/23
 * description: activity基类
 */
public abstract class BaseActivity<P extends BasePresenter>
        extends AppCompatActivity {

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutView() != null) {
            setContentView(getLayoutView());
        } else {
            setContentView(getLayoutResId());
        }
        //创建Presenter
        mPresenter = createPresenter();
        //绑定View
        mPresenter.attachView((BaseView) this);
        initData();
        initView();
        requestData();
    }

    protected abstract View getLayoutView();

    protected abstract int getLayoutResId();

    /**
     * 创建Presenter
     */
    protected abstract P createPresenter();

    protected void initData() {
    }

    protected void initView() {
    }

    protected abstract void requestData();

    /**
     * 页面销毁时解除绑定
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
