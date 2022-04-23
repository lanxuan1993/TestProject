package com.example.mvp.base;

import java.lang.ref.WeakReference;

/**
 * author: ZhaoBeiBei
 * created on: 2022/04/23
 * description: Presenter基类 操作视图View和Model的控制层
 */
public class BasePresenter<V extends BaseView> {

    //弱引用View
    protected WeakReference<V> mWeakReference;
    private V mView;

    /**
     * 绑定View
     *
     * @param view
     */
    public void attachView(V view) {
        mView = view;
        mWeakReference = new WeakReference<V>(view);
    }

    /**
     * 解绑View
     */
    public void detachView() {
        mView = null;
        if (mWeakReference != null) {
            mWeakReference.clear();
            mWeakReference = null;
        }
    }

    /**
     * 获取view
     *
     * @return
     */
    public V getView() {
        if (mWeakReference != null) {
            return mWeakReference.get();
        }
        return null;
    }

    /**
     * View是否绑定
     *
     * @return
     */
    public boolean isViewAttached() {
        return mView != null;
    }
}

