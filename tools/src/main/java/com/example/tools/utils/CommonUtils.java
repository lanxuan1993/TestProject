package com.example.tools.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * author: zhaobeibei
 * created on: 2019/12/24
 * description:公共工具类
 */
public final class CommonUtils {
    /**
     * context转activity
     *
     * @param cont
     * @return
     */
    public static Activity scanForActivity(Context cont) {
        if (cont == null) return null;
        else if (cont instanceof Activity) return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }


    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : list) {
            String runningName = service.service.getClassName();
            if (serviceClass.getName().equals(runningName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否安装了微博
     *
     * @param context
     * @return
     */
    public static boolean isWeiboInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.sina.weibo")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否安装了微信
     *
     * @param context
     * @return
     */
    public static boolean isWeixinInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否安装了QQ
     *
     * @param context
     * @return
     */
    public static boolean isQQClientInstalled(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equalsIgnoreCase("com.tencent.qqlite")
                        || pn.equalsIgnoreCase("com.tencent.mobileqq")
                        || pn.equalsIgnoreCase("com.tencent.minihd.qq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 快速点击
     *
     * @return
     */
    private static final int MIN_DELAY_TIME = 1000;  // 两次点击间隔不能少于1000ms
    private static long lastTime;
    public static boolean isFastClick() {
        boolean flag = true;
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastTime = currentTime;
        return flag;
    }



    /**
     * 小数点格式化两位
     *
     * @param number
     * @return
     */
    public static String formatNumber(double number) {
        String result = "0.00";
        try {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            result = decimalFormat.format(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 金额格式化(10,000,000.00)
     * <p>
     * 精确到小数点两位
     *
     * @param num
     * @return
     */
    public static String NumberFormat(double num) {
        DecimalFormat format = new DecimalFormat(",##0.00");
        return format.format(num);
    }

    /**
     * 金额格式化(10,000,000.00)
     *
     * @param num
     * @return
     */
    public static String NumberFormatChina(double num) {
        NumberFormat number_format = NumberFormat.getInstance(Locale.CHINA);
        return number_format.format(num);
    }

    /**
     * 货币格式化(元,万元)
     *
     * @param num
     * @return
     */
    public static StringBuffer formatMoney(String num) {
        StringBuffer sb = new StringBuffer();
        BigDecimal b0 = new BigDecimal("100");
        BigDecimal b1 = new BigDecimal("10000");
        BigDecimal b2 = new BigDecimal("100000000");
        BigDecimal b3 = new BigDecimal(num);

        String formatNumStr = "";
        String unit = "";

        // 以万为单位处理
        if (b3.compareTo(b1) == -1) {
            formatNumStr = b3.toString();
        } else if ((b3.compareTo(b1) == 0 && b3.compareTo(b1) == 1)
                || b3.compareTo(b2) == -1) {
            unit = "万";

            formatNumStr = b3.divide(b1).toString();
        } else if (b3.compareTo(b2) == 0 || b3.compareTo(b2) == 1) {
            unit = "亿";
            formatNumStr = b3.divide(b2).toString();

        }
        if (!"".equals(formatNumStr)) {
            int i = formatNumStr.indexOf(".");
            if (i == -1) {
                sb.append(formatNumStr).append(unit);
            } else {
                i = i + 1;
                String v = formatNumStr.substring(i, i + 1);
                if (!v.equals("0")) {
                    sb.append(formatNumStr.substring(0, i + 1)).append(unit);
                } else {
                    sb.append(formatNumStr.substring(0, i - 1)).append(unit);
                }
            }
        }
        if (sb.length() == 0)
            return sb.append("0");
        return sb;
    }

    /**
     * 获取时间段
     * 凌晨0：00－6：00时显示凌晨，
     * 上午6：00-12：00显示上午，
     * 中午12：00-14：00显示中午,
     * 下午14：00-18:00显示下午，
     * 晚上18：00-24：00显示晚上
     */
    public static String getTimePeriod() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a < 6) {
            return "凌晨";
        }
        if (a >= 6 && a < 12) {
            return "上午";
        }
        if (a >= 12 && a < 14) {
            return "中午";
        }
        if (a >= 14 && a < 18) {
            return "下午";
        }
        if (a >= 18 && a < 24) {
            return "晚上";
        }
        return null;
    }

    /**
     * 将16进制的颜色字符串转为rgb颜色
     *
     * @param hex
     * @return
     */
    public static int hexStrToColor(String hex) {
        int result = 0;
        if (hex != null && !hex.isEmpty()) {
            if (hex.charAt(0) == '#') {
                hex = hex.substring(1);
            }
            // No alpha, that's fine, we will just attach ff.
            if (hex.length() < 8) {
                hex += "ff";
            }
            result = (int) Long.parseLong(hex, 16);
            // Almost done, but Android color code is in form of ARGB instead of
            // RGBA, so we gotta shift it a bit.
            int alpha = (result & 0xff) << 24;
            result = result >> 8 & 0xffffff | alpha;
        }
        return result;
    }


    public static void startActivity(Context context, Class<?> targetClass, boolean isFinish) {
        Activity activity = CommonUtils.scanForActivity(context);
        Intent intent = new Intent(context, targetClass);
        context.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void startActivity(Context context, Class<?> targetClass, Bundle bundle) {
        Intent i = new Intent(context, targetClass);
        if (bundle != null) {
            i.putExtras(bundle);
        }
        context.startActivity(i);
    }

}
