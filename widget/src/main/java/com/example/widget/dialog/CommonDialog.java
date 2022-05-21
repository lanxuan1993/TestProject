package com.example.widget.dialog;

/*import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bhfae.fae.dev.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

*//**
 * @author: created by ZhaoBeibei on 2020-03-02 19:45
 * @describe: 普通dialog
 *//*
public class CommonDialog extends Dialog {
    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.tv_content)
    TextView contentTv;
    @BindView(R.id.tv_cancel)
    TextView cancelTv;
    @BindView(R.id.tv_sure)
    TextView sureTv;
    private Context mContext;
    private String title;
    private String content;
    private String sure;
    private String cancel;

    public CommonDialog(Context context, String title, String content, String cancel, String sure, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        this.title = title;
        this.content = content;
        this.cancel = cancel;
        this.sure = sure;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_common);
        ButterKnife.bind(this);

        if (TextUtils.isEmpty(content)) {
            contentTv.setVisibility(View.GONE);
        } else {
            contentTv.setVisibility(View.VISIBLE);
            contentTv.setText(content);
        }

        if (TextUtils.isEmpty(cancel)) {
            cancelTv.setVisibility(View.GONE);
        } else {
            cancelTv.setVisibility(View.VISIBLE);
            cancelTv.setText(cancel);
        }

        titleTv.setText(title);
        sureTv.setText(sure);
    }

    @OnClick({R.id.tv_cancel, R.id.tv_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                doCancel();
                dismiss();
                break;
            case R.id.tv_sure:
                confirm();
                break;
        }
    }

    public void confirm() {
        dismiss();
    }

    public void doCancel() {
    }

    *//**
     * dialog点击返回键和屏幕不消失
     *//*
    public void forbidCancel() {
        setCancelable(false); // dialog弹出后会点击屏幕或物理返回键，dialog不消失
        setCanceledOnTouchOutside(false); // dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
    }
}*/
