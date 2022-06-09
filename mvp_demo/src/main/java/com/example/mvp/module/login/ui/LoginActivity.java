package com.example.mvp.module.login.ui;

import android.view.View;

import com.jiongbook.evaluation.R;
import com.mvp.base.BaseActivity;
import com.example.mvp.module.login.presenter.LoginPresenter;
import com.example.mvp.module.login.contract.LoginContract;

public class LoginActivity extends BaseActivity<LoginPresenter>
        implements LoginContract.View{

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected void requestData() {
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