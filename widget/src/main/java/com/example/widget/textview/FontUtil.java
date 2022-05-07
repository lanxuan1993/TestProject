package com.example.widget.textview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

public class FontUtil {

    public static final String FONT_REGULAR_PATH = "fonts/Lato-Regular.ttf";
    public static final String FONT_BOLD_PATH = "fonts/Lato-Bold.ttf";
    public static final String FONT_ITALIC_PATH = "fonts/Lato-Italic.ttf";
    public static final String FONT_CARTER_ONE_PATH = "fonts/CarterOne.ttf";

    public static Typeface typeface_regular = null;//常规字体
    public static Typeface typeface_bold = null;//粗体
    public static Typeface typeface_italic = null;//斜体
    public static Typeface typeface_carter_one = null;//carterOne字体

    public static void setCustomFont(TextView textView, Context ctx) {

        try {
            if (typeface_regular == null)
                typeface_regular = Typeface.createFromAsset(ctx.getAssets(), FONT_REGULAR_PATH);
            if (typeface_bold == null)
                typeface_bold = Typeface.createFromAsset(ctx.getAssets(), FONT_BOLD_PATH);
            if (typeface_italic == null)
                typeface_italic = Typeface.createFromAsset(ctx.getAssets(), FONT_ITALIC_PATH);

            Typeface tf = textView.getTypeface();
            if (tf == null) {
                textView.setTypeface(typeface_regular);
            }
            if (tf != null && tf.getStyle() == Typeface.ITALIC) {
                textView.setTypeface(typeface_italic);
            } else {
                int style = tf.getStyle();
                if (style == Typeface.BOLD)
                    textView.setTypeface(typeface_bold);
                else textView.setTypeface(typeface_regular);
            }
        } catch (Exception e) {
            Log.e(ctx.getClass().getName(), "Unable to load typeface: " + e.getMessage());
        }

    }

    public static void setCarterOneFont(TextView textView, Context ctx) {
        try {
            if (textView == null || ctx == null) {
                return;
            }
            if (typeface_carter_one == null) {
                typeface_carter_one = Typeface.createFromAsset(ctx.getAssets(), FONT_CARTER_ONE_PATH);
            }
            textView.setTypeface(typeface_carter_one);
        } catch (Exception e) {
            Log.e(ctx.getClass().getName(), "Unable to load typeface: " + e.getMessage());
        }
    }

}
