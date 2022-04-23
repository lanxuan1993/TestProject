package com.example.tools.language;

import java.util.Locale;

public class LanguageUtils {

    public static String getDeviceLanguage(){
        String language = Locale.getDefault().getLanguage();
        return language;
    }
}
