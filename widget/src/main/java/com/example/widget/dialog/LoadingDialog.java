package com.example.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.example.widget.R;

/**
 * @author: created by ZhaoBeibei on 2021/4/15 17:54
 * @describe: 加载框
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.MyDarkDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_loading);
        Window window = getWindow();
        window.setDimAmount(0f);//dialog框外透明
        setCanceledOnTouchOutside(false);//点击弹窗外部不消失，物理返回键消失
    }
}

//    使用
//    LoadingDialog dialog = new LoadingDialog(this);
//    dialog.show();

