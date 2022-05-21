package com.example.tools.utils;

import java.util.Locale;

public class LanguageUtils {

    public static String getDeviceLanguage(){
        String language = Locale.getDefault().getLanguage();
        return language;
    }
}
