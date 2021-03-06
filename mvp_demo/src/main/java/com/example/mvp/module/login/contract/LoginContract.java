package com.example.mvp.module.login.contract;

import com.mvp.base.BaseView;

/**
 * 契约：连接M,V,P三层（通过接口实现的方式）
 */
public interface LoginContract {
    /**
     * Model:数据的获取（一般是网络请求）
     */
    interface Model {
    }

    /**
     * ui控件等的展示
     */
    interface View extends BaseView {
    }


    /**
     * Presenter：代码逻辑的处理
     */
    interface Presenter {
        /**
         * 登陆
         *
         * @param username
         * @param password
         */
        void login(String username, String password);
    }
}
