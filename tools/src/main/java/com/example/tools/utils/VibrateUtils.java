package com.example.tools.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;

public class VibrateUtils {
    public static void startShortVibrate(Activity activity) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);
    }

}
