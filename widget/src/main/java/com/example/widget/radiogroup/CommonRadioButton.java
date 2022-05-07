package com.example.widget.radiogroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import com.example.tools.utils.DensityUtil;
import com.example.widget.R;

/**
 * @author: created by ZhaoBeibei on 2021/1/22 10:15
 * @describe: 自定义RadioButton
 * 1. 实现文字上下左右方向的图片大小设置
 */
public class CommonRadioButton extends androidx.appcompat.widget.AppCompatRadioButton {

    private float mImg_width;
    private float mImg_height;

    public CommonRadioButton(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public CommonRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.CommonRadioButton);
        mImg_width = t.getDimension(R.styleable.CommonRadioButton_img_width, DensityUtil.dp2px(context, 25));
        mImg_height = t.getDimension(R.styleable.CommonRadioButton_img_height, DensityUtil.dp2px(context, 25));
        t.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //让RadioButton的图标可调大小 属性：
        Drawable drawableLeft = this.getCompoundDrawables()[0];//获得文字左侧图片
        Drawable drawableTop = this.getCompoundDrawables()[1];//获得文字顶部图片
        Drawable drawableRight = this.getCompoundDrawables()[2];//获得文字右侧图片
        Drawable drawableBottom = this.getCompoundDrawables()[3];//获得文字底部图片
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(drawableLeft, null, null, null);
        }
        if (drawableRight != null) {
            drawableRight.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, null, drawableRight, null);
        }
        if (drawableTop != null) {
            drawableTop.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, drawableTop, null, null);
        }
        if (drawableBottom != null) {
            drawableBottom.setBounds(0, 0, (int) mImg_width, (int) mImg_height);
            this.setCompoundDrawables(null, null, null, drawableBottom);
        }
    }
}