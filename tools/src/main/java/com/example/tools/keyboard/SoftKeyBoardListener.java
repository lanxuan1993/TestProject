package com.example.tools.keyboard;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author: created by ZhaoBeibei on 2021/5/24 13:51
 * @describe:
 */
public class SoftKeyBoardListener {
    private static final String TAG = "SoftKeyBoard";

    private View rootView;//activity的根视图
    private int rootViewVisibleHeight;//纪录根视图的显示高度
    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

    public SoftKeyBoardListener(Activity activity) {
        //获取activity的根视图
        rootView = activity.getWindow().getDecorView();

        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取当前根视图在屏幕上显示的大小
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int visibleHeight = r.height();
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }

                //根视图显示高度变小超过200，可以看作软键盘显示了
                if (rootViewVisibleHeight - visibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        int height = rootViewVisibleHeight - visibleHeight;
                        Log.i(TAG, "keyBoardShow: " + height);
                        onSoftKeyBoardChangeListener.keyBoardShow(height);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

                //根视图显示高度变大超过200，可以看作软键盘隐藏了
                if (visibleHeight - rootViewVisibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        int height = visibleHeight - rootViewVisibleHeight;
                        Log.i(TAG, "keyBoardHide: " + height);
                        onSoftKeyBoardChangeListener.keyBoardHide(height);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

            }
        });
    }

    private void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    public interface OnSoftKeyBoardChangeListener {
        void keyBoardShow(int height);

        void keyBoardHide(int height);
    }

    public void setListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener);
    }


    /**
     * 输入框位置上移
     *
     * @param activity
     * @param editText
     * @param height
     */
    public void setEditMoveUp(Activity activity, EditText editText, int height, TextView view) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int heightPixels = outMetrics.heightPixels;
        int[] l = {0, 0};
        editText.getLocationInWindow(l);
        //得到输入框在屏幕中上下左右的位置
        int left = l[0], top = l[1], bottom = top + editText.getHeight(), right = left + editText.getWidth();
        int mEditBottom = bottom + 20;
        int mKeyboardTop = heightPixels - height;
        Log.i(TAG, "keyBoardShow: 输入框底部位置:" + mEditBottom);
        Log.i(TAG, "keyBoardShow: 软键盘顶部位置:" + mKeyboardTop);
        if (mEditBottom > mKeyboardTop) { //键盘遮住了编辑框
            int distance = mEditBottom - mKeyboardTop;
            setTopMargin(distance, view);
        }
    }


    public void setTopMargin(int topMargin, View view) {
        Log.i(TAG, "topMargin: 上移距离:" + topMargin);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.setMargins(lp.leftMargin, topMargin, lp.rightMargin, lp.bottomMargin);
        view.setLayoutParams(lp);
    }

    public void setTopMargin(int topMargin, ViewGroup view) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
        lp.setMargins(0, topMargin, 0, 0);
        view.setLayoutParams(lp);
    }
}

