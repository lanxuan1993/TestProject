package com.example.mvp.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * author: ZhaoBeiBei
 * created on: 2022/04/23
 * description: activity基类
 */
public abstract class BaseActivity<P extends BasePresenter>
        extends AppCompatActivity{

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //创建Presenter
        mPresenter = createPresenter();
        //绑定View
        mPresenter.attachView((BaseView) this);

        initData();
        initView();
        business();
    }

    protected abstract int getLayoutResId();

    /**
     * 创建Presenter
     */
    protected abstract P createPresenter();

    protected void initData() {
    }

    protected void initView() {
    }

    protected abstract void business();

    /**
     * 页面销毁时解除绑定
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
