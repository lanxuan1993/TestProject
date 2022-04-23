package com.example.mvp.module.login.presenter;

import com.example.mvp.base.BasePresenter;
import com.example.mvp.module.login.contract.LoginContract;

public class LoginPresenter extends BasePresenter<LoginContract.View>
        implements LoginContract.Presenter{

    @Override
    public void login(String username, String password) {

    }
}
