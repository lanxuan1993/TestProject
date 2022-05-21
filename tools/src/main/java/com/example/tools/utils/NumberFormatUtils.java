package com.example.tools.utils;

import java.text.DecimalFormat;

/**
 * 数字格式化工具类
 * @Author ZhaoBeibei
 */
public class NumberFormatUtils {

    /**
     * 小数点格式化两位
     *
     * @param number
     * @return
     */
    public static String formatDecimal(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String result = decimalFormat.format(number);
        return result;
    }

}
