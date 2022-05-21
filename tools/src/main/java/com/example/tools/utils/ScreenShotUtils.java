package com.example.tools.utils;

import android.app.Activity;
import android.view.WindowManager;

public class ScreenShotUtils {

    private static void disableScreenShot(Activity activity, boolean isDisabled) {
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (isDisabled) {
                            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                        } else {
                            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
