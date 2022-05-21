package com.example.tools.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    /**
     * 判断手机号
     *
     * @param mobiles 手机号码
     * @return boolean
     */
    public static boolean isMobileNo(String mobiles) {
        String regx = "^1\\d{10}$";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断密码
     *
     * @param psw 密码由8-20位数字、字母组成
     * @return
     */
    public static boolean isPassword(String psw) {
        String regx = "^(?![a-zA-z]+$)(?!\\d+$)[a-zA-Z\\d]{8,20}$";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(psw);
        return m.matches();
    }

    /**
     * 判断密码(兼容旧客户的登录密码)
     *
     * @param psw 密码由6-20位数字、字母组成
     * @return
     */
    public static boolean isLoginPassword(String psw) {
        String regx = "^(?![a-zA-z]+$)(?!\\d+$)[a-zA-Z\\d]{6,20}$";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(psw);
        return m.matches();
    }

    /**
     * 校验验证码
     *
     * @param code 4位数字
     * @return
     */
    public static boolean isYzmCode(String code) {
        String regx = "(^[0-9]{4}$)";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(code);
        return m.matches();
    }

    /**
     * 校验验证码
     *
     * @param code 6位数字
     * @return
     */
    public static boolean isSmsCode(String code) {
        String regx = "(^[0-9]{4,6}$)";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(code);
        return m.matches();
    }

    /**
     * 获取139****234格式的手机号
     *
     * @param phoneS
     */
    public static String getEncryptPhone(String phoneS) {
        if (!TextUtils.isEmpty(phoneS) && phoneS.length() == 11) {
            String startS = phoneS.substring(0, 3);
            String endS = phoneS.substring(phoneS.length() - 3, phoneS.length());
            return startS + "****" + endS;
        }
        return "";
    }

    /**
     * 校验版本号
     *
     * @param versionCode
     * @return
     */
    public static boolean isVersionCode(String versionCode) {
        String regx = "^\\d+\\.\\d+\\.\\d+$";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(versionCode);
        return m.matches();
    }

    /**
     * 判断身份证
     *
     * @param certificate 身份证号码
     * @return boolean
     */
    public static boolean isCertificateNo(String certificate) {
        String regx = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        Pattern p = Pattern.compile(regx);
        Matcher m = p.matcher(certificate);
        return m.matches();
    }

    /**
     * 判断是否为奇数
     *
     * @param s
     * @return
     */
    public static boolean isOdd(String s) {
        int num = Integer.valueOf(s).intValue();
        return isOdd(num);
    }


    /**
     * 判断是否为奇数
     *
     * @param num
     * @return
     */
    public static boolean isOdd(int num) {
        if (num % 2 == 1) {   //是奇数
            return true;
        }
        return false;
    }

    /**
     * 去除空格,回车,换行符,制表符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            if (m.find()) {
                dest = m.replaceAll("");
            }
        }
        return dest;
    }


    /**
     * 设置EditText输入手机号自动空格
     * (注:需求是130 1234 4567，中间第4个数字和第8个数字空格前面加空格)
     *
     * @param s
     */
    public static String setPhoneBlank(String s) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return s;
        }
    }

    /**
     * 监听onTextChanged时使用
     *
     * @param s
     * @param start
     * @param before
     * @param count
     * @throws Exception
     */
    public static void setPhoneBlank(EditText editText, CharSequence s,
                                     int start, int before, int count) throws Exception {
        if (s == null || s.length() == 0) return;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }

        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if (sb.charAt(start) == ' ') {
                if (before == 0) {
                    index++;
                } else {
                    index--;
                }
            } else {
                if (before == 1) {
                    index--;
                }
            }
            editText.setText(sb.toString());
            editText.setSelection(index);
        }

        String content = replaceBlank(s.toString());
        if (content.length() == 11) {
            editText.setSelection(s.length());
        }
    }


}
