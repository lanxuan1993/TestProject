package com.example.mine.permission;

import android.app.Activity;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    public static void requestPermission(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, 100);
    }
}
