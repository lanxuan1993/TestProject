package com.example.tools.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tools.R;

public class ToastUtils {
    public static void show(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showLong(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 无图 的Toast提示
     * 居中显示
     * duration :自定义时长   如果不需要自定义则传0，显示默认时长（LENGTH_SHORT）, 1为 LENGTH_LONG
     */
    public static void showToast(Context context, String message, int duration) {
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast_corner, null);
        TextView tv = toastRoot.findViewById(R.id.message);
        tv.setText(message);
        Toast toast = new Toast(context);
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER, 0, 0);
        if (duration == 0) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else if (duration == 1) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else { //自定义时长
            toast.setDuration(duration);
        }
        toast.show();
    }

//    public static void showImageMessageLong(Context context, String message) {
//        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
//        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setView(toastRoot);
//        TextView tv = toastRoot.findViewById(R.id.message);
//        tv.setText(message);
//        toast.show();
//    }

}
