package com.mvp.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * author: ZhaoBeiBei
 * created on: 2022/04/23
 * description:
 */
public abstract class BaseDialog extends Dialog {

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        init();
    }

    protected abstract int setLayout();

    protected abstract void init();
}
