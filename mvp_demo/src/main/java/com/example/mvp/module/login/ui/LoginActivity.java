package com.example.mvp.module.login.ui;

import com.example.mvp.R;
import com.example.mvp.base.BaseActivity;
import com.example.mvp.module.login.presenter.LoginPresenter;
import com.example.mvp.module.login.contract.LoginContract;

public class LoginActivity extends BaseActivity<LoginPresenter>
        implements LoginContract.View{

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void business() {
       mPresenter.login("","");
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}