package com.example.widget.textview;


import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {
    private static final String TAG = "CustomTextView";

    public CustomTextView(Context context) {
        super(context);
        FontUtil.setCustomFont(this, context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontUtil.setCustomFont(this, context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontUtil.setCustomFont(this, context);
    }
}

