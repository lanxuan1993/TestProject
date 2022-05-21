package com.example.tools.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * @author: created by ZhaoBeibei on 2021/3/30 14:58
 * @describe: 剪切板工具类
 * <p>
 * ClipboardManager： 表示一个剪贴板
 * ClipData： 剪贴板中保存的所有剪贴数据集（剪贴板可同时复制/保存多条多种数据条目）
 * ClipData.Item： 剪贴数据集中的一个数据条目
 */
public class ClipboardUtils {
    private static final String TAG = "ClipboardUtils";
    private static ClipboardUtils sInstance = null;
    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener;
    private Context mContext;

    public static ClipboardUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new ClipboardUtils();
        }
        return sInstance;
    }

    public ClipboardUtils() {
        mClipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * 实现文本复制功能
     *
     * @param content
     */
    public void copy(String content) {
//      mClipboardManager.setText(content.trim());  方法已过期
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        ClipData clipData = ClipData.newPlainText(null, content);
        // 把数据集设置（复制）到剪贴板
        mClipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 获取系统剪贴板内容
     *
     * @return
     */
    public String getClipContent() {
//        CharSequence text = mClipboardManager.getText(); 方法已过期
        if (mClipboardManager != null) {
            if (mClipboardManager.hasPrimaryClip() && mClipboardManager.getPrimaryClip().getItemCount() > 0) {
                // 获取复制、剪切的文本内容
                CharSequence text = mClipboardManager.getPrimaryClip().getItemAt(0).getText();
                String textS = String.valueOf(text);
                if (!TextUtils.isEmpty(textS)) {
                    return textS;
                }
            }
        }
        return "";
    }

    /**
     * 清空剪贴板内容
     */
    public void clearClipboard() {
        if (mClipboardManager != null) {
            try {
                mClipboardManager.setPrimaryClip(mClipboardManager.getPrimaryClip());
                mClipboardManager.setText(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 注册剪切板复制、剪切事件监听
     */
    public void registerClipListener(ValueCallBack valueCallBack) {
        mOnPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String clipContent = getClipContent();
                valueCallBack.onSuccess(clipContent);
            }
        };
        mClipboardManager.addPrimaryClipChangedListener(mOnPrimaryClipChangedListener);
    }


    public void release() {
        if (mClipboardManager != null && mOnPrimaryClipChangedListener != null) {
            mClipboardManager.removePrimaryClipChangedListener(mOnPrimaryClipChangedListener);
        }
    }

    public interface ValueCallBack {
        void onSuccess(String value);
    }
}
