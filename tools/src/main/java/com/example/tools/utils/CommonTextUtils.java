package com.example.tools.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public class CommonTextUtils {

    /**
     * 粘贴文本
     *
     * @param txt
     */
    public static void copyText(Context context, String txt) {
        if (TextUtils.isEmpty(txt)) {
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, txt);
        clipboard.setPrimaryClip(clipData);
    }

    /**
     * Html.fromHtml()，这个方法是Android中专门用来解析HTML格式的一个方法，
     * 我们可以将任意的HTML格式下的代码通过此方法解析，最后得到我们需要的结果
     * <p>
     * String str = "恭喜您！您的手机跑分为<font color='#F50057'><big><big><big>888888分</big></big></big></font>，已经超过全国<font color='#00E676'><big><big><big>99%</big></big></big></font>的Android手机。";
     * <p>
     * tv.setText(Html.fromHtml(str));
     *
     * @param htmlText
     */
    public static void setHtmlText(String htmlText) {
        Spanned ss = Html.fromHtml(htmlText);
    }


    public void setOnTextChangedListener(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    StringUtils.setPhoneBlank(editText, s, start, before, count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
